package jp.redmine.redmineclient.form;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.form.helper.FormHelper;
import android.view.View;
import android.widget.TextView;

public class RedmineAttachmentListItemForm extends FormHelper {
	public TextView textSubject;
	public TextView textSize;
	public TextView textModified;
	protected String[] sizes = new String[]{"","k","M","G","E"};
	public RedmineAttachmentListItemForm(View activity){
		this.setup(activity);
	}


	public void setup(View view){
		textSubject = (TextView)view.findViewById(R.id.textSubject);
		textSize = (TextView)view.findViewById(R.id.textSize);
		textModified = (TextView)view.findViewById(R.id.textModified);

	}
	public void setValue(RedmineAttachment rd){
		textSubject.setText(rd.getFilename());
		textSize.setText(getSizeString(rd.getFilesize()));
		convertDate(textModified, rd.getCreated());
	}
	
	protected String getSizeString(Integer size){
		if(size == null)
			return "";
		BigDecimal export = new BigDecimal(size);
		export.setScale(1, RoundingMode.HALF_UP);
		String laststr = "";
		for(String cnt : sizes){
			if(export.longValue() < 1024)
				break;
			laststr = cnt;
			export = export.divide(new BigDecimal(1024));
		}
		return export.toString() + laststr;
	}

}

