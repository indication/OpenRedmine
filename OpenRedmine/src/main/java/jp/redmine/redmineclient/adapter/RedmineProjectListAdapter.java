package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.form.ProjectListItemForm;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

public class RedmineProjectListAdapter extends  RedmineDaoAdapter<RedmineProject, Long, DatabaseCacheHelper> {
	private static final String TAG = RedmineProjectListAdapter.class.getSimpleName();
	private RedmineProjectModel model;
	protected Integer connection_id;
	public RedmineProjectListAdapter(DatabaseCacheHelper helper, Context context) {
		super(helper, context, RedmineProject.class);
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
		ProjectListItemForm form;
		if(view.getTag() != null && view.getTag() instanceof ProjectListItemForm){
			form = (ProjectListItemForm)view.getTag();
		} else {
			form = new ProjectListItemForm(view);
		}
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
	protected QueryBuilder<RedmineProject, Long> getQueryBuilder() throws SQLException {
		QueryBuilder<RedmineProject, Long> builder = dao.queryBuilder();
		Where<RedmineProject,Long> where = builder.where()
				.eq(RedmineProject.CONNECTION, connection_id)
				;
		builder.setWhere(where);
		builder.orderBy(RedmineProject.PROJECT_ID, true);
		return builder;
	}
	@Override
	protected QueryBuilder<RedmineProject, Long> getSearchQueryBuilder(String search) throws SQLException {
		QueryBuilder<RedmineProject, Long> builder = getQueryBuilder();
		builder.where()
				.like(RedmineProject.NAME, "%"+search+"%")
				.and()
				.eq(RedmineProject.CONNECTION, connection_id)
		;
		builder.orderBy(RedmineProject.NAME, true);
		return builder;
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
