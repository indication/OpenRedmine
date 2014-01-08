package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionEmptyHandler;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineWikiModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineWiki;
import jp.redmine.redmineclient.form.helper.TextileHelper;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.WikiArgument;
import jp.redmine.redmineclient.task.SelectWikiTask;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class WikiDetail extends OrmLiteFragment<DatabaseCacheHelper> {
	private static final String TAG = WikiDetail.class.getSimpleName();
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private TextileHelper webViewHelper;

	private WebviewActionInterface mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ActivityInterface){
			mListener = ((ActivityInterface)activity).getHandler( WebviewActionInterface.class);
		}
		if(mListener == null) {
			//setup empty events
			mListener = new WebviewActionEmptyHandler();
		}

	}
	protected void cancelTask(){
		// cleanup task
		if(task != null && task.getStatus() == AsyncTask.Status.RUNNING){
			task.cancel(true);
		}
	}
	public WikiDetail(){
		super();
	}

	static public WikiDetail newInstance(WikiArgument arg){
		WikiDetail fragment = new WikiDetail();
		fragment.setArguments(arg.getArgument());
		return fragment;
	}

	@Override
	public void onDestroyView() {
		cancelTask();
		super.onDestroyView();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		webViewHelper.setAction(mListener);
		webViewHelper.setup();
		loadWebView(true);

	}

	public void loadWebView(boolean isRefresh){
		RedmineWikiModel model = new RedmineWikiModel(getHelper());
		WikiArgument intent = new WikiArgument();
		intent.setArgument(getArguments());
		try {
			RedmineWiki wiki = model.fetchById(intent.getConnectionId(), intent.getProjectId(), intent.getWikiTitle());
			if(wiki.getId() != null){

				webViewHelper.setContent(intent.getConnectionId(),intent.getProjectId(), wiki.getBody());
			} else if(isRefresh) {
				onRefresh();
			}
		} catch (SQLException e) {
			Log.e(TAG, "loadWebView", e);
		}
	}

	private class SelectDataTask extends SelectWikiTask {
		public SelectDataTask(DatabaseCacheHelper helper, RedmineConnection con, long proj_id){
			super(helper,con,proj_id);
		}

		// can use UI thread here
		@Override
		protected void onPreExecute() {
			if(menu_refresh != null)
				menu_refresh.setEnabled(false);
			if(mPullToRefreshLayout != null && !mPullToRefreshLayout.isRefreshing())
				mPullToRefreshLayout.setRefreshing(true);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(Void b) {
			loadWebView(false);
			if(menu_refresh != null)
				menu_refresh.setEnabled(true);
			if(mPullToRefreshLayout != null)
				mPullToRefreshLayout.setRefreshComplete();
		}

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.webview, container, false);
		WebView mWebView = (WebView) view.findViewById(R.id.webView);
		webViewHelper = new TextileHelper(mWebView);
		return view;
	}

	private PullToRefreshLayout mPullToRefreshLayout;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// This is the View which is created by ListFragment
		ViewGroup viewGroup = (ViewGroup) view;

		// We need to create a PullToRefreshLayout manually
		mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

		// We can now setup the PullToRefreshLayout
		ActionBarPullToRefresh.from(getActivity())
				// We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
				.insertLayoutInto(viewGroup)

				// We need to mark the ListView and it's Empty View as pullable
				// This is because they are not dirent children of the ViewGroup
				.theseChildrenArePullable(R.id.webView)

				// We can now complete the setup as desired
				.listener(new OnRefreshListener() {
					@Override
					public void onRefreshStarted(View view) {
						onRefresh();
					}
				})
				.setup(mPullToRefreshLayout);
	}

	protected void onRefresh(){
		if(task != null && task.getStatus() == AsyncTask.Status.RUNNING){
			return;
		}
		WikiArgument intent = new WikiArgument();
		intent.setArgument(getArguments());
		int id = intent.getConnectionId();
		ConnectionModel mConnection = new ConnectionModel(getActivity());
		RedmineConnection connection = mConnection.getItem(id);
		mConnection.finalize();
		task = new SelectDataTask(getHelper(), connection, (long)intent.getProjectId());
		task.execute(intent.getWikiTitle());
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh, menu);
		menu_refresh = menu.findItem(R.id.menu_refresh);
		if(task != null && task.getStatus() == AsyncTask.Status.RUNNING)
			menu_refresh.setEnabled(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_refresh:
			{
				this.onRefresh();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
