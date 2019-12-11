package jp.redmine.redmineclient.fragment;

import android.app.FragmentBreadCrumbs;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import android.support.v4.widget.ListFragmentSwipeRefreshLayout;
import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.WebViewActivity;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineWikiModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineWiki;
import jp.redmine.redmineclient.form.helper.WebViewHelper;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.fragment.helper.SwipeRefreshLayoutHelper;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.WebArgument;
import jp.redmine.redmineclient.param.WikiArgument;
import jp.redmine.redmineclient.task.SelectWikiTask;

public class WikiDetail extends OrmLiteFragment<DatabaseCacheHelper> implements SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = WikiDetail.class.getSimpleName();
	private SelectWikiTask task;
	private MenuItem menu_refresh;
	private WebViewHelper webViewHelper;
	private WebView webView;
	SwipeRefreshLayout mSwipeRefreshLayout;

	private WebviewActionInterface mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
		webView.destroy();
		super.onDestroyView();
	}

	@Override
	public void onLowMemory() {
		webView.freeMemory();
		super.onLowMemory();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListener = ActivityHandler.getHandler(getActivity(), WebviewActionInterface.class);
		webViewHelper.setAction(mListener);
		webViewHelper.setup(webView);
		loadWebView(true);

	}

	public void loadWebView(boolean isRefresh){
		RedmineWikiModel model = new RedmineWikiModel(getHelper());
		WikiArgument intent = new WikiArgument();
		intent.setArgument(getArguments());
		RedmineConnection connection = ConnectionModel.getItem(getActivity(), intent.getConnectionId());
		try {
			RedmineWiki wiki = model.fetchById(intent.getConnectionId(), intent.getProjectId(), intent.getWikiTitle());
			if(!TextUtils.isEmpty(wiki.getTitle())){
				ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
				if (actionBar != null)
					actionBar.setTitle(wiki.getTitle());
			}
			StringBuilder content = new StringBuilder();
			if(isRefresh && TextUtils.isEmpty(wiki.getBody())) {
				onRefresh();
			} else {
				if(!TextUtils.isEmpty(wiki.getParent())) {
					content.append("[[");
					content.append(wiki.getParent());
					content.append("]]\n\n");
				}
				content.append(wiki.getBody());
			}
			webViewHelper.setContent(webView, connection.getWikiType(), intent.getConnectionId(),intent.getProjectId(), content.toString());
		} catch (SQLException e) {
			Log.e(TAG, "loadWebView", e);
		}
	}

	@Override
	public void onPause() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			webView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			webView.onResume();
		super.onResume();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_webview, container, false);
		webView = view.findViewById(R.id.webView);
		webViewHelper = new WebViewHelper();
		ListFragmentSwipeRefreshLayout.ViewRefreshLayout result
				= ListFragmentSwipeRefreshLayout.inject(container, view);
		mSwipeRefreshLayout = result.layout;
		SwipeRefreshLayoutHelper.setEvent(mSwipeRefreshLayout, this);
		return result.parent;
	}

	public void onRefresh(){
		if(task != null && task.getStatus() == AsyncTask.Status.RUNNING){
			return;
		}
		WikiArgument intent = new WikiArgument();
		intent.setArgument(getArguments());
		int id = intent.getConnectionId();
		RedmineConnection connection = ConnectionModel.getItem(getActivity(), id);
		task = new SelectWikiTask(getHelper(), connection, (long)intent.getProjectId());
		task.setupEventWithRefresh(null, menu_refresh, mSwipeRefreshLayout, (data) -> loadWebView(false));
		task.execute(intent.getWikiTitle());
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.refresh, menu);
		inflater.inflate( R.menu.web, menu );
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
			case R.id.menu_web:
			{
				RedmineWikiModel model = new RedmineWikiModel(getHelper());
				WikiArgument input = new WikiArgument();
				input.setArgument(getArguments());
				RedmineWiki wiki = null;
				try {
					wiki = model.fetchById(input.getConnectionId(), input.getProjectId(), input.getWikiTitle());
				} catch (SQLException e) {
					Log.e(TAG,"onOptionsItemSelected",e);
					return false;
				}
				WebArgument intent = new WebArgument();
				intent.setIntent(getActivity().getApplicationContext(), WebViewActivity.class);
				intent.importArgument(input);
				intent.setUrl("/wiki/"
						+ ((wiki == null || wiki.getTitle() == null) ? "" :wiki.getTitle())
						+ ""
				);
				getActivity().startActivity(intent.getIntent());
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
