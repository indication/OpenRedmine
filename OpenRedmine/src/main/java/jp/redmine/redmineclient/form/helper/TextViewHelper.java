package jp.redmine.redmineclient.form.helper;

import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;

public class TextViewHelper {
	private Pattern patternIntent = Pattern.compile(TextileHelper.URL_PREFIX);
	private WebviewActionInterface action;
	public void setAction(WebviewActionInterface act){
		action = act;
	}

	public void setContent(TextView view, final int connectionid, final long project, final String text){
		String content = TextileHelper.getHtml(connectionid, project, text);
		setTextViewHTML(view, content, new ClickLink() {
			@Override
			public void onClick(View view, URLSpan span) {
				String url = span.getURL();
				if(TextUtils.isEmpty(url))
					return;
				Matcher m = patternIntent.matcher(url);
				if (m.find()) {
					TextileHelper.kickAction(action, m.replaceAll(""));
				} else if (action != null) {
					action.url(url);
				}
			}
		});
	}

	public void setup(TextView text){
		text.setLinksClickable(true);
		text.setMovementMethod(LinkMovementMethod.getInstance());
	}
	public interface ClickLink{
		public void onClick(View view, URLSpan span);
	}

	/**
	 * Setup TextView with HTML
	 * Author: Zane Claes
	 * Inherit from: http://stackoverflow.com/questions/12418279/android-textview-with-clickable-links-how-to-capture-clicks
	 * @param text Target
	 * @param html Input HTML String
	 * @param handler On click the link event
	 */
	static void setTextViewHTML(TextView text, String html, final ClickLink handler)
	{
		CharSequence sequence = Html.fromHtml(html);
		SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
		for(final URLSpan span :  strBuilder.getSpans(0, sequence.length(), URLSpan.class)) {
			int start = strBuilder.getSpanStart(span);
			int end = strBuilder.getSpanEnd(span);
			int flags = strBuilder.getSpanFlags(span);
			ClickableSpan clickable = new ClickableSpan() {
				@Override
				public void onClick(View view) {
					handler.onClick(view, span);
				}
			};
			strBuilder.removeSpan(span);
			strBuilder.setSpan(clickable, start, end, flags);
		}
		text.setText(strBuilder);
	}
}
