package jp.redmine.redmineclient.fragment.form;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

public class DownloadForm extends FormHelper {
	public TextView textSize;
	public TextView textSubject;
	public TextView textCreated;
	public TextView textAuthor;
	public TextView textProgress;
	public TableRow rowProgress;
	public ProgressBar progressBar;
	public Button buttonDownload;
	public DownloadForm(View activity){
		this.setup(activity);
		this.setupEvents();
	}


	public void setup(View view){
		textSize = (TextView)view.findViewById(R.id.textSize);
		textSubject = (TextView)view.findViewById(R.id.textSubject);
		textCreated = (TextView)view.findViewById(R.id.textCreated);
		textAuthor = (TextView)view.findViewById(R.id.textAuthor);
		textProgress = (TextView)view.findViewById(R.id.textProgress);
		rowProgress = (TableRow)view.findViewById(R.id.rowProgress);
		progressBar = (ProgressBar)view.findViewById(R.id.progress);
		buttonDownload = (Button)view.findViewById(R.id.buttonDownload);
	}

	public void setupEvents(){

	}
	
	public void progress(Integer size){
		if(size == null || size >= progressBar.getMax()){
			buttonDownload.setEnabled(true);
			rowProgress.setVisibility(View.GONE);
		} else {
			if(rowProgress.getVisibility() == View.GONE){
				buttonDownload.setEnabled(false);
				rowProgress.setVisibility(View.VISIBLE);
			}
			//show animation
			if(size == 0){
				progressBar.setIndeterminate(true);
				textProgress.setText("");
			} else {
				if(TextUtils.isEmpty(textProgress.getText()))
					progressBar.setIndeterminate(false);
				progressBar.setProgress(size);
				int current = Math.round((float)(size*100)/progressBar.getMax());
				textProgress.setText(textProgress.getContext().getString(R.string.format_progress, current));
			}
		}
	}

	public void setValue(RedmineAttachment rd){
		textSubject.setText(rd.getFilename());
		progressBar.setMax(rd.getFilesize());
		textSize.setText(String.valueOf(rd.getFilesize()));
		setDateTime(textCreated, rd.getCreated());
		setMasterName(textAuthor, rd.getUser());
	}


}

