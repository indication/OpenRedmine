package jp.redmine.redmineclient;

import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.form.RedmineConnectionActivityForm;
import jp.redmine.redmineclient.intent.ConnectionIntent;
import jp.redmine.redmineclient.intent.ConnectionNaviResultIntent;
import jp.redmine.redmineclient.model.ConnectionModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConnectionActivity extends Activity {
	public ConnectionActivity(){
		super();
	}
	private int idEditing = -1;
	private static final int ACTIVITY_SUB = 1001;
	private RedmineConnectionActivityForm form;

	private ConnectionModel modelConnection;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(modelConnection != null){
			modelConnection.finalize();
			modelConnection = null;
		}
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.connection);

		modelConnection = new ConnectionModel(getApplicationContext());

		form = new RedmineConnectionActivityForm(this);
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
				ConnectionNaviResultIntent load = new ConnectionNaviResultIntent( getApplicationContext(), ConnectionNaviActivity.class );
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
	protected void onStart() {
		super.onStart();
		ConnectionIntent intent = new ConnectionIntent(getIntent());
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
		//@todo tostring
		Toast.makeText(getApplicationContext(),
				"Has been saved.", Toast.LENGTH_SHORT).show();
		this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case ACTIVITY_SUB:
			if(resultCode !=RESULT_OK )
				break;
			ConnectionNaviResultIntent intent = new ConnectionNaviResultIntent(data);
			form.setAuthentication(intent.getAuthID(), intent.getAuthPassword());
			form.setUnsafeConnection(intent.isUnsafeSSL());
			form.setToken(intent.getToken());
			break;
		default:
			break;
		}
	}
}

