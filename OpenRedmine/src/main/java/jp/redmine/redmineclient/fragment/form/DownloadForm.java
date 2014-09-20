package jp.redmine.redmineclient.fragment.form;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.form.helper.FormHelper;

public class DownloadForm extends FormHelper {
	public TextView textSize;
	public TextView textSubject;
	public TextView textCreated;
	public TextView textAuthor;
	public Button buttonDownload;
	public Button buttonBrowser;
	public DownloadForm(View activity){
		this.setup(activity);
		this.setupEvents();
	}


	public void setup(View view){
		textSize = (TextView)view.findViewById(R.id.textSize);
		textSubject = (TextView)view.findViewById(R.id.textSubject);
		textCreated = (TextView)view.findViewById(R.id.textCreated);
		textAuthor = (TextView)view.findViewById(R.id.textAuthor);
		buttonDownload = (Button)view.findViewById(R.id.buttonDownload);
		buttonBrowser = (Button)view.findViewById(R.id.buttonBrowser);
	}

	public void setupEvents(){

	}

	public void setValue(RedmineAttachment rd){
		textSubject.setText(rd.getFilename());
		textSize.setText(String.valueOf(rd.getFilesize()));
		setDateTime(textCreated, rd.getCreated());
		setMasterName(textAuthor, rd.getUser());
	}


}

