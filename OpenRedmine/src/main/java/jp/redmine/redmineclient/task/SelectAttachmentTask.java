package jp.redmine.redmineclient.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.external.lib.PlaceHolder;

import org.xmlpull.v1.XmlPullParserException;

import android.net.Uri;

public class SelectAttachmentTask extends SelectDataTask<List<RedmineAttachment>,RedmineAttachment> {

	protected RedmineConnection connection;
	protected String savefolder;
	public SelectAttachmentTask(RedmineConnection con,String savefolder){
		this.connection = con;
		this.savefolder = savefolder;
	}


	public SelectAttachmentTask() {
	}

	@Override
	protected List<RedmineAttachment> doInBackground(RedmineAttachment... params) {
		List<RedmineAttachment> paths = new ArrayList<RedmineAttachment>();
		final PlaceHolder<Integer> max = new PlaceHolder<Integer>(0);
		final PlaceHolder<Integer> progress = new PlaceHolder<Integer>(0);
		for(RedmineAttachment item : params){
			max.item += item.getFilesize();
		}
		notifyProgress(max.getItem(), 0);
		
		SelectDataTaskConnectionHandler client = new SelectDataTaskRedmineConnectionHandler(connection);
		for(RedmineAttachment item : params){
			final File fl = new File(savefolder, item.getLocalFileName());
			//if(fl.getFreeSpace() < item.getFilesize())
			//	continue;
			boolean result = false;
			if(!fl.exists()){
	
				Uri data = Uri.parse(item.getContentUrl());
				SelectDataTaskDataHandler handler = new SelectDataTaskDataHandler() {
					
					@Override
					public void onContent(InputStream stream) throws XmlPullParserException,
							IOException, SQLException {
						if(fl.exists())
							fl.delete();
						OutputStream output = new FileOutputStream(fl);
						byte buffer[] = new byte[1024];
						int count;
						InputStream input = new BufferedInputStream(stream);
						while((count = input.read(buffer)) != -1){
							progress.item += count;
							notifyProgress(max.getItem(), progress.getItem());
							output.write(buffer,0,count);
						}
						output.flush();
						output.close();
					}
				};
				result = fetchData(RemoteType.get, client, data.buildUpon(), handler, null);
			} else {
				result = true;
				progress.item += item.getFilesize();
				notifyProgress(max.getItem(), progress.getItem());
			}
			if(result){
				fl.deleteOnExit();
				item.setFile(fl);
				paths.add(item);
			}
		}
		client.close();

		notifyProgress(max.getItem(), max.getItem());
		return paths;
	}

	@Override
	protected void onErrorRequest(int statuscode) {

	}

	@Override
	protected void onProgress(int max, int proc) {

	}

}
