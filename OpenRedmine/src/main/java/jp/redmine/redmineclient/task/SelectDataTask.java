package jp.redmine.redmineclient.task;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.util.List;

import jp.redmine.redmineclient.fragment.helper.SwipeRefreshLayoutHelper;
import jp.redmine.redmineclient.parser.BaseParser;
import jp.redmine.redmineclient.url.RemoteUrl;

public abstract class SelectDataTask<T,P> extends AsyncTask<P, Integer, T> {
	public interface OnProgressHandler {
		/**
		 * Notify progress on UI thread
		 * @param max total count of the items
		 * @param proc current count of the items
		 */
		void onProgress(int max, int proc);
	}
	private OnProgressHandler progressHandler;
	public void setOnProgressHandler(OnProgressHandler handler){
		progressHandler = handler;
	}
	public interface OnErrorRequestHandler {
		/**
		 * Notify error request on UI thread
		 * @param statuscode http response code
		 */
		void onErrorRequest(int statuscode);
	}
	private OnErrorRequestHandler errorRequestHandler;
	public void setOnErrorRequestHandler(OnErrorRequestHandler handler){
		errorRequestHandler = handler;
	}
	public interface OnErrorHandler {
		void onError(Exception ex);
	}
	private OnErrorHandler errorHandler = (lasterror) -> {Log.e("SelectDataTask", "background", lasterror);};
	public void setOnErrorHandler(OnErrorHandler handler){
		errorHandler = handler;
	}

	public interface OnPostExecute<T> {
		void onPostExecute(T data);
	}
	private OnPostExecute<T> postExecute;
	public void setOnPostExecute(OnPostExecute<T> handler){
		postExecute = handler;
	}
	public interface OnPreExecute {
		void onPreExecute();
	}
	private OnPreExecute preExecute;
	public void setOnPreExecute(OnPreExecute handler){
		preExecute = handler;
	}

	public interface OnItemRefreshed<T> {
		void onItemRefreshed(T data);
	}
	public void setupEventWithRefresh(View footer, MenuItem menuRefresh, SwipeRefreshLayout swipe, OnItemRefreshed itemRefreshed){

		setOnPostExecute((b) ->{
			if (footer != null)
				footer.setVisibility(View.GONE);
			if (itemRefreshed != null)
				itemRefreshed.onItemRefreshed(b);
			if(menuRefresh != null)
				menuRefresh.setEnabled(true);
			SwipeRefreshLayoutHelper.setRefreshing(swipe, false);
		});
		setOnPreExecute(() ->{
			if (footer != null)
				footer.setVisibility(View.VISIBLE);
			if(menuRefresh != null)
				menuRefresh.setEnabled(false);
			SwipeRefreshLayoutHelper.setRefreshing(swipe, true);
		});
	}
	/**
	 * Store the last exception (reference by UI thread)
	 */
	private volatile Exception lasterror;

	interface ProgressKind{
		public int progress = 1;
		public int error = 2;
		public int unknown = 3;
	}

	@Override
	protected void onPostExecute(T t) {
		super.onPostExecute(t);
		if (postExecute != null)
			postExecute.onPostExecute(t);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (preExecute != null)
			preExecute.onPreExecute();
	}

	@Override
	protected final void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		switch(values[0]){
		case ProgressKind.progress:
			if (progressHandler != null)
				progressHandler.onProgress(values[1],values[2]);
			break;
		case ProgressKind.error:
			if (errorRequestHandler != null)
				errorRequestHandler.onErrorRequest(values[1]);
			break;
		case ProgressKind.unknown:
			if (errorHandler != null)
				errorHandler.onError(lasterror);
			break;
		default:
		}
	}

	protected void notifyProgress(int max,int proc){
		super.publishProgress(ProgressKind.progress,max,proc);
	}

	protected void publishErrorRequest(int status){
		super.publishProgress(ProgressKind.error,status);
	}
	protected void publishError(Exception e){
		lasterror = e;
		super.publishProgress(ProgressKind.unknown);
	}

	protected void helperAddItems(ArrayAdapter<T> listAdapter,List<T> items){
		if(items == null)
			return;
		listAdapter.notifyDataSetInvalidated();
		for (T i : items){
			listAdapter.add(i);
		}
		listAdapter.notifyDataSetChanged();
	}

	protected void helperSetupParserStream(InputStream stream,BaseParser<?,?> parser) throws XmlPullParserException{
		Fetcher.setupParserStream(stream, parser);
	}

	protected boolean fetchData(SelectDataTaskRedmineConnectionHandler connectionhandler, RemoteUrl url,SelectDataTaskDataHandler handler){
		return Fetcher.fetchData(connectionhandler, getErrorHandler(), connectionhandler.getUrl(url), handler);
	}
	protected boolean fetchData(SelectDataTaskConnectionHandler connectionhandler,String url,SelectDataTaskDataHandler handler){
		return Fetcher.fetchData(connectionhandler, getErrorHandler(), url, handler);
	}
	protected boolean putData(SelectDataTaskRedmineConnectionHandler connectionhandler,RemoteUrl url,SelectDataTaskDataHandler handler, SelectDataTaskPutHandler puthandler){
		return Fetcher.putData(connectionhandler, getErrorHandler(), connectionhandler.getUrl(url), handler, puthandler);
	}
	protected boolean postData(SelectDataTaskRedmineConnectionHandler connectionhandler,RemoteUrl url,SelectDataTaskDataHandler handler, SelectDataTaskPutHandler puthandler){
		return Fetcher.postData(connectionhandler, getErrorHandler(), connectionhandler.getUrl(url), handler, puthandler);
	}

	protected Fetcher.ContentResponseErrorHandler getErrorHandler(){
		return new Fetcher.ContentResponseErrorHandler() {
			@Override
			public void onErrorRequest(int status) {
				publishErrorRequest(status);
			}

			@Override
			public void onError(Exception e) {
				publishError(e);
			}
		};
	}
}
