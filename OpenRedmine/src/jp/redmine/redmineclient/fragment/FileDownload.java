package jp.redmine.redmineclient.fragment;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineAttachmentModel;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.form.RedmineDownloadForm;
import jp.redmine.redmineclient.model.ConnectionModel;
import jp.redmine.redmineclient.param.AttachmentArgument;
import jp.redmine.redmineclient.param.IssueArgument;
import jp.redmine.redmineclient.task.SelectAttachmentTask;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

public class FileDownload extends OrmLiteFragment<DatabaseCacheHelper> {
	static private final String TAG = FileDownload.class.getSimpleName();
	private RedmineDownloadForm form;
	private SelectAttachmentTask task;

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
		return inflater.inflate(R.layout.downloaditem, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		form = new RedmineDownloadForm(getView());
		form.buttonDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AttachmentArgument instance = new AttachmentArgument();
				instance.setArgument(getArguments());
				
				RedmineAttachmentModel modelAttachemnt = new RedmineAttachmentModel(getHelper());
				try {
					RedmineAttachment attachment = modelAttachemnt.fetchById(instance.getConnectionId(), instance.getAttachmentId());
					task = new Downloader();
					task.execute(attachment);
				} catch (SQLException e) {
					Log.e(TAG,"onClick",e);
				}
				
			}
		});
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
	
	protected String getDownloadDir(){
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
				+ File.separator + getActivity().getPackageName();
	}
	
	class Downloader extends SelectAttachmentTask{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			form.progress(0);

			File directory = new File(savefolder);
			if(!directory.exists()){
				if(!directory.mkdirs()){
					Toast.makeText(getActivity(), "failed to create folder " + savefolder, Toast.LENGTH_SHORT).show();
					cancel(false);
				}
			}
		}
		
		@Override
		protected void onProgress(int max, int proc) {
			super.onProgress(max, proc);
			form.progress(proc);
		}
		
		@Override
		protected void onPostExecute(List<RedmineAttachment> result) {
			super.onPostExecute(result);
			form.progress(null);
			for(RedmineAttachment item : result){
				Intent intent = new Intent(Intent.ACTION_VIEW);
				String mimetype =MimeTypeMap.getSingleton().getMimeTypeFromExtension(item.getFilenameExt());
				Log.d(TAG,"file: " + item.getFile().getPath() + " mimetype: " + mimetype + " -- " + item.getContentType());
				intent.setDataAndType(Uri.fromFile(item.getFile()), mimetype);
				
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				getActivity().startActivity(intent);
			}
		}
		public Downloader(){
			super();
			this.savefolder = getDownloadDir();
			IssueArgument intent = new IssueArgument();
			intent.setArgument(getArguments());
			int connectionid = intent.getConnectionId();
			RedmineConnection connection = null;
			ConnectionModel mConnection = new ConnectionModel(getActivity());
			connection = mConnection.getItem(connectionid);
			mConnection.finalize();
			this.connection = connection;
		}
	}
}
