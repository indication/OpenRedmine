package jp.redmine.redmineclient;

import android.app.Activity;
import android.os.Bundle;

public class ProjectsActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //@TODO 未実装
        setContentView(R.layout.connectionlist);
        /*
         *
		ArrayAdapter arrayAdapter
			= new ArrayAdapter( this, R.layout.rowitem, data );

		ListView list = (ListView)findViewById( R.id.ListView01 );
		list.setAdapter( arrayAdapter );
         */
    }
}
