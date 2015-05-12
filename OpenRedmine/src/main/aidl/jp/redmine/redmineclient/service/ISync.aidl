// ISync.aidl
package jp.redmine.redmineclient.service;

// Declare any non-default types here with import statements
import jp.redmine.redmineclient.service.ISyncObserver;

interface ISync {
    void fetchMaster(int connection_id);
    void fetchProject(int connection_id);
    void fetchNews(int connection_id, long project_id);
    void fetchIssuesByProject(int connection_id, long project_id, int offset, boolean isFetchAll);
    void fetchIssuesByFilter(int connection_id, int filter_id, int offset, boolean isFetchAll);
    void fetchIssue(int connection_id, int issue_id);
    void fetchWikiByProject(int connection_id, long project_id);
    void fetchWiki(int connection_id, long project_id, String title);
    void setObserver(ISyncObserver observer);
    void removeObserver(ISyncObserver observer);
}
