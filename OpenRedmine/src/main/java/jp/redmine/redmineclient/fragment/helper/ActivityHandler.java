package jp.redmine.redmineclient.fragment.helper;

import android.content.Context;

import jp.redmine.redmineclient.activity.handler.AttachmentActionInterface;
import jp.redmine.redmineclient.activity.handler.ConnectionActionInterface;
import jp.redmine.redmineclient.activity.handler.IssueActionInterface;
import jp.redmine.redmineclient.activity.handler.TimeentryActionInterface;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.fragment.ActivityInterface;

public class ActivityHandler {
	@SuppressWarnings("unchecked")
	static public <T> T getHandler(Context activity, Class<T> cls){
		if(activity instanceof ActivityInterface) {
			T handler = ((ActivityInterface) activity).getHandler(cls);
			if (handler != null) return handler;
		}
		// returns empty handler
		if(cls.equals(ConnectionActionInterface.class))
			return (T) new ConnectionActionInterface(){
				@Override
				public void onConnectionSelected(int connectionid) {}
				@Override
				public void onConnectionEdit(int connectionid) {}
				@Override
				public void onConnectionAdd() {}
				@Override
				public void onConnectionSaved() {}
			};
		if(cls.equals(WebviewActionInterface.class))
			return (T) new WebviewActionInterface(){
				@Override
				public void issue(int connection, int issueid) {}
				@Override
				public boolean url(String url, Integer connection) {return false;}
				@Override
				public void wiki(int connection, long projectid, String title) {}
			};
		if(cls.equals(IssueActionInterface.class))
			return (T) new IssueActionInterface(){
				@Override
				public void onIssueFilterList(int connectionid, int filterid) {}
				@Override
				public void onIssueList(int connectionid, long projectid) {}
				@Override
				public void onKanbanList(int connectionid, long projectid) {}
				@Override
				public void onIssueSelected(int connectionid, int issueid) {}
				@Override
				public void onIssueEdit(int connectionid, int issueid) {}
				@Override
				public void onIssueRefreshed(int connectionid, int issueid) {}
				@Override
				public void onIssueAdd(int connectionId, long projectId) {}
			};
		if(cls.equals(TimeentryActionInterface.class))
			return (T) new TimeentryActionInterface(){
				@Override
				public void onTimeEntryList(int connectionid, int issueid) {}
				@Override
				public void onTimeEntrySelected(int connectionid, int issueid, int timeentryid) {}
				@Override
				public void onTimeEntryEdit(int connectionid, int issueid, Integer timeentryid) {}
				@Override
				public void onTimeEntryAdd(int connectionid, int issueid) {}
			};
		if(cls.equals(AttachmentActionInterface.class))
			return (T) new AttachmentActionInterface(){
				@Override
				public void onAttachmentSelected(int connectionid, int attachmentid) {}
			};
		return null;
	}
}
