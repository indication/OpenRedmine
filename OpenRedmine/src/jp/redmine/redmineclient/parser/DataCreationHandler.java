package jp.redmine.redmineclient.parser;

import java.sql.SQLException;

public interface DataCreationHandler<CON,TYPE>{
	public void onData(CON info,TYPE data) throws SQLException;
}