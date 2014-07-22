package jp.redmine.redmineclient.adapter;

import android.content.Context;
import android.view.View;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.adapter.form.AttachmentForm;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.entity.RedmineAttachment;

class AttachmentListAdapter extends RedmineDaoAdapter<RedmineAttachment, Long, DatabaseCacheHelper>  {
	protected Integer connection_id;
	protected Integer issue_id;


	public AttachmentListAdapter(DatabaseCacheHelper m, Context context) {
		super(m, context, RedmineAttachment.class);
	}

	public void setupParameter(int connection, int issue){
		connection_id = connection;
		issue_id = issue;
	}

    @Override
	public boolean isValidParameter(){
		if(issue_id == null || connection_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected int getItemViewId() {
		return R.layout.listitem_attachment;
	}

	@Override
	protected void setupView(View view, RedmineAttachment data) {
		AttachmentForm form;
		if(view.getTag() != null && view.getTag() instanceof AttachmentForm){
			form = (AttachmentForm)view.getTag();
		} else {
			form = new AttachmentForm(view);
		}
		form.setValue(data);
	}

	@Override
	protected long getDbItemId(RedmineAttachment item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

	@Override
	protected QueryBuilder<RedmineAttachment, Long> getQueryBuilder() throws SQLException {
		QueryBuilder<RedmineAttachment, Long> builder = dao.queryBuilder();
		Where<RedmineAttachment,Long> where = builder.where()
				.eq(RedmineAttachment.CONNECTION, connection_id)
				.and()
				.eq(RedmineAttachment.ISSUE_ID, issue_id)
				;
		builder.setWhere(where);
		builder.orderBy(RedmineAttachment.ATTACHMENT_ID, true);
		return builder;
	}

}
