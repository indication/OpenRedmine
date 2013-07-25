package jp.redmine.redmineclient.fragment;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.form.RedmineIssueJumpForm;
import jp.redmine.redmineclient.fragment.IssueView.OnArticleSelectedListener;
import jp.redmine.redmineclient.param.ConnectionArgument;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class IssueJump extends OrmLiteFragment<DatabaseCacheHelper> {

	private RedmineIssueJumpForm form;
	private OnArticleSelectedListener mListener;

	public IssueJump(){
		super();
	}


	static public IssueJump newInstance(ConnectionArgument intent){
		IssueJump instance = new IssueJump();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.issuejump, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		form = new RedmineIssueJumpForm(getView());

		form.buttonOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!form.Validate())
					return;

				ConnectionArgument intent = new ConnectionArgument();
				intent.setArgument(getArguments());
				mListener.onIssueSelected(intent.getConnectionId(), form.getIssueId());
			}
		});
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof ActivityInterface){
			mListener = ((ActivityInterface)activity).getHandler(OnArticleSelectedListener.class);
		}
		if(mListener == null) {
			//setup empty events
			mListener = new OnArticleSelectedListener() {

				@Override
				public void onTimeEntrySelected(int connectionid, int issueid) {}
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

	}

}
