package jp.redmine.redmineclient.fragment;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.RedmineJournalListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueViewDetailForm;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.task.SelectIssueJournalTask;
import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class IssueView extends OrmLiteListFragment<DatabaseCacheHelper> {
	private RedmineJournalListAdapter adapter;
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private RedmineIssueViewDetailForm formDetail;
	private View mHeader;
	private View mFooter;
	private IntentAction mActionListener;
	private OnArticleSelectedListener mListener;
	private TimeEntryList.OnArticleSelectedListener mTimeEntryListener;

	public IssueView(){
		super();
	}

	static public IssueView newInstance(IssueArgument intent){
		IssueView instance = new IssueView();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onDestroyView() {
		cancelTask(true);
		setListAdapter(null);
		super.onDestroyView();
	}
	protected void cancelTask(boolean isForce){
		// cleanup task
		if(task != null && task.getStatus() == Status.RUNNING){
			task.cancel(isForce);
		}
	}
	@Override
	public void onPause() {
		cancelTask(false);
		super.onPause();
	}

	public interface OnArticleSelectedListener {
		public void onIssueList(int connectionid, long projectid);
		public void onIssueSelected(int connectionid, int issueid);
		public void onIssueEdit(int connectionid, int issueid);
		public void onIssueRefreshed(int connectionid, int issueid);
		public void onIssueAdd(int connectionId, long projectId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ActivityInterface){
			ActivityInterface aif = (ActivityInterface)activity;
			mActionListener = aif.getHandler(IntentAction.class);
			mListener = aif.getHandler(OnArticleSelectedListener.class);
			mTimeEntryListener = aif.getHandler(TimeEntryList.OnArticleSelectedListener.class);
		}
		if(mActionListener == null) {
			//setup empty events
			mActionListener = new IntentAction() {
				@Override
				public void issue(int connection, int issueid) {
				}

				@Override
				public boolean url(String url) {
					return false;
				}
			};
		}
		if(mListener == null){
			//setup empty events
			mListener = new OnArticleSelectedListener() {

				@Override
				public void onIssueList(int connectionId, long projectId) {}
				@Override
				public void onIssueSelected(int connectionid, int issueid) {}
				@Override
				public void onIssueEdit(int connectionid, int issueid) {}
				@Override
				public void onIssueRefreshed(int connectionid, int issueid) {}
				@Override
				public void onIssueAdd(int connectionId, long projectId) {}
			};
		}
		if(mTimeEntryListener == null){
			//setup empty events
			mTimeEntryListener = new TimeEntryList.OnArticleSelectedListener() {

				@Override
				public void onTimeEntrySelected(int connectionid, int issueid, int timeentryid) {}
				@Override
				public void onTimeEntryList(int connectionid, int issueid) {}
				@Override
				public void onTimeEntryEdit(int connectionid, int issueid, int timeentryid) {}
				@Override
				public void onTimeEntryAdd(int connectionid, int issueid) {}
			};
		}

	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().addHeaderView(mHeader);
		getListView().addFooterView(mFooter);

		adapter = new RedmineJournalListAdapter(getHelper(),mActionListener);
		setListAdapter(adapter);

		getListView().setFastScrollEnabled(true);

		formDetail = new RedmineIssueViewDetailForm(mHeader);
		formDetail.setupWebView(mActionListener);

		formDetail.linearTimeEntry.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				IssueArgument baseintent = new IssueArgument();
				baseintent.setArgument(getArguments());
				mTimeEntryListener.onTimeEntryList(baseintent.getConnectionId(), baseintent.getIssueId());
			}
		});
		onRefresh(true);

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mHeader = inflater.inflate(R.layout.issueviewdetail,null);
		mFooter = inflater.inflate(R.layout.listview_footer,null);
		mFooter.setVisibility(View.GONE);
		View mFragment = super.onCreateView(inflater, container, savedInstanceState);
		return mFragment;
	}

	protected void onRefresh(boolean isFetch){
		IssueArgument intent = new IssueArgument();
		intent.setArgument(getArguments());
		int connectionid = intent.getConnectionId();

		RedmineIssueModel model = new RedmineIssueModel(getHelper());
		RedmineIssue issue = new RedmineIssue();
		Log.d("SelectDataTask","ParserIssue Start");
		try {
			issue = model.fetchById(connectionid, intent.getIssueId());
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserIssue",e);
		}

		if(issue.getId() == null){
			if(isFetch){
				onFetchRemote();
			}
		} else {
			RedmineTimeEntryModel mTimeEntry = new RedmineTimeEntryModel(getHelper());
			BigDecimal hours = new BigDecimal(0);
			try {
				hours = mTimeEntry.sumByIssueId(connectionid, issue.getIssueId());
			} catch (SQLException e) {
				Log.e("SelectDataTask","ParserIssue",e);
			}
			formDetail.setValue(issue);
			formDetail.setValueTimeEntry(hours);


			adapter.setupParameter(connectionid,issue.getId());
			adapter.notifyDataSetInvalidated();
			adapter.notifyDataSetChanged();

			if(adapter.getCount() < 1 && isFetch){
				onFetchRemote();
			}
		}
	}

	protected void onFetchRemote(){
		if(task != null && task.getStatus() == Status.RUNNING)
			return;
		IssueArgument intent = new IssueArgument();
		intent.setArgument(getArguments());
		task = new SelectDataTask();
		task.execute(intent.getIssueId());
	}

	private class SelectDataTask extends SelectIssueJournalTask{
		public SelectDataTask() {
			super();
			helper = getHelper();
			IssueArgument intent = new IssueArgument();
			intent.setArgument(getArguments());
			int connectionid = intent.getConnectionId();
			ConnectionModel mConnection = new ConnectionModel(getActivity());
			connection = mConnection.getItem(connectionid);
			mConnection.finalize();
		}
		// can use UI thread here
		@Override
		protected void onPreExecute() {
			mFooter.setVisibility(View.VISIBLE);
			if(menu_refresh != null)
				menu_refresh.setEnabled(false);
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(Void v) {
			onRefresh(false);
			onStopped();

			IssueArgument intent = new IssueArgument();
			intent.setArgument(getArguments());
			mListener.onIssueRefreshed(intent.getConnectionId(), intent.getIssueId());
		}
		@Override
		protected void onCancelled() {
			super.onCancelled();
			onStopped();
		}

		protected void onStopped(){
			mFooter.setVisibility(View.GONE);
			if(menu_refresh != null)
				menu_refresh.setEnabled(true);
		}
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate( R.menu.issue_view, menu );
		menu_refresh = menu.findItem(R.id.menu_refresh);
		if(task != null && task.getStatus() == Status.RUNNING)
			menu_refresh.setEnabled(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_refresh:
			{
				onFetchRemote();
				return true;
			}
			case R.id.menu_edit:
			{
				IssueArgument baseintent = new IssueArgument();
				baseintent.setArgument(getArguments());
				mListener.onIssueEdit(baseintent.getConnectionId(), baseintent.getIssueId());
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

}
