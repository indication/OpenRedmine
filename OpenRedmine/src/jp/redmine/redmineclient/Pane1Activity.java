package jp.redmine.redmineclient;

import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;

import jp.redmine.redmineclient.activity.handler.ConnectionListHandler;
import jp.redmine.redmineclient.activity.handler.IssueListHandler;
import jp.redmine.redmineclient.activity.handler.ProjectListHandler;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.form.helper.TextileHelper.IntentAction;
import jp.redmine.redmineclient.fragment.ActivityInterface;
import jp.redmine.redmineclient.fragment.ConnectionList;
import jp.redmine.redmineclient.fragment.IssueList;
import jp.redmine.redmineclient.fragment.ProjectList;
import android.os.Bundle;

public class Pane1Activity extends OrmLiteFragmentActivity<DatabaseCacheHelper>
	implements ActivityInterface {
	public Pane1Activity(){
		super();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.fragment_one);


		getSupportFragmentManager().beginTransaction()
			.add(R.id.fragmentOne, ConnectionList.newInstance())
			.commit();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@SuppressWarnings("unchecked")
	public <T> T getHandler(Class<T> cls){
		if(cls.equals(ConnectionList.OnArticleSelectedListener.class))
			return (T) new ConnectionListHandler(getSupportFragmentManager());
		if(cls.equals(ProjectList.OnArticleSelectedListener.class))
			return (T) new ProjectListHandler(getSupportFragmentManager());
		if(cls.equals(IssueList.OnArticleSelectedListener.class))
			return (T) new IssueListHandler(getSupportFragmentManager());
		if(cls.equals(IntentAction.class))
			return (T) new IssueListHandler(getSupportFragmentManager());
		return null;
	}
}
