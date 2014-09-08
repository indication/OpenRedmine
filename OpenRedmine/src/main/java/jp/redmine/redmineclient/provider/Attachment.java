package jp.redmine.redmineclient.provider;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.j256.ormlite.android.apptools.OrmLiteContentProvider;

import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineAttachmentModel;

public class Attachment extends OrmLiteContentProvider<DatabaseCacheHelper> {
	private static enum AttachmentUrl {
		id,
		issue,
		;
		public static AttachmentUrl getEnum(int value){
			return AttachmentUrl.values()[value];
		}
	}
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI("attachment","id/#",AttachmentUrl.id.ordinal());
		sURIMatcher.addURI("attachment","issue/#",AttachmentUrl.issue.ordinal());
	}
	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
		return null;
	}

	@Override
	public String getType(Uri uri) {
		switch(AttachmentUrl.getEnum(sURIMatcher.match(uri))){
			case id:
				RedmineAttachmentModel model = new RedmineAttachmentModel(getHelper());

			case issue:
				break;
			default:
				return null;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		return null;
	}

	@Override
	public int delete(Uri uri, String s, String[] strings) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
		return 0;
	}
}
