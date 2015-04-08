// ISyncObserver.aidl
package jp.redmine.redmineclient.service;

interface ISyncObserver {
    void onStart(int kind);
    void onStop(int kind);
    void onError(int kind, int status);
}
