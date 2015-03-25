package jp.redmine.redmineclient.service;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteBaseService;

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
	private enum ExecuteMethod {
		Halt,
		Flush,
		Master,
		Project,
	}
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
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	class FetcherThread extends Thread {
		@Override
		public void run() {
			ExecuteParam param = null;
			SelectDataTaskRedmineConnectionHandler handler = null;

			Fetcher.ContentResponseErrorHandler errorHandler = new Fetcher.ContentResponseErrorHandler() {
				@Override
				public void onErrorRequest(int status) {
					Log.i("SyncService", "fetchMaster status:" + status);
				}

				@Override
				public void onError(Exception e) {
					Log.e("SyncService","fetchMaster",e);
				}
			};
			while(true){
				try {
					//connection handler is null then wait forever
					param = handler == null ? queue.poll() : queue.poll(2L, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
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
				switch (param.method){
					case Halt:
						if(handler != null) {
							handler.close();
							handler = null;
						}
						return; //stop loop
					case Master:
						SyncMaster.fetchStatus(getHelper(), handler, errorHandler);
						SyncMaster.fetchTracker(getHelper(), handler, errorHandler);
						SyncMaster.fetchPriority(getHelper(), handler, errorHandler);
						SyncMaster.fetchTimeEntryActivity(getHelper(), handler, errorHandler);
						SyncMaster.fetchUsers(getHelper(), handler, errorHandler);
						SyncMaster.fetchCurrentUser(getHelper(), handler, errorHandler);
						break;
					case Project:
						int limit = 20;
						List<RedmineProject> projects = SyncProject.fetchProject(getHelper(), handler, errorHandler, param.offset, limit);
						if(projects.size() >= limit) {
							ExecuteParam new_param = new ExecuteParam(param.method, 20, param.connection_id);
							new_param.offset +=  limit;
							queue.add(new_param);
						}
						break;
				}
			}

		}
	}
}
