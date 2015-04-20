package jp.redmine.redmineclient.service;

import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;

public class Sync extends OrmLiteBaseService<DatabaseCacheHelper>{
	private static final String TAG = Sync.class.getSimpleName();
	private RemoteCallbackList<ISyncObserver> mObservers = new RemoteCallbackList<ISyncObserver>();

	private class ExecuteParam {
		public ExecuteParam(ExecuteMethod m, int p){
			method = m;
			priority = p;
		}
		public ExecuteParam(ExecuteMethod m, int p, int c){
			method = m;
			priority = p;
			connection_id = c;
		}
		public ExecuteMethod method;
		public int priority;
		public int connection_id;
		public int offset;
		public int param_int1;
		public long param_long1;
		public String param_string1;
	}
	private PriorityBlockingQueue<ExecuteParam> queue;
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
		private void blessStart(){
			broadcastEvent(mObservers, new BroadCastHandler<ISyncObserver>() {
				@Override
				public void onEvent(ISyncObserver observer, int cnt) throws RemoteException {
					observer.onStart(current_method.getId());
				}
			});
		}
		private void blessStop(){

			broadcastEvent(mObservers, new BroadCastHandler<ISyncObserver>() {
				@Override
				public void onEvent(ISyncObserver observer, int cnt) throws RemoteException {
					observer.onStop(current_method.getId());
				}
			});
		}
		private void blessError(final int status){
			broadcastEvent(mObservers, new BroadCastHandler<ISyncObserver>() {
				@Override
				public void onEvent(ISyncObserver observer, int cnt) throws RemoteException {
					observer.onError(current_method.getId(), status);
				}
			});
		}
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
					blessStart();
					try {
						runCommand(param, handler, errorHandler);
					} catch (SQLException e) {
						errorHandler.onError(e);
					}
					blessStop();
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
						ExecuteParam new_param = new ExecuteParam(param.method, 20, param.connection_id);
						new_param.offset =  param.offset + limit;
						queue.add(new_param);
					}
					break;
				case Issues:
					if(SyncIssue.fetchIssuesByProject(getHelper(), handler, errorHandler,
							param.param_long1, param.offset, limit, false)) {
						ExecuteParam new_param = new ExecuteParam(param.method, 20, param.connection_id);
						new_param.offset =  param.offset + limit;
						queue.add(new_param);
					}
					break;
			}
		}
	}

	interface BroadCastHandler<E> {
		void onEvent(E observer, int cnt) throws RemoteException;
	}
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
