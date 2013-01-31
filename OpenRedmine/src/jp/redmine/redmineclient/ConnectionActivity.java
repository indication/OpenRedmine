package jp.redmine.redmineclient;

import java.sql.SQLException;

import jp.redmine.redmineclient.db.store.RedmineConnectionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.form.RedmineConnectionActivityForm;
import jp.redmine.redmineclient.intent.ConnectionIntent;
import jp.redmine.redmineclient.intent.ConnectionNaviResultIntent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConnectionActivity extends DbBaseActivity {
	public ConnectionActivity(){
		super();
	}
	private int idEditing = -1;
	private static final int ACTIVITY_SUB = 1001;
	private RedmineConnectionActivityForm form;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection);

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

		RedmineConnectionModel model = new RedmineConnectionModel(getHelper());
		RedmineConnection con = new RedmineConnection();
		try {
			con = model.fetchById(idEditing);
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		RedmineConnectionModel model = new RedmineConnectionModel(getHelper());
		RedmineConnection con = new RedmineConnection();
		form.getValue(con);
		try {
			if(idEditing == -1){
				model.create(con);
			} else {
				con.setId(idEditing);
				model.update(con);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

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

