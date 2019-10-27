package jp.redmine.redmineclient.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineAttachmentModel;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.TypeConverter;
import jp.redmine.redmineclient.fragment.form.DownloadForm;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.AttachmentArgument;
import jp.redmine.redmineclient.provider.Attachment;

public class FileDownload extends OrmLiteFragment<DatabaseCacheHelper> {
	static private final String TAG = FileDownload.class.getSimpleName();
	private DownloadForm form;

	public FileDownload(){
		super();
	}

	static public FileDownload newInstance(AttachmentArgument intent){
		FileDownload instance = new FileDownload();
		instance.setArguments(intent.getArgument());
		return instance;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.page_download, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		form = new DownloadForm(getActivity(), getView());

		form.buttonDownload.setOnClickListener(v -> {
			AttachmentArgument instance = new AttachmentArgument();
			instance.setArgument(getArguments());
			RedmineAttachmentModel modelAttachment = new RedmineAttachmentModel(getHelper());
			try {
				RedmineAttachment attachment = modelAttachment.fetchById(instance.getConnectionId(), instance.getAttachmentId());

				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Attachment.getUrl(attachment.getId());
				String mime = TypeConverter.getMimeType(attachment.getFilenameExt());
				intent.setDataAndType(uri, mime);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				getActivity().startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(getActivity(), R.string.activity_not_found, Toast.LENGTH_SHORT).show();
			} catch (SQLException e) {
				Log.e(TAG,"onClick",e);
			}

		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			// Downloader is supported from GINGERBREAD
			form.buttonBrowser.setOnClickListener(v -> {
				// Check permission
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
							== PackageManager.PERMISSION_GRANTED) {
						Log.i(TAG, "You have permission");
						StartDownloadByManager();
					} else {
						Log.i(TAG, "You have asked for permission");
						requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
					}
				} else {
					//you dont need to worry about these stuff below api level 23
					Log.i(TAG, "You already have the permission");
					StartDownloadByManager();
				}
			});
		} else {
			form.buttonBrowser.setVisibility(View.GONE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode != 1 || grantResults.length < 1)
			return;
		if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			StartDownloadByManager();
		} else {
			Toast.makeText(getActivity(), R.string.download_cancelled, Toast.LENGTH_LONG).show();
		}
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void StartDownloadByManager(){
		AttachmentArgument instance = new AttachmentArgument();
		instance.setArgument(getArguments());
		RedmineAttachmentModel modelAttachment = new RedmineAttachmentModel(getHelper());
		try {
			RedmineAttachment attachment = modelAttachment.fetchById(instance.getConnectionId(), instance.getAttachmentId());
			RedmineConnection connection = ConnectionModel.getItem(getActivity(), attachment.getConnectionId());

			Uri uri = Uri.parse(attachment.getContentUrl());
			DownloadManager.Request r = new DownloadManager.Request(uri);
			r.setTitle(connection.getName() + " - " + attachment.getFilename());
			r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, attachment.getFilename());
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				r.allowScanningByMediaScanner();
				r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			}
			if (!TextUtils.isEmpty(connection.getToken()))
				r.addRequestHeader("X-Redmine-API-Key",connection.getToken());
			if (connection.isAuth()) {
				String auth = connection.getAuthId() + ":" + connection.getAuthPasswd();
				String base64 = Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
				r.addRequestHeader("Authorization", "Basic " + base64);
			}

			DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
			dm.enqueue(r);

		} catch (SQLException e) {
			Log.e(TAG,"onClick",e);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		AttachmentArgument intent = new AttachmentArgument();
		intent.setArgument(getArguments());
		int connectionid = intent.getConnectionId();

		RedmineAttachmentModel model = new RedmineAttachmentModel(getHelper());
		RedmineAttachment attachment = new RedmineAttachment();
		try {
			attachment = model.fetchById(connectionid, intent.getAttachmentId());
		} catch (SQLException e) {
			Log.e(TAG,"onStart",e);
		}

		form.setValue(attachment);
	}

}
