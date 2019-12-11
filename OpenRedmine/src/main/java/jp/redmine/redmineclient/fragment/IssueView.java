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
import jp.redmine.redmineclient.fragment.helper.SwipeRefreshLayoutHelper;
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
	private SelectIssueJournalTask task;
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

	private boolean isSuccess = true;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Activity activity = getActivity();
		mListener = ActivityHandler.getHandler(activity, IssueActionInterface.class);
		mActionListener = ActivityHandler.getHandler(activity, WebviewActionInterface.class);
		mTimeEntryListener = ActivityHandler.getHandler(activity, TimeentryActionInterface.class);
		mAttachmentListener = ActivityHandler.getHandler(activity, AttachmentActionInterface.class);

        list.addFooterView(mFooter);

		adapter = new IssueStickyListAdapter(getHelper(),activity, mActionListener);
        list.setAdapter(adapter);
		
        list.setFastScrollEnabled(true);

		//setup title view
		formTitle = new IssueViewForm(getView());

		//setup comment form
		formComment = new IssueCommentForm(getView());

		onRefresh(true);

		formComment.buttonOK.setOnClickListener(v -> {
			if(!formComment.Validate())
				return;
			IssueArgument intent = new IssueArgument();
			intent.setArgument(getArguments());

			RedmineConnection connection = ConnectionModel.getItem(getActivity(), intent.getConnectionId());
			RedmineJournal journal = new RedmineJournal();
			journal.setIssueId((long) intent.getIssueId());
			formComment.getValue(journal);

			SelectIssueJournalPost post = new SelectIssueJournalPost(getHelper(), connection);
			isSuccess = true;
			post.setOnErrorHandler((lasterror) -> {
				isSuccess = false;
				ActivityHelper.toastRemoteError(getActivity(), ActivityHelper.ERROR_APP);
			});
			post.setOnErrorRequestHandler((statuscode) -> {
				isSuccess = false;
				ActivityHelper.toastRemoteError(getActivity(), statuscode);
			});
			task.setupEventWithRefresh(mFooter, menu_refresh, mSwipeRefreshLayout,  (data) ->{
				if(isSuccess){
					Toast.makeText(getActivity().getApplicationContext(), R.string.remote_saved, Toast.LENGTH_LONG).show();
					formComment.clear();
				}
			});
			post.execute(journal);
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
		mSwipeRefreshLayout = view.findViewById(R.id.layoutSwipeRefresh);
		SwipeRefreshLayoutHelper.setEvent(mSwipeRefreshLayout, this);
        list = view.findViewById(R.id.list);
        list.setOnItemClickListener((adapterView, currentView, position, id) -> {

                Object item = id < 0 ? null : adapter.getItem(position);
                if (item == null) {
                } else if (item instanceof RedmineTimeEntry) {
                    RedmineTimeEntry entry = (RedmineTimeEntry) item;
                    mTimeEntryListener.onTimeEntrySelected(entry.getConnectionId(), entry.getIssueId(), entry.getTimeentryId());
                } else if (item instanceof RedmineIssueRelation) {
                    RedmineIssueRelation relation = (RedmineIssueRelation) item;
                    if (relation.getIssue() != null) {
                        mListener.onIssueSelected(relation.getConnectionId(), relation.getIssue().getIssueId());
                    }
                } else if (item instanceof RedmineIssue) {
                    RedmineIssue issue = (RedmineIssue) item;
                    if (currentView.getId() == R.id.textProject && issue.getProject() != null) {
                        mListener.onIssueList(issue.getConnectionId(), issue.getProject().getId());
                    }
                } else if (item instanceof RedmineAttachment) {
                    RedmineAttachment attachment = (RedmineAttachment) item;
                    mAttachmentListener.onAttachmentSelected(attachment.getConnectionId(), attachment.getAttachmentId());
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
			//set title
			getActivity().setTitle(getString(R.string.issue_id_subject, issue.getIssueId(), issue.getSubject()));
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

		RedmineConnection connection = ConnectionModel.getItem(getActivity(), intent.getConnectionId());
		task = new SelectIssueJournalTask(getHelper(), connection, null);
		task.setOnPreExecute(() -> {
			mFooter.setVisibility(View.VISIBLE);
			if(menu_refresh != null)
				menu_refresh.setEnabled(false);
			SwipeRefreshLayoutHelper.setRefreshingPost(mSwipeRefreshLayout, true);
		});
		task.setOnPostExecute(data -> {
			onRefresh(false);
			mFooter.setVisibility(View.GONE);
			if(menu_refresh != null)
				menu_refresh.setEnabled(true);
			SwipeRefreshLayoutHelper.setRefreshingPost(mSwipeRefreshLayout, false);

			IssueArgument intentIssue = new IssueArgument();
			intentIssue.setArgument(getArguments());
			mListener.onIssueRefreshed(intentIssue.getConnectionId(), intentIssue.getIssueId());
		});
		task.execute(intent.getIssueId());
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
