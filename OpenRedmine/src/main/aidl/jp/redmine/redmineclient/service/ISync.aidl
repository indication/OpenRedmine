// ISync.aidl
package jp.redmine.redmineclient.service;

// Declare any non-default types here with import statements

interface ISync {
    void fetchMaster(int connection_id);
    void fetchProject(int connection_id);
}
