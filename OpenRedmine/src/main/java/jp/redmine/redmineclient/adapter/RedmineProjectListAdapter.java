package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.ProjectListItemForm;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

public class RedmineProjectListAdapter extends RedmineBaseAdapter<RedmineProject> {
	private static final String TAG = RedmineProjectListAdapter.class.getSimpleName();
	private RedmineProjectModel model;
	protected Integer connection_id;
	public RedmineProjectListAdapter(DatabaseCacheHelper helper) {
		super();
		model = new RedmineProjectModel(helper);
	}

	public void setupParameter(int connection){
		connection_id = connection;
	}

    @Override
	public boolean isValidParameter(){
		if(connection_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected int getItemViewId() {
		return R.layout.projectitem;
	}

	@Override
	protected void setupView(View view, final RedmineProject data) {
		ProjectListItemForm form = new ProjectListItemForm(view);
		form.ratingBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				data.setFavorite(b ? 1 : 0);
				try {
					model.update(data);
				} catch (SQLException e) {
					Log.e(TAG, "onCheckedChanged" , e);
				}
				notifyDataSetChanged();
			}
		});
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		return (int) model.countByProject(connection_id,0);
	}

	@Override
	protected RedmineProject getDbItem(int position) throws SQLException {
		return model.fetchItemByProject(connection_id,0,(long) position, 1L);
	}

	@Override
	protected long getDbItemId(RedmineProject item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

}
