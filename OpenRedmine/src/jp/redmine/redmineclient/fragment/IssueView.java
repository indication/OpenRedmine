package jp.redmine.redmineclient.fragment;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteListFragment;

import jp.redmine.redmineclient.IssueEditActivity;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.TimeEntryViewActivity;
import jp.redmine.redmineclient.activity.helper.ActionActivityHelper;
import jp.redmine.redmineclient.adapter.RedmineJournalListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineTimeEntryModel;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueViewDetailForm;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.task.SelectIssueJournalTask;
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

	public IssueView(){
		super();
	}

	@Override
	public void onDestroy() {
		cancelTask();
		super.onDestroy();
	}
	protected void cancelTask(){
		// cleanup task
		if(task != null && task.getStatus() == Status.RUNNING){
			task.cancel(true);
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().addHeaderView(mHeader);
		getListView().addFooterView(mFooter);

		ActionActivityHelper actionhelper = new ActionActivityHelper(getActivity());

		adapter = new RedmineJournalListAdapter(getHelper(),actionhelper);
		setListAdapter(adapter);

		getListView().setFastScrollEnabled(true);

		formDetail = new RedmineIssueViewDetailForm(mHeader);
		formDetail.setupWebView(actionhelper);

		formDetail.linearTimeEntry.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				IssueArgument baseintent = new IssueArgument();
				baseintent.setIntent(getActivity().getIntent());
				IssueArgument intent = new IssueArgument();
				intent.setIntent(getActivity(), TimeEntryViewActivity.class );
				intent.setConnectionId(baseintent.getConnectionId());
				intent.setIssueId(baseintent.getIssueId());
				startActivity( intent.getIntent() );
			}
		});

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

	@Override
	public void onStart() {
		super.onStart();
		onRefresh(true);
	}

	protected void onRefresh(boolean isFetch){
		IssueArgument intent = new IssueArgument();
		intent.setIntent(getActivity().getIntent());
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
		intent.setIntent(getActivity().getIntent());
		task = new SelectDataTask();
		task.execute(intent.getIssueId());
	}

	private class SelectDataTask extends SelectIssueJournalTask{
		public SelectDataTask() {
			super();
			helper = getHelper();
			IssueArgument intent = new IssueArgument();
			intent.setIntent(getActivity().getIntent());
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
			mFooter.setVisibility(View.GONE);
			onRefresh(false);
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
				baseintent.setIntent(getActivity().getIntent());
				IssueArgument intent = new IssueArgument();
				intent.setIntent(getActivity(), IssueEditActivity.class );
				intent.setConnectionId(baseintent.getConnectionId());
				intent.setProjectId(baseintent.getProjectId());
				intent.setIssueId(baseintent.getIssueId());
				startActivity( intent.getIntent() );
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

}
