package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.AttachmentActionEmptyHandler;
import jp.redmine.redmineclient.activity.handler.AttachmentActionInterface;
import jp.redmine.redmineclient.activity.handler.IssueActionEmptyHandler;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.activity.handler.TimeentryActionEmptyHandler;
import jp.redmine.redmineclient.activity.handler.TimeentryActionInterface;
import jp.redmine.redmineclient.activity.handler.WebviewActionEmptyHandler;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.adapter.RedmineIssueViewStickyListHeadersAdapter;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.form.RedmineIssueCommentForm;
import jp.redmine.redmineclient.form.RedmineIssueViewForm;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.task.SelectIssueJournalPost;
import jp.redmine.redmineclient.task.SelectIssueJournalTask;
import se.emilsjolander.stickylistheaders.PtrStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class IssueView extends OrmLiteFragment<DatabaseCacheHelper> {
	private final String TAG = IssueView.class.getSimpleName();
	private RedmineIssueViewStickyListHeadersAdapter adapter;
    private StickyListHeadersListView list;
	private SelectDataTask task;
	private MenuItem menu_refresh;
	private View mFooter;
	private WebviewActionInterface mActionListener;
	private IssueActionInterface mListener;
	private TimeentryActionInterface mTimeEntryListener;
	private AttachmentActionInterface mAttachmentListener;
	private RedmineIssueViewForm formTitle;
	private RedmineIssueCommentForm formComment;
	private ProgressDialog dialog;

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
		if(activity instanceof ActivityInterface){
			ActivityInterface aif = (ActivityInterface)activity;
			mActionListener		= aif.getHandler(WebviewActionInterface.class);
			mListener			= aif.getHandler(IssueActionInterface.class);
			mTimeEntryListener	= aif.getHandler(TimeentryActionInterface.class);
			mAttachmentListener	= aif.getHandler(AttachmentActionInterface.class);
		}
		//setup empty events
		if(mActionListener		== null)	mActionListener		= new WebviewActionEmptyHandler();
		if(mListener			== null)	mListener			= new IssueActionEmptyHandler();
		if(mTimeEntryListener	== null)	mTimeEntryListener	= new TimeentryActionEmptyHandler();
		if(mAttachmentListener	== null)	mAttachmentListener	= new AttachmentActionEmptyHandler();

	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        list.addFooterView(mFooter);

		adapter = new RedmineIssueViewStickyListHeadersAdapter(getHelper(),getActivity(), mActionListener);
        list.setAdapter(adapter);
		
        list.setFastScrollEnabled(true);

		//setup title view
		formTitle = new RedmineIssueViewForm(getView());

		//setup comment form
		formComment = new RedmineIssueCommentForm(getView());

		onRefresh(true);

		formComment.buttonOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!formComment.Validate())
					return;
				IssueArgument intent = new IssueArgument();
				intent.setArgument(getArguments());

				RedmineConnection connection = null;
				ConnectionModel mConnection = new ConnectionModel(getActivity());
				connection = mConnection.getItem(intent.getConnectionId());
				mConnection.finalize();

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
					protected void onPreExecute() {
						dialog.show();
						super.onPreExecute();
					}
					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						if (dialog.isShowing())
							dialog.dismiss();
						if(isSuccess){
							Toast.makeText(getActivity(), R.string.remote_saved, Toast.LENGTH_LONG).show();
							formComment.clear();
						}
					}
				};
				post.execute(journal);
			}
		});

		dialog = new ProgressDialog(getActivity());
		dialog.setMessage(getString(R.string.menu_settings_uploading));

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFooter = inflater.inflate(R.layout.listview_footer,null);
		mFooter.setVisibility(View.GONE);
        View current = inflater.inflate(R.layout.issuedetail, container, false);
        list = (StickyListHeadersListView)current.findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Object item = adapter.getItem(position);
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
        return current;
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
				.theseChildrenArePullable(R.id.list, android.R.id.empty)

				// We can now complete the setup as desired
				.listener(new OnRefreshListener() {
					@Override
					public void onRefreshStarted(View view) {
						onFetchRemote();
					}
				})

				.useViewDelegate(PtrStickyListHeadersListView.class, (PtrStickyListHeadersListView)view.findViewById(R.id.list))
		//.options(...)
		.setup(mPullToRefreshLayout);
	}

	protected void onRefresh(boolean isFetch){
		IssueArgument intent = new IssueArgument();
		intent.setArgument(getArguments());
		RedmineIssueModel mIssue = new RedmineIssueModel(getHelper());
		RedmineIssue issue = null;
		try {
			issue = mIssue.fetchById(intent.getConnectionId(), intent.getIssueId());
		} catch (SQLException e) {
			Log.e(TAG,"onRefresh",e);
		}
		if(issue != null && issue.getId() != null){
			formTitle.setValue(issue);
			adapter.setupParameter(intent.getConnectionId(), issue.getId());
			adapter.notifyDataSetChanged();
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
			if(mPullToRefreshLayout != null && !mPullToRefreshLayout.isRefreshing())
				mPullToRefreshLayout.setRefreshing(true);
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
			if(mPullToRefreshLayout != null)
				mPullToRefreshLayout.setRefreshComplete();
		}
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate( R.menu.issue_view, menu );
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
		}
		return super.onOptionsItemSelected(item);
	}

}
