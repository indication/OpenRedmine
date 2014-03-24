package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.view.View;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineNews;
import jp.redmine.redmineclient.form.IMasterRecordListItemForm;
import jp.redmine.redmineclient.adapter.form.NewsForm;

public class NewsListAdapter extends RedmineDaoAdapter<RedmineNews, Long, DatabaseCacheHelper> {
	protected Integer connection_id;
	protected Long project_id;
	public NewsListAdapter(DatabaseCacheHelper helper, Context context) {
		super(helper, context, RedmineNews.class);
	}

	public void setupParameter(int connection, long project){
		connection_id = connection;
		project_id = project;
	}

    @Override
	public boolean isValidParameter(){
		if(connection_id == null || project_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected int getItemViewId() {
		return android.R.layout.simple_list_item_1;
	}

	@Override
	protected void setupView(View view, RedmineNews data) {
		NewsForm form;
		if(view.getTag() != null && view.getTag() instanceof NewsForm){
			form = (NewsForm)view.getTag();
		} else {
			form = new NewsForm(view);
		}
		form.setValue(data);
	}

	@Override
	protected QueryBuilder<RedmineNews, Long> getQueryBuilder() throws SQLException {
		QueryBuilder<RedmineNews, Long> builder = dao.queryBuilder();
		builder
				.orderBy(RedmineNews.NEWS_ID, false)
				.where()
				.eq(RedmineNews.CONNECTION, connection_id)
				.and()
				.eq(RedmineNews.NEWS_ID, project_id)
		;
		return builder;
	}

	@Override
	protected long getDbItemId(RedmineNews item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

}
