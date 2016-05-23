package jp.redmine.redmineclient.model;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.store.RedmineConnectionModel;
import jp.redmine.redmineclient.entity.IConnectionRecord;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineAttachmentData;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineIssueRelation;
import jp.redmine.redmineclient.entity.RedmineNews;
import jp.redmine.redmineclient.entity.RedminePriority;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineProjectCategory;
import jp.redmine.redmineclient.entity.RedmineProjectMember;
import jp.redmine.redmineclient.entity.RedmineProjectVersion;
import jp.redmine.redmineclient.entity.RedmineRecentIssue;
import jp.redmine.redmineclient.entity.RedmineRole;
import jp.redmine.redmineclient.entity.RedmineTimeActivity;
import jp.redmine.redmineclient.entity.RedmineTimeEntry;
import jp.redmine.redmineclient.entity.RedmineTracker;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.RedmineWatcher;
import jp.redmine.redmineclient.entity.RedmineWiki;

public class ConnectionModel extends Connector {
	private final static String TAG = ConnectionModel.class.getSimpleName();

	public ConnectionModel(Context context) {
		super(context);
	}
	public static RedmineConnection getItem(Context context, int item_id){
		ConnectionModel mConnection = new ConnectionModel(context);
		RedmineConnection connection = mConnection.getItem(item_id);
		mConnection.close();
		return connection;
	}

	public RedmineConnection getItem(int itemid){
		RedmineConnectionModel model = new RedmineConnectionModel(helperStore);
		RedmineConnection item = new RedmineConnection();
		try {
			item = model.fetchById(itemid);
		} catch (SQLException e) {
			Log.e(TAG, "getItem", e);
		}
		return item;
	}

	public RedmineConnection updateItem(int itemid,RedmineConnection item){
		RedmineConnectionModel model = new RedmineConnectionModel(helperStore);

		try {
			if(itemid == -1){
				model.create(item);
			} else {
				item.setId(itemid);
				model.update(item);
			}
		} catch (SQLException e) {
			Log.e(TAG, "updateItem", e);
		}
		return item;
	}
	public int deleteItem(int itemid){
		int count = 0;
		RedmineConnectionModel model = new RedmineConnectionModel(helperStore);
		List<Class<? extends IConnectionRecord>> model_class =new ArrayList<Class<? extends IConnectionRecord>>();
		model_class.add(RedmineAttachmentData.class);
		model_class.add(RedmineAttachment.class);
		model_class.add(RedmineFilter.class);
		model_class.add(RedmineTimeEntry.class);
		model_class.add(RedmineTimeActivity.class);
		model_class.add(RedmineRecentIssue.class);
		model_class.add(RedmineIssueRelation.class);
		model_class.add(RedmineIssue.class);
		model_class.add(RedmineWiki.class);
		model_class.add(RedmineNews.class);
		model_class.add(RedmineUser.class);
		model_class.add(RedmineProjectCategory.class);
		model_class.add(RedmineProjectMember.class);
		model_class.add(RedmineProjectVersion.class);
		model_class.add(RedmineProject.class);
		model_class.add(RedmineRole.class);
		model_class.add(RedmineTracker.class);
		model_class.add(RedminePriority.class);
		model_class.add(RedmineWatcher.class);
		for(Class<? extends IConnectionRecord> cls : model_class){
			try {
				Dao<?, ?> dao =  helperCache.getDao(cls);
				DeleteBuilder<?,?> builder = dao.deleteBuilder();
				builder.where()
						.eq(RedmineConnection.CONNECTION_ID, itemid);
				builder.delete();
			} catch (SQLException e) {
				Log.e(TAG, "deleteItem", e);
			}
		}

		try {
			count += model.delete(itemid);
		} catch (SQLException e) {
			Log.e(TAG, "deleteItem", e);
		}
		return count;
	}

}
