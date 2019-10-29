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

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineAttachmentModel;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.TypeConverter;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.parser.DataCreationHandler;
import jp.redmine.redmineclient.parser.ParserAttachment;
import jp.redmine.redmineclient.task.Fetcher;
import jp.redmine.redmineclient.task.SelectDataTaskDataHandler;
import jp.redmine.redmineclient.task.SelectDataTaskRedmineConnectionHandler;
import jp.redmine.redmineclient.url.RemoteUrlAttachment;

public class Attachment extends OrmLiteContentProvider<DatabaseCacheHelper> {
	private static final String TAG = Attachment.class.getSimpleName();
	protected static final String PROVIDER = BuildConfig.APPLICATION_ID + "." + TAG.toLowerCase(Locale.getDefault());
	protected static final String PROVIDER_BASE = ContentResolver.SCHEME_CONTENT + "://" + PROVIDER;
	private static enum AttachmentUrl {
		none,
		id,
		attachment,
		;
		public static AttachmentUrl getEnum(int value){
			return AttachmentUrl.values()[value];
		}
	}
	private static final UriMatcher sURIMatcher = new UriMatcher(AttachmentUrl.none.ordinal());

	static {
		sURIMatcher.addURI(PROVIDER,"id/#",AttachmentUrl.id.ordinal());
		sURIMatcher.addURI(PROVIDER, "attachment/#", AttachmentUrl.attachment.ordinal());
	}

	public static Uri getUrl(long id){
		Uri.Builder builder = Uri.parse(PROVIDER_BASE).buildUpon();
		builder.appendPath("id");
		builder = ContentUris.appendId(builder, id);
		return builder.build();
	}
	public static Uri getUrl(int connection, int attachment){
		Uri.Builder builder = Uri.parse(PROVIDER_BASE).buildUpon();
		builder.appendPath("attachment");
		builder = ContentUris.appendId(builder, connection);
		builder = ContentUris.appendId(builder, attachment);
		return builder.build();
	}

	@Override
	public boolean onCreate() {
		super.onCreate();
		return true;
	}

	@Override
	protected Class<DatabaseCacheHelper> getOrmClass() {
		return DatabaseCacheHelper.class;
	}

	@Override
	public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
		return null;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		if (BuildConfig.DEBUG) Log.d(TAG, "Called openFile uri");
		return openFileInner(uri, mode, null);
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode, android.os.CancellationSignal signal) throws FileNotFoundException {
		return openFileInner(uri, mode, signal);
	}

	protected ParcelFileDescriptor openFileInner(Uri uri, String mode, android.os.CancellationSignal signal) throws FileNotFoundException {
		final RedmineAttachmentModel model = new RedmineAttachmentModel(getHelper());
		RedmineAttachment attachment = getAttachment(uri, model);
		SelectDataTaskRedmineConnectionHandler client = null;
		if (attachment.getId() == null && attachment.getConnectionId() == null){
			return null;
		}
		try {
			RedmineConnection connection = ConnectionModel.getItem(getContext(), attachment.getConnectionId());
			client = new SelectDataTaskRedmineConnectionHandler(connection);
			Fetcher.ContentResponseErrorHandler errorHandler = new Fetcher.ContentResponseErrorHandler() {
				@Override
				public void onErrorRequest(int status) {
					Log.e( TAG, "Request Error:  " + status);
				}

				@Override
				public void onError(Exception e) {
					Log.e( TAG, "IO Error:  ", e);
				}
			};
			if (attachment.getId() == null){
				fetchInfoFromRemote(client, errorHandler,model, connection,String.valueOf(attachment.getAttachmentId()));
				attachment = getAttachment(uri, model);
			}
			if (!model.isFileExists(attachment)) {
				fetchAttachmentFromRemote(client, errorHandler, model, attachment);
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
		} catch (InterruptedException e) {
			Log.e(TAG, "Thread exception", e);
		}
		return null;
	}

	protected void fetchInfoFromRemote(final SelectDataTaskRedmineConnectionHandler client
			, final Fetcher.ContentResponseErrorHandler errorHandler
			, final RedmineAttachmentModel model
			, final RedmineConnection connection
			, final String attachment_id
	) throws InterruptedException {
		final Thread fetcher = new Thread(){
			@Override
			public void run() {
				super.run();
				RemoteUrlAttachment url = new RemoteUrlAttachment();
				url.setAttachment(attachment_id);
				final ParserAttachment parserAttachment = new ParserAttachment();
				parserAttachment.registerDataCreation((info, data) -> {
					data.setRedmineConnection(connection);
					model.refreshItem(data);
				});
				boolean fetch_status = Fetcher.fetchData(client, errorHandler, client.getUrl(url)
						, (stream) -> {
						Fetcher.setupParserStream(stream,parserAttachment);
						parserAttachment.parse(null);
				});
				if (!fetch_status){
					Log.e(TAG, "Fetch failed: "+  client.getUrl(url));
				}
			}
		};
		fetcher.start();
		fetcher.join();
	}
	protected void fetchAttachmentFromRemote(final SelectDataTaskRedmineConnectionHandler client
			, final Fetcher.ContentResponseErrorHandler errorHandler
			, final RedmineAttachmentModel model
			, final RedmineAttachment attachment
	) throws InterruptedException {
		final Thread fetcher = new Thread(){
			@Override
			public void run() {
				super.run();
				if (BuildConfig.DEBUG) Log.i(TAG, "Fetch start");
				Fetcher.fetchData(client, errorHandler, attachment.getContentUrl(), stream -> {
					if (BuildConfig.DEBUG) Log.i(TAG, "Fetch incoming data");
					model.saveData(attachment,stream);
				});
				if (BuildConfig.DEBUG) Log.i(TAG, "Fetch end");
			}
		};
		fetcher.start();
		fetcher.join();

	}
	@Override
	public String getType(Uri uri) {
		RedmineAttachmentModel model = new RedmineAttachmentModel(getHelper());
		RedmineAttachment attachment = getAttachment(uri, model);
		if (attachment.getId() == null)
			return null;
		String extension = attachment.getFilenameExt();
		String mimetype = TypeConverter.getMimeType(extension);
		if (BuildConfig.DEBUG) Log.d(TAG,"file: " + uri.toString() + " mimetype: " + mimetype + " -- " + attachment.getContentType());
		return mimetype;
	}

	protected RedmineAttachment getAttachment(Uri uri,RedmineAttachmentModel model){
		try {
			switch(AttachmentUrl.getEnum(sURIMatcher.match(uri))){
				case id:
					return model.fetchById(ContentUris.parseId(uri));
				case attachment:
					List<String> params = uri.getPathSegments();
					if (params.size() < 3)
						break;
					return model.fetchById(TypeConverter.parseInteger(params.get(1)),TypeConverter.parseInteger(params.get(2)));
				default:
					Log.e(TAG, "Not found:" + uri.toString());
					break;
			}
		} catch (SQLException e) {
			Log.e(TAG, "SQL Exception", e);
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
