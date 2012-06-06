package jp.redmine.redmineclient;

import java.sql.SQLException;

import jp.redmine.redmineclient.db.DatabaseHelper;
import jp.redmine.redmineclient.entity.RedmineConnection;
import com.j256.ormlite.dao.Dao;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RedmineConnectionActivity extends Activity {
	private int idEditing = -1;
	public static final String INTENT_INT_ID = "CONNECTION_ID";
	private RedmineConnectionActivityForm form;
	private DatabaseHelper helper;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection);

		form = new RedmineConnectionActivityForm(this);
		form.setupEvents();

		helper = new DatabaseHelper(this);

		Intent intent = getIntent();
		idEditing = intent.getIntExtra(INTENT_INT_ID,-1);

		form.buttonSave.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View view) {
				completeSave();
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
		if (helper == null)
			return;

		try {
			Dao<RedmineConnection, Integer> projectDao = helper.getDao(RedmineConnection.class);
			RedmineConnection con = (RedmineConnection)projectDao.queryForId(idEditing);
			if(con != null)
			{
				form.setValue(con);
			} else {
				idEditing = -1;
			}
		} catch (SQLException e) {
			Log.e("RedmineConnectionActivity","loadData",e);
		}

    }
	/**
	 * Save button clicked
	 */
	protected void completeSave(){
		if (helper == null)
			return;
		if(!form.Validate())
			return;

		try {
			RedmineConnection con = new RedmineConnection();
			Dao<RedmineConnection, Integer> projectDao = helper.getDao(RedmineConnection.class);
			form.getValue(con);
			if(idEditing == -1){
				projectDao.create(con);
			} else {
				con.setId(idEditing);
				projectDao.update(con);
			}

		} catch (SQLException e) {
			Log.e("RedmineConnectionActivity","completeSave",e);
		}
		//@todo tostring
		Toast.makeText(getApplicationContext(),
				"Has been saved.", Toast.LENGTH_SHORT).show();
		this.finish();
	}
}

