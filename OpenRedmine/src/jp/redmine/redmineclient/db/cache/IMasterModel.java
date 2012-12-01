package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;


public interface IMasterModel<T> {

	public long countByProject(int connection_id,long project_id) throws SQLException;
	public T fetchItemByProject(int connection_id,long project_id,long offset,long limit) throws SQLException;
}
