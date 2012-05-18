package jp.redmine.redmineclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class OpenRedmineActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu( menu );

        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main, menu );
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch ( item.getItemId() )
    	{
    		case R.id.menu_access_to:
    		{
    			Intent intent = new Intent( this, RedmineConnectionListActivity.class );
    			startActivity( intent );
    			return true;
    		}
    	}
    	return super.onOptionsItemSelected(item);
    }
}