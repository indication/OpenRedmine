package jp.redmine.redmineclient.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.param.IssueArgument;

public class Issue extends SherlockFragment {

	public Issue(){
		super();
	}

	static public Issue newInstance(IssueArgument intent){
		Issue instance = new Issue();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		IssueArgument arg = new IssueArgument();
		arg.setArgument(getArguments(),true);
		FragmentTransaction tran = getFragmentManager().beginTransaction();
		tran.replace(R.id.fragmentOneHeader, IssueTitle.newInstance(arg));
		tran.replace(R.id.fragmentOne, IssueView.newInstance(arg));
		tran.replace(R.id.fragmentOneFooter, IssueComment.newInstance(arg));
		//tran.addToBackStack(null);
		tran.commit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_one, container, false);
	}
}
