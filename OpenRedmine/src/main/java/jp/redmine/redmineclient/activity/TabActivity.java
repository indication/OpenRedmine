package jp.redmine.redmineclient.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.GravityCompat;
import androidx.appcompat.widget.DrawerLayout;
import androidx.appcompat.widget.ActionBarDrawerToggle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteFragmentActivity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.AttachmentActionHandler;
import jp.redmine.redmineclient.activity.handler.AttachmentActionInterface;
import jp.redmine.redmineclient.activity.handler.ConnectionActionInterface;
import jp.redmine.redmineclient.activity.handler.ConnectionListHandler;
import jp.redmine.redmineclient.activity.handler.Core.ActivityRegistry;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.activity.handler.IssueViewHandler;
import jp.redmine.redmineclient.activity.handler.TimeEntryHandler;
import jp.redmine.redmineclient.activity.handler.TimeentryActionInterface;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.activity.helper.ActivityHelper;
import jp.redmine.redmineclient.activity.pager.CorePage;
import jp.redmine.redmineclient.fragment.ActivityInterface;

abstract class TabActivity<T extends OrmLiteSqliteOpenHelper> extends OrmLiteFragmentActivity<T>
	implements ActivityInterface {

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually
	 * expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;
	private SharedPreferences mPreferences;
	private List<CorePage> mListTabs;

	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	abstract protected List<CorePage> getTabs();
	public TabActivity(){
		super();
	}
	private Toolbar mActionBarToolbar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActivityHelper.setupTheme(this);
		setContentView(R.layout.navigation_list);
		setUp();
		super.onCreate(savedInstanceState);


		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		mListTabs = getTabs();
		mDrawerListView = (ListView)findViewById(R.id.listMenu);

		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});

		BaseAdapter adapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return mListTabs.size();
			}

			@Override
			public Object getItem(int position) {
				return mListTabs.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null) {
					convertView = getLayoutInflater().inflate(R.layout.listitem_navigation, null);
				}
				CorePage page = mListTabs.get(position);
				ImageView image = (ImageView)convertView.findViewById(R.id.imageView);
				TextView text= (TextView)convertView.findViewById(R.id.textSubject);
				if(page != null){
					Drawable icon = getResources().getDrawable(
							page.getIcon() == null ? android.R.drawable.ic_menu_gallery : page.getIcon()
					);
					image.setImageDrawable(icon);
					text.setText(page.getName());
				}
				return convertView;
			}
		};
		mDrawerListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CorePage page = (CorePage) parent.getItemAtPosition(position);
				Runnable run = page.getCustomAction();
				if (run != null){
					run.run();
					return;
				}
				mActionBarToolbar.setTitle(page.getName());
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.container, page.getFragment())
						.commit();

				mDrawerLayout.closeDrawer(mFragmentContainerView);
			}
		});
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

		// Read in the flag indicating whether or not the user has demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mUserLearnedDrawer = mPreferences.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		} else {
			mCurrentSelectedPosition = getDefaultPosition();
		}

		// Select either the default item (0) or the last selected item.
		selectItem(mCurrentSelectedPosition);

		mDrawerListView.performItemClick(mDrawerListView,mCurrentSelectedPosition,0);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	protected int getDefaultPosition(){
		for(int i=0;i<mListTabs.size();i++){
			if(mListTabs.get(i).isDefault())
				return i;
		}
		return 0;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	protected Toolbar getActionBarToolbar() {
		if (mActionBarToolbar == null) {
			mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
			if (mActionBarToolbar != null) {
				setSupportActionBar(mActionBarToolbar);
			}
		}
		return mActionBarToolbar;
	}
	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 *
	 */
	public void setUp() {
		mFragmentContainerView = findViewById(R.id.listMenu);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                    /* host Activity */
				mDrawerLayout,                    /* DrawerLayout object */
				getActionBarToolbar(),
				R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
				R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);

				supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					mPreferences.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
				}

				supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getHandler(Class<T> cls){
		ActivityRegistry registry = new ActivityRegistry(){

			@Override
			public FragmentManager getFragment() {
				return getSupportFragmentManager();
			}

			@Override
			public Intent getIntent(Class<?> activity) {
				return new Intent(getApplicationContext(),activity);
			}

			@Override
			public void kickActivity(Intent intent) {
				startActivity(intent);
			}

		};
		if(cls.equals(ConnectionActionInterface.class))
			return (T) new ConnectionListHandler(registry);
		if(cls.equals(WebviewActionInterface.class))
			return (T) new IssueViewHandler(registry);
		if(cls.equals(IssueActionInterface.class))
			return (T) new IssueViewHandler(registry);
		if(cls.equals(TimeentryActionInterface.class))
			return (T) new TimeEntryHandler(registry);
		if(cls.equals(AttachmentActionInterface.class))
			return (T) new AttachmentActionHandler(registry);
		return null;
	}
}
