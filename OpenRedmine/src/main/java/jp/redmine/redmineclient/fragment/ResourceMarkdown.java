package jp.redmine.redmineclient.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.j256.ormlite.android.apptools.OrmLiteFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.redmine.redmineclient.BuildConfig;
import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.activity.handler.WebviewActionInterface;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.form.helper.WebViewHelper;
import jp.redmine.redmineclient.form.helper.WikiType;
import jp.redmine.redmineclient.fragment.helper.ActivityHandler;
import jp.redmine.redmineclient.param.ResourceArgument;

public class ResourceMarkdown extends OrmLiteFragment<DatabaseCacheHelper> {
	private static final String TAG = ResourceMarkdown.class.getSimpleName();
	private WebViewHelper webViewHelper;
	private WebView webView;

	private WebviewActionInterface mListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public ResourceMarkdown(){
		super();
	}

	static public ResourceMarkdown newInstance(ResourceArgument arg){
		ResourceMarkdown fragment = new ResourceMarkdown();
		fragment.setArguments(arg.getArgument());
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mListener = ActivityHandler.getHandler(getActivity(), WebviewActionInterface.class);
		webViewHelper.setAction(mListener);
		webViewHelper.setup(webView);
		ResourceArgument arg = new ResourceArgument();
		arg.setArgument(getArguments());
		if(arg.getResource() != null) {
			try {
				loadWebView(arg.getResource());
			} catch (IOException e) {
				Log.e(TAG, "onActivityCreated", e);
			}
		}
	}

	public void loadWebView(int resource) throws IOException {
		// get input data
		InputStream stream = getResources().openRawResource(resource);
		StringBuilder text = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String str;
		String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
		while((str = reader.readLine()) != null){
			text.append(str
					.replace("$current_version$", BuildConfig.VERSION_NAME)
					.replace("$current_build$", versionCode)
			);
			text.append("\n");
		}
		webViewHelper.setContent(webView, text.toString(), WikiType.Markdown);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_webview, container, false);
		webView = (WebView) view.findViewById(R.id.webView);
		webViewHelper = new WebViewHelper();
		return view;
	}

}
