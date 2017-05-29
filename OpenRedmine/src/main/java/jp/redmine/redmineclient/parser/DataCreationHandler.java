package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;

public interface DataCreationHandler<CON,TYPE>{
	public void onData(CON info,TYPE data) throws SQLException, IOException;
}