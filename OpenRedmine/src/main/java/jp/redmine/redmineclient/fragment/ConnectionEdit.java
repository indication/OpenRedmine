package jp.redmine.redmineclient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.ConnectionNaviActivity;
import jp.redmine.redmineclient.activity.handler.ConnectionActionInterface;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.fragment.form.ConnectionForm;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.ConnectionArgument;
import jp.redmine.redmineclient.param.ConnectionNaviResultArgument;

public class ConnectionEdit extends OrmLiteFragment<DatabaseCacheHelper> {

	private int idEditing = -1;
	private static final int ACTIVITY_SUB = 1001;
	private ConnectionForm form;

	private ConnectionModel modelConnection;
	private ConnectionActionInterface mListener;

	public ConnectionEdit(){
		super();
	}


	static public ConnectionEdit newInstance(ConnectionArgument intent){
		ConnectionEdit instance = new ConnectionEdit();
		instance.setArguments(intent.getArgument());
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.input_connection, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		modelConnection = new ConnectionModel(getActivity());
		mListener = ActivityHandler.getHandler(getActivity(), ConnectionActionInterface.class);

		form = new ConnectionForm(getView());
		form.setupEvents();

		form.buttonSave.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				completeSave();
			}
		});
		form.buttonAccess.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				String url = form.getUrl();
				if("".equals(url))
					return;
				ConnectionNaviResultArgument load = new ConnectionNaviResultArgument();
				load.setIntent(getActivity(), ConnectionNaviActivity.class );
				load.setUrl(url);
				load.setAuthID(form.getAuthID());
				load.setAuthPassword(form.getAuthPassword());
				load.setToken(form.getToken());
				load.setUnsafeSSL(form.isUnsafeConnection());
				startActivityForResult(load.getIntent(), ACTIVITY_SUB);
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		ConnectionArgument intent = new ConnectionArgument();
		intent.setArgument(getArguments());
		idEditing = intent.getConnectionId();
		loadData();
	}


    /**
     * Setup data from database.
     */
    protected void loadData(){
		if (idEditing == -1)
			return;

		RedmineConnection con = modelConnection.getItem(idEditing);
		if(con.getId() != null)
		{
			form.setValue(con);
		} else {
			idEditing = -1;
		}
    }
	/**
	 * Save button clicked
	 */
	protected void completeSave(){
		if(!form.Validate())
			return;
		RedmineConnection con = new RedmineConnection();
		form.getValue(con);
		modelConnection.updateItem(idEditing, con);
		Toast.makeText(getActivity(),R.string.has_been_saved, Toast.LENGTH_SHORT).show();
		mListener.onConnectionSaved();
	}

	protected void computeDelete(){
		if (idEditing == -1)
			return;
		if (modelConnection.deleteItem(idEditing) > 0) {
			Toast.makeText(getActivity(),R.string.has_been_saved, Toast.LENGTH_SHORT).show();
			mListener.onConnectionSaved();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case ACTIVITY_SUB:
			if(resultCode !=Activity.RESULT_OK )
				break;
			ConnectionNaviResultArgument intent = new ConnectionNaviResultArgument();
			intent.setIntent(data);
			form.setAuthentication(intent.getAuthID(), intent.getAuthPassword());
			form.setUnsafeConnection(intent.isUnsafeSSL());
			form.setToken(intent.getToken());
			break;
		default:
			break;
		}
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.edit, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_save:
				completeSave();
				return true;
			case R.id.menu_delete:
				computeDelete();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
