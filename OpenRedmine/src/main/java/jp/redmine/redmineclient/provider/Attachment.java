package jp.redmine.redmineclient.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.j256.ormlite.android.apptools.OrmLiteContentProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineAttachmentModel;
import jp.redmine.redmineclient.entity.RedmineAttachment;

public class Attachment extends OrmLiteContentProvider<DatabaseCacheHelper> {
	private static final String TAG = Attachment.class.getSimpleName();
	public static final String PROVIDER = BuildConfig.PACKAGE_NAME + "." + TAG;
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
		//sURIMatcher.addURI("attachment","issue/#/#",AttachmentUrl.issue.ordinal());
	}

	public static String getUrl(long id){
		Uri.Builder builder = Uri.parse(PROVIDER).buildUpon();
		builder.scheme(ContentResolver.SCHEME_CONTENT);
		builder.appendPath("id");
		builder = ContentUris.appendId(builder, id);
		return builder.build().toString();
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
	public ParcelFileDescriptor openFile(Uri uri, String mode, android.os.CancellationSignal signal) throws FileNotFoundException {
		RedmineAttachmentModel model = new RedmineAttachmentModel(getHelper());
		RedmineAttachment attachment = getAttachment(uri, model);
		if (attachment.getId() == null)
			return null;
		try {
			if (!model.isFileExists(attachment)) {

			}
			File file = File.createTempFile(attachment.getLocalFileName(), attachment.getFilenameExt(), getContext().getCacheDir());
			FileOutputStream file_stream = new FileOutputStream(file);
			model.loadData(attachment,file_stream);
			file_stream.close();
			ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
			return parcel;
		} catch (IOException e) {
			Log.e( TAG, "IO Error: " + uri.toString(), e);
		} catch (SQLException e) {
			Log.e(TAG, "SQL Error: " + uri.toString(), e);
		}
		return null;
	}

	@Override
	public String getType(Uri uri) {
		RedmineAttachmentModel model = new RedmineAttachmentModel(getHelper());
		RedmineAttachment attachment = getAttachment(uri, model);
		if (attachment.getId() == null)
			return null;
		String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(attachment.getFilenameExt());
		Log.d(TAG,"file: " + uri.toString() + " mimetype: " + mimetype + " -- " + attachment.getContentType());
		return mimetype;
	}

	protected RedmineAttachment getAttachment(Uri uri,RedmineAttachmentModel model){
		try {
			switch(AttachmentUrl.getEnum(sURIMatcher.match(uri))){
				case id:
					return model.fetchById(ContentUris.parseId(uri));
				case issue:
					List<String> params = uri.getPathSegments();
					if (params.size() < 4)
						break;
					return model.fetchById(Integer.parseInt(params.get(1)),Integer.parseInt(params.get(3)));
				default:
					break;
			}
		} catch (SQLException e) {
			Log.e(TAG, "");
		}
		return new RedmineAttachment();
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
