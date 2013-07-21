package jp.redmine.redmineclient.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RedmineIssueRelation {

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(uniqueIndexName="issue_target")
    private Integer connection_id;
    @DatabaseField(foreign = true,foreignColumnName="id", columnName= "project_id", foreignAutoRefresh = true)
    private RedmineProject project;

}
