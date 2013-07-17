package jp.redmine.redmineclient;

import java.io.File;

import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.intent.ConnectionIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ConnectionListActivity extends FragmentActivity {
	public ConnectionListActivity(){
		super();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.fragment_connection);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu( menu );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.connection, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
		{
			case R.id.menu_access_addnew:
			{
				ConnectionIntent intent = new ConnectionIntent( getApplicationContext(), ConnectionActivity.class );
				intent.setConnectionId(-1);
				startActivity( intent.getIntent() );
				return true;
			}
			case R.id.menu_access_removecache:
			{
				String path = DatabaseCacheHelper.getDatabasePath(getApplicationContext());
				File file = new File(path);
				file.delete();
				Log.d("Cache Deleted",path);
				this.finish();
				//@todo show dialog
				return true;
			}
			case R.id.menu_settings:
			{
				Intent intent = new Intent( getApplicationContext(), CommonPreferenceActivity.class );
				startActivity( intent );

				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}