package jp.redmine.redmineclient;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.form.RedmineConnectionActivityForm;
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
	public static final String INTENT_INT_ID = "CONNECTION_ID";
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
		setContentView(R.layout.connection);

		modelConnection = new ConnectionModel(getApplicationContext());

		form = new RedmineConnectionActivityForm(this);
		form.setupEvents();


		Intent intent = getIntent();
		idEditing = intent.getIntExtra(INTENT_INT_ID,-1);

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
				Intent load = new Intent( getApplicationContext(), ConnectionNaviActivity.class );
				load.putExtra(ConnectionNaviActivity.INTENT_STR_URL, url);
				load.putExtra(ConnectionNaviActivity.INTENT_STR_ID, form.getAuthID());
				load.putExtra(ConnectionNaviActivity.INTENT_STR_PASS, form.getAuthPassword());
				startActivityForResult(load, ACTIVITY_SUB);
			}
		});

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
			form.setAuthentication(
					  data.getStringExtra(ConnectionNaviActivity.INTENT_STR_ID)
					, data.getStringExtra(ConnectionNaviActivity.INTENT_STR_PASS));
			form.setUnsafeConnection( data.getBooleanExtra(ConnectionNaviActivity.INTENT_BOOL_UNSAFESSL, false));
			form.setToken(data.getStringExtra(ConnectionNaviActivity.INTENT_STR_TOKEN));
			break;
		default:
			break;
		}
	}
}

