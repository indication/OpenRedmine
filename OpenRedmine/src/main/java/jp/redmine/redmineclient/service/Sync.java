package jp.redmine.redmineclient.service;

import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;

public class Sync extends OrmLiteBaseService<DatabaseCacheHelper>{
	private static final String TAG = Sync.class.getSimpleName();
	private RemoteCallbackList<ISyncObserver> mObservers = new RemoteCallbackList<ISyncObserver>();

	private PriorityBlockingQueue<ExecuteParam> queue;
	/**
	 * Handler from activity to service
	 * Only make a request to event loop
	 */
	private final ISync.Stub mBinder = new ISync.Stub(){

		@Override
		public void fetchMaster(int connection_id) throws RemoteException {
			queue.add(new ExecuteParam(ExecuteMethod.Master, 0, connection_id));
		}

		@Override
		public void fetchProject(int connection_id) throws RemoteException {
			queue.add(new ExecuteParam(ExecuteMethod.Project, 0, connection_id));
		}

		@Override
		public void fetchNews(int connection_id, long project_id) throws RemoteException {
			ExecuteParam param = new ExecuteParam(ExecuteMethod.News, 0, connection_id);
			param.param_long1 = project_id;
			queue.add(param);
		}

		@Override
		public void fetchIssuesByProject(int connection_id, long project_id, int offset, boolean isFetchAll) throws RemoteException {
			ExecuteParam param = new ExecuteParam(ExecuteMethod.Issues, 0, connection_id);
			param.param_long1 = project_id;
			param.param_bool1 = isFetchAll;
			param.offset = offset;
			queue.add(param);
		}

		@Override
		public void fetchIssuesByFilter(int connection_id, int filter_id, int offset, boolean isFetchAll) throws RemoteException {
			ExecuteParam param = new ExecuteParam(ExecuteMethod.IssueFilter, 0, connection_id);
			param.param_int1 = filter_id;
			param.param_bool1 = isFetchAll;
			param.offset = offset;
			queue.add(param);
		}

		@Override
		public void fetchIssue(int connection_id, int issue_id) throws RemoteException {
			ExecuteParam param = new ExecuteParam(ExecuteMethod.Issue, 0, connection_id);
			param.param_int1 = issue_id;
			param.offset = 0;
			queue.add(param);
			ExecuteParam param2 = new ExecuteParam(ExecuteMethod.TimeEntries, 0, connection_id);
			param2.param_int1 = issue_id;
			param2.offset = 0;
			queue.add(param2);
		}

		@Override
		public void fetchWikiByProject(int connection_id, long project_id) throws RemoteException {
			ExecuteParam param = new ExecuteParam(ExecuteMethod.Wiki, 0, connection_id);
			param.param_long1 = project_id;
			param.offset = 0;
			queue.add(param);
		}

		@Override
		public void fetchWiki(int connection_id, long project_id, String title) throws RemoteException {
			ExecuteParam param = new ExecuteParam(ExecuteMethod.Wiki, 0, connection_id);
			param.param_long1 = project_id;
			param.param_string1 = title;
			param.offset = 0;
			queue.add(param);
		}

		@Override
		public void setObserver(ISyncObserver observer) throws RemoteException {
			mObservers.register(observer);
		}

		@Override
		public void removeObserver(ISyncObserver observer) throws RemoteException {
			mObservers.unregister(observer);
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private FetcherThread thread;
	@Override
	public void onCreate() {
		super.onCreate();
		thread = new FetcherThread();
		queue = new PriorityBlockingQueue<>(5, new Comparator<ExecuteParam>() {
			@Override
			public int compare(ExecuteParam lhs, ExecuteParam rhs) {
				return lhs.priority - rhs.priority;
			}
		});
		thread.start();
	}

	@Override
	public void onDestroy() {
		queue.clear();
		if(thread != null) {
			queue.add(new ExecuteParam(ExecuteMethod.Halt, -1));
			try {
				thread.join();
			} catch (InterruptedException e) {
				Log.e(TAG, "onDestroy", e);
			}
		}
		super.onDestroy();
	}

	class FetcherThread extends Thread {
		volatile ExecuteMethod current_method = ExecuteMethod.Halt;

		/**
		 * Notify start to activity
		 */
		private void blessStart(final ExecuteMethod param){
			broadcastEvent(mObservers, new BroadCastHandler<ISyncObserver>() {
				@Override
				public void onEvent(ISyncObserver observer, int cnt) throws RemoteException {
					if(!observer.isNotify(current_method.getId()))
						return;
					observer.onStart(param.getId());
				}
			});
		}

		/**
		 * Notify stop to activity
		 */
		private void blessStop(final ExecuteMethod param){

			broadcastEvent(mObservers, new BroadCastHandler<ISyncObserver>() {
				@Override
				public void onEvent(ISyncObserver observer, int cnt) throws RemoteException {
					if(!observer.isNotify(current_method.getId()))
						return;
					observer.onStop(param.getId());
				}
			});
		}

		/**
		 * Notify stop to activity
		 */
		private void blessDataChanged(final ExecuteMethod param){

			broadcastEvent(mObservers, new BroadCastHandler<ISyncObserver>() {
				@Override
				public void onEvent(ISyncObserver observer, int cnt) throws RemoteException {
					if(!observer.isNotify(current_method.getId()))
						return;
					observer.onChanged(param.getId());
				}
			});
		}

		/**
		 * Notify error to activity
		 * @param status HTTP Error code including custom code(6xx)
		 */
		private void blessError(final int status){
			broadcastEvent(mObservers, new BroadCastHandler<ISyncObserver>() {
				@Override
				public void onEvent(ISyncObserver observer, int cnt) throws RemoteException {
					if(!observer.isNotify(current_method.getId()))
						return;
					observer.onError(current_method.getId(), status);
				}
			});
		}

		/**
		 * Event loop for service
		 */
		@Override
		public void run() {
			ExecuteParam param = null;
			SelectDataTaskRedmineConnectionHandler handler = null;

			Fetcher.ContentResponseErrorHandler errorHandler = new Fetcher.ContentResponseErrorHandler() {
				@Override
				public void onErrorRequest(int status) {
					blessError(status);
					Log.i(TAG, "onErrorRequest status:" + status);
				}

				@Override
				public void onError(Exception e) {
					blessError(600);
					Log.e(TAG,"onError",e);
				}
			};
			List<ExecuteMethod> notification = new ArrayList<>();
			// Event loop
			while(true){
				try {
					//connection handler is null then wait forever
					param = handler == null ? queue.take() : queue.poll(2L, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					Log.e(TAG,"queue.poll", e);
				}
				if(param == null){
					if(handler != null) {
						handler.close();
						handler = null;
					}
					for(ExecuteMethod method : notification){
						blessStop(method);
					}
					notification.clear();
					continue;
				} else {
					//if the requested connection_id is deference, reconnect.
					if(handler != null && handler.getConnection().getId() != param.connection_id){
						handler.close();
						handler = null;
					}
					if(handler == null) {
						RedmineConnection connection = ConnectionModel.getConnectionItem(getContentResolver(), param.connection_id);
						if(connection == null){
							continue;
						}
						handler = new SelectDataTaskRedmineConnectionHandler(connection);
					}
				}
				current_method = param.method;
				if(current_method == ExecuteMethod.Halt) {
					if(handler != null) {
						handler.close();
						handler = null;
					}
					return; //stop loop
				} else {
					if(!notification.contains(current_method)){
						blessStart(current_method);
						notification.add(current_method);
					}
					try {
						runCommand(param, handler, errorHandler);
					} catch (SQLException e) {
						errorHandler.onError(e);
					}
					blessDataChanged(current_method);
				}
			}

		}

		private void runCommand(ExecuteParam param
				, SelectDataTaskRedmineConnectionHandler handler
				, Fetcher.ContentResponseErrorHandler errorHandler
		) throws SQLException {

			int limit = 20;
			switch (param.method){
				case Halt:
					break;
				case Master:
					SyncMaster.fetchStatus(getHelper(), handler, errorHandler);
					SyncMaster.fetchTracker(getHelper(), handler, errorHandler);
					SyncMaster.fetchPriority(getHelper(), handler, errorHandler);
					SyncMaster.fetchTimeEntryActivity(getHelper(), handler, errorHandler);
					SyncMaster.fetchUsers(getHelper(), handler, errorHandler);
					SyncMaster.fetchCurrentUser(getHelper(), handler, errorHandler);
					break;
				case Project:
					if(SyncProject.fetchProject(getHelper(), handler, errorHandler, param.offset, limit)){
						ExecuteParam new_param = param.Clone();
						new_param.offset =  param.offset + limit;
						queue.add(new_param);
					}
					break;
				case Issues:
					if(SyncIssue.fetchIssuesByProject(getHelper(), handler, errorHandler,
							param.param_long1, param.offset, limit, param.param_bool1)) {
						ExecuteParam new_param = param.Clone();
						if(param.offset == ExecuteMethod.REFRESH_ALL) {
							new_param.offset = limit;
						} else {
							new_param.offset = param.offset + limit;
						}
						queue.add(new_param);
					}
					if(param.offset < 0){
						SyncCategory.fetchCategory(getHelper(), handler, errorHandler, param.param_long1);
						SyncVersion.fetchVersions(getHelper(), handler, errorHandler, param.param_long1);
					}
					break;
				case Issue:
					if(SyncIssue.fetchIssue(getHelper(), handler, errorHandler, param.param_int1)){
						ExecuteParam new_param = param.Clone();
						new_param.offset += limit;
						queue.add(new_param);
					}
					break;
				case TimeEntries:
					if(SyncTimeentry.fetchByIssue(getHelper(), handler, errorHandler, param.param_int1, param.offset, limit)){
						ExecuteParam new_param = param.Clone();
						new_param.offset += limit;
						queue.add(new_param);
					}
					break;
				case IssueFilter:
					if(SyncIssue.fetchIssuesByFilter(getHelper(), handler, errorHandler,
							param.param_int1, param.offset, limit, param.param_bool1)) {
						ExecuteParam new_param = param.Clone();
						if(param.offset == ExecuteMethod.REFRESH_ALL) {
							new_param.offset = limit;
						} else {
							new_param.offset = param.offset + limit;
						}
						queue.add(new_param);
					}
					break;
				case News:
					SyncNews.fetchNews(getHelper(), handler, errorHandler, param.param_long1);
					break;
				case Wiki:
					if(SyncWiki.fetchWiki(getHelper(), handler, errorHandler, param.param_long1, param.param_string1,
							param.offset, limit)){
						ExecuteParam new_param = param.Clone();
						new_param.offset = param.offset + limit;
						queue.add(new_param);
					}
					break;
			}
		}
	}

	interface BroadCastHandler<E> {
		void onEvent(E observer, int cnt) throws RemoteException;
	}

	/**
	 *
	 * @param observers
	 * @param handler
	 */
	static <E extends IInterface> void broadcastEvent(RemoteCallbackList<E> observers, BroadCastHandler<E> handler){
		if(observers == null)
			return;
		int count = observers.beginBroadcast();
		for(int i = 0; i < count ; i++) {
			try {
				handler.onEvent(observers.getBroadcastItem(i), i);
			} catch (RemoteException e) {
				Log.e(TAG, "broadcastEvent", e);
			}
		}
		observers.finishBroadcast();

	}
}
