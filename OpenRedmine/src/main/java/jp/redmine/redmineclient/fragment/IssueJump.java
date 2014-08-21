package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.fragment.form.IssueJumpForm;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ConnectionArgument;

public class IssueJump extends OrmLiteFragment<DatabaseCacheHelper> {

	private IssueJumpForm form;
	private IssueActionInterface mListener;

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
		return inflater.inflate(R.layout.listheader_issuejump, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		form = new IssueJumpForm(getView());

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
		mListener = ActivityHandler.getHandler(activity, IssueActionInterface.class);

	}

}
