// ISync.aidl
package jp.redmine.redmineclient.service;

// Declare any non-default types here with import statements
import jp.redmine.redmineclient.service.ISyncObserver;

interface ISync {
    void fetchMaster(int connection_id);
    void fetchProject(int connection_id);
    void fetchNews(int connection_id, long project_id);
    void setObserver(ISyncObserver observer);
    void removeObserver(ISyncObserver observer);
}
