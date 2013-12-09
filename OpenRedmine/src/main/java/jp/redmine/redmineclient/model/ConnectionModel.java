package jp.redmine.redmineclient.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.db.store.RedmineConnectionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import android.content.Context;
import android.util.Log;

public class ConnectionModel extends Connector {

	public ConnectionModel(Context context) {
		super(context);
	}

	public RedmineConnection getItem(int itemid){
		RedmineConnectionModel model = new RedmineConnectionModel(helperStore);
		RedmineConnection item = new RedmineConnection();
		try {
			item = model.fetchById(itemid);
		} catch (SQLException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return item;
	}
	public void deleteItem(int itemid){
		RedmineConnectionModel model = new RedmineConnectionModel(helperStore);
		try {
			model.delete(itemid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<RedmineConnection> fetchAllData(){
		final RedmineConnectionModel model =
			new RedmineConnectionModel(helperStore);
		List<RedmineConnection> projects = new ArrayList<RedmineConnection>();
		try {
			projects = model.fetchAll();
		} catch (SQLException e) {
			Log.e("SelectDataTask","doInBackground",e);
		} catch (Throwable e) {
			Log.e("SelectDataTask","doInBackground",e);
		}
		return projects;
	}
}
