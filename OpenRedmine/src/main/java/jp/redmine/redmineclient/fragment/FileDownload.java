package jp.redmine.redmineclient.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineAttachmentModel;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.fragment.form.DownloadForm;
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
		form = new DownloadForm(getView());
		form.buttonDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AttachmentArgument instance = new AttachmentArgument();
				instance.setArgument(getArguments());
				RedmineAttachmentModel modelAttachemnt = new RedmineAttachmentModel(getHelper());
				try {
					RedmineAttachment attachment = modelAttachemnt.fetchById(instance.getConnectionId(), instance.getAttachmentId());

					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri uri = Attachment.getUrl(attachment.getId());
					intent.setData(uri);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					getActivity().startActivity(intent);

				} catch (ActivityNotFoundException e) {
					Toast.makeText(getActivity(), R.string.activity_not_found, Toast.LENGTH_SHORT).show();
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

}
