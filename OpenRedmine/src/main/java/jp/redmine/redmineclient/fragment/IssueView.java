package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.WebViewActivity;
import jp.redmine.redmineclient.activity.handler.AttachmentActionInterface;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.activity.handler.TimeentryActionInterface;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.adapter.IssueStickyListAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RecentIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.fragment.form.IssueCommentForm;
import jp.redmine.redmineclient.fragment.form.IssueViewForm;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.param.WebArgument;
import jp.redmine.redmineclient.task.SelectIssueJournalPost;
import jp.redmine.redmineclient.task.SelectIssueJournalTask;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class IssueView extends OrmLiteFragment<DatabaseCacheHelper> implements SwipeRefreshLayout.OnRefreshListener {
	private final String TAG = IssueView.class.getSimpleName();
	private IssueStickyListAdapter adapter;
    private StickyListHeadersListView list;
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private View mFooter;
	private WebviewActionInterface mActionListener;
	private IssueActionInterface mListener;
	private TimeentryActionInterface mTimeEntryListener;
	private AttachmentActionInterface mAttachmentListener;
	private IssueViewForm formTitle;
	private IssueCommentForm formComment;

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
        if(list  != null)
            list.setAdapter(null);
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

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = ActivityHandler.getHandler(activity, IssueActionInterface.class);
		mActionListener = ActivityHandler.getHandler(activity, WebviewActionInterface.class);
		mTimeEntryListener = ActivityHandler.getHandler(activity, TimeentryActionInterface.class);
		mAttachmentListener = ActivityHandler.getHandler(activity, AttachmentActionInterface.class);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        list.addFooterView(mFooter);

		adapter = new IssueStickyListAdapter(getHelper(),getActivity(), mActionListener);
        list.setAdapter(adapter);
		
        list.setFastScrollEnabled(true);

		//setup title view
		formTitle = new IssueViewForm(getView());

		//setup comment form
		formComment = new IssueCommentForm(getView());

		onRefresh(true);

		formComment.buttonOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!formComment.Validate())
					return;
				IssueArgument intent = new IssueArgument();
				intent.setArgument(getArguments());

				RedmineConnection connection = ConnectionModel.getItem(getActivity(), intent.getConnectionId());
				RedmineJournal journal = new RedmineJournal();
				journal.setIssueId((long) intent.getIssueId());
				formComment.getValue(journal);

				SelectIssueJournalPost post = new SelectIssueJournalPost(getHelper(), connection){
					private boolean isSuccess = true;
					@Override
					protected void onError(Exception lasterror) {
						isSuccess = false;
						ActivityHelper.toastRemoteError(getActivity(), ActivityHelper.ERROR_APP);
						super.onError(lasterror);
					}
					@Override
					protected void onErrorRequest(int statuscode) {
						isSuccess = false;
						ActivityHelper.toastRemoteError(getActivity(), statuscode);
						super.onErrorRequest(statuscode);
					}
					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						if(mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing())
							mSwipeRefreshLayout.setRefreshing(false);
						if(isSuccess){
							Toast.makeText(getActivity(), R.string.remote_saved, Toast.LENGTH_LONG).show();
							formComment.clear();
						}
					}
				};
				if(mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
					mSwipeRefreshLayout.setRefreshing(true);
				post.execute(journal);
			}
		});


	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	SwipeRefreshLayout mSwipeRefreshLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFooter = inflater.inflate(R.layout.listview_footer,null);
		mFooter.setVisibility(View.GONE);
		View view = inflater.inflate(R.layout.page_issue, container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.layoutSwipeRefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
        list = (StickyListHeadersListView)view.findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Object item = id < 0 ? null : adapter.getItem(position);
                if (item == null) {
                } else if (item instanceof RedmineTimeEntry) {
                    RedmineTimeEntry entry = (RedmineTimeEntry) item;
                    mTimeEntryListener.onTimeEntrySelected(entry.getConnectionId(), entry.getIssueId(), entry.getTimeentryId());
                    return;
                } else if (item instanceof RedmineIssueRelation) {
                    RedmineIssueRelation relation = (RedmineIssueRelation) item;
                    if (relation.getIssue() != null) {
                        mListener.onIssueSelected(relation.getConnectionId(), relation.getIssue().getIssueId());
                        return;
                    }
                } else if (item instanceof RedmineIssue) {
                    RedmineIssue issue = (RedmineIssue) item;
                    if (view.getId() == R.id.textProject && issue.getProject() != null) {
                        mListener.onIssueList(issue.getConnectionId(), issue.getProject().getId());
                        return;
                    }
                } else if (item instanceof RedmineAttachment) {
                    RedmineAttachment attachment = (RedmineAttachment) item;
                    mAttachmentListener.onAttachmentSelected(attachment.getConnectionId(), attachment.getAttachmentId());
                    return;
                }
            }
        });
        return view;
	}


	@Override
	public void onRefresh() {
		onFetchRemote();
	}

	protected void onRefresh(boolean isFetch){
		IssueArgument intent = new IssueArgument();
		intent.setArgument(getArguments());
		RedmineIssueModel mIssue = new RedmineIssueModel(getHelper());
		RecentIssueModel mRecentIssue = new RecentIssueModel(getHelper());
		RedmineIssue issue = null;
		try {
			issue = mIssue.fetchById(intent.getConnectionId(), intent.getIssueId());
			if(issue != null && issue.getId() != null) {
				mRecentIssue.ping(issue);
				mRecentIssue.strip(issue.getProject(), 10);
			}
		} catch (SQLException e) {
			Log.e(TAG,"onRefresh",e);
		}
		if(issue != null && issue.getId() != null){
			formTitle.setValue(issue);
			adapter.setupParameter(intent.getConnectionId(), issue.getId());
			adapter.notifyDataSetChanged();
			if(!issue.getProject().getStatus().isUpdateable()) {
				formComment.hide();
			} else {
				formComment.show();
			}
		}

		if(adapter.getJournalCount() < 1 && isFetch){
			onFetchRemote();
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
			connection = ConnectionModel.getItem(getActivity(), connectionid);
		}
		// can use UI thread here
		@Override
		protected void onPreExecute() {
			mFooter.setVisibility(View.VISIBLE);
			if(menu_refresh != null)
				menu_refresh.setEnabled(false);
			if(mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
				mSwipeRefreshLayout.setRefreshing(true);
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
			if(mSwipeRefreshLayout != null)
				mSwipeRefreshLayout.setRefreshing(false);
		}
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate( R.menu.issue_view, menu );
		inflater.inflate( R.menu.web, menu );
		menu_refresh = menu.findItem(R.id.menu_refresh);
		if(task != null && task.getStatus() == Status.RUNNING)
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
				onFetchRemote();
				return true;
			}
			case R.id.menu_web:
			{
				IssueArgument input = new IssueArgument();
				input.setArgument(getArguments());
				RedmineIssueModel mIssue = new RedmineIssueModel(getHelper());
				RedmineIssue issue = null;
				try {
					issue = mIssue.fetchById(input.getConnectionId(), input.getIssueId());
				} catch (SQLException e) {
					Log.e(TAG,"onRefresh",e);
				}
				WebArgument intent = new WebArgument();
				intent.setIntent(getActivity().getApplicationContext(), WebViewActivity.class);
				intent.importArgument(input);
				intent.setUrl("/issues/"
						+ ((issue == null || issue.getIssueId() == null) ? "" :issue.getIssueId())
						+ ""
				);
				getActivity().startActivity(intent.getIntent());
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

}
