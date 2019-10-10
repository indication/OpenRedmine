package jp.redmine.redmineclient.adapter.form;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.TextView;

public class AttachmentForm extends FormHelper {
	public TextView textSubject;
	public TextView textSize;
	public TextView textCreated;
	protected String[] sizes = new String[]{"","k","M","G","E"};
	public AttachmentForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = view.findViewById(R.id.textSubject);
		textSize = view.findViewById(R.id.textSize);
		textCreated = view.findViewById(R.id.textCreated);

	}
	public void setValue(RedmineAttachment rd){
		textSubject.setText(rd.getFilename());
		textSize.setText(getSizeString(rd.getFilesize()));
		setDate(textCreated, rd.getCreated());
	}
	
	protected String getSizeString(Integer size){
		if(size == null)
			return "";
		BigDecimal export = new BigDecimal(size);
		BigDecimal base = new BigDecimal(1024);
		String exportname = "";
		NumberFormat df = NumberFormat.getIntegerInstance();
		export = export.setScale(1, RoundingMode.HALF_UP);
		for(String sizename : sizes){
			exportname = sizename;
			if(export.longValue() < base.longValue())
				break;
			export = export.divide(base);
		}
		return  df.format(export) + exportname;
	}

}

