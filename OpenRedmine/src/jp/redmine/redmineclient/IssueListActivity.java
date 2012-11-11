package jp.redmine.redmineclient;

import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.adapter.RedmineIssueListAdapter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.intent.IssueIntent;
import jp.redmine.redmineclient.intent.ProjectIntent;
import jp.redmine.redmineclient.model.IssueModel;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IssueListActivity extends Activity
	implements OnScrollListener
	{
	public IssueListActivity(){
		super();
	}

	private IssueModel modelIssue;
	private ArrayAdapter<RedmineIssue> listAdapter;
	private SelectDataTask task;
	private View mFooter;
	private ListView listView;

	@Override
	protected void onDestroy() {
		// cleanup task
		if(task != null && task.getStatus() == Status.RUNNING){
			task.cancel(true);
		}
		// cleanup models
		if(modelIssue != null){
			modelIssue.finalize();
			modelIssue = null;
		}
		super.onDestroy();
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issuelist);

		ProjectIntent intent = new ProjectIntent(getIntent());
		int connectionid = intent.getConnectionId();
		long projectid = intent.getProjectId();
		modelIssue = new IssueModel(getApplicationContext(), connectionid,projectid);

		listView = (ListView)findViewById(R.id.listConnectionList);
		listAdapter = new RedmineIssueListAdapter(
				this,R.layout.issueitem
				,new ArrayList<RedmineIssue>());
		listView.addFooterView(getFooter());
		listView.setAdapter(listAdapter);

		listView.setOnScrollListener(this);

		onReload();

		if(listAdapter.getCount() == 0){
			onRefresh();
		}


		//リスト項目がクリックされた時の処理
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				ListView listView = (ListView) parent;
				Object listitem = listView.getItemAtPosition(position);
				if(listitem == null || ! RedmineIssue.class.isInstance(listitem)  )
				{
					return;
				}
				RedmineIssue item = (RedmineIssue) listitem;
				IssueIntent intent = new IssueIntent(getApplicationContext(), IssueViewActivity.class );
				intent.setConnectionId(item.getConnectionId());
				intent.setIssueId(item.getIssueId());
				startActivity( intent.getIntent() );
			}
		});
	}

	private View getFooter() {
		if (mFooter == null) {
			mFooter = getLayoutInflater()
				.inflate(R.layout.listview_footer,null);
		}
		return mFooter;
	}
	private void invisibleFooter() {
		Log.d("footer","invisible");
		if (mFooter == null)
			return;
		if (listView.getFooterViewsCount() < 1)
			return;
		listView.removeFooterView(getFooter());
	}
	protected void onReload(){
		listAdapter.notifyDataSetInvalidated();
		curentpos = 0;
		listAdapter.clear();
		additionalReading();
		listAdapter.notifyDataSetChanged();
	}

	private long curentpos = 0;
	private final long READ_ITEMS = 10;
	private void additionalReading() {

		List<RedmineIssue> issues = modelIssue.fetchAllData(curentpos,READ_ITEMS);
		for (RedmineIssue i : issues){
			listAdapter.add(i);
		}
		if(issues.size() < READ_ITEMS){
			invisibleFooter();
			Log.d("additionalReading","invisible");
		}
		Log.d("additionalReading","pos: " + Long.valueOf(curentpos));
		Log.d("additionalReading","size: " + Long.valueOf(issues.size()));
		curentpos += issues.size();

	}
	protected void onRefresh(){
		if(task != null && task.getStatus() == Status.RUNNING){
			return;
		}
		task = new SelectDataTask(this,0,0);
		task.execute(0);
	}

	private class SelectDataTask extends AsyncTask<Integer, Integer, Integer> {
		private ProgressDialog dialog;
		private Context parentContext;
		private final int MAXLOAD = 50;
		private int lastloaded = 0;
		private int limitloaded = 0;
		public SelectDataTask(final Context tex,int loaded,int limit){
			lastloaded = loaded;
			limitloaded = limit;
			parentContext = tex;
		}
		// can use UI thread here
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(parentContext);
			dialog.setMessage(parentContext.getString(R.string.menu_settings_loading));
			dialog.show();
			dialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					limitloaded = -1;
				}
			});
		}

		@Override
		protected Integer doInBackground(Integer ... params) {
			int count = modelIssue.fetchRemoteData(lastloaded,MAXLOAD);
			return count;
		}
		// can use UI thread here
		@Override
		protected void onPostExecute(Integer params) {
			if(lastloaded == 0){
				onReload();
			}
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (limitloaded != 0 && (lastloaded + MAXLOAD) > limitloaded){
			} else if (params == MAXLOAD){
				task = new SelectDataTask(parentContext,lastloaded + MAXLOAD,limitloaded);
				task.execute(0);
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu( menu );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.projects, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_projects_refresh:
			{
				this.onRefresh();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	public void onScroll(AbsListView view, int firstVisibleItem,
		int visibleItemCount, int totalItemCount) {
		if (totalItemCount == firstVisibleItem + visibleItemCount) {
			if (listView.getFooterViewsCount() < 1)
				return;
			listAdapter.notifyDataSetInvalidated();
			additionalReading();
			listAdapter.notifyDataSetChanged();
		}
	}
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}
}
