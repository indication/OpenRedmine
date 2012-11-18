package jp.redmine.redmineclient.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.db.cache.RedmineProjectModel;
import jp.redmine.redmineclient.db.store.RedmineConnectionModel;
import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.external.lib.AuthenticationParam;
import jp.redmine.redmineclient.external.lib.ClientParam;
import jp.redmine.redmineclient.external.lib.ConnectionHelper;
import jp.redmine.redmineclient.parser.ParserIssue;
import jp.redmine.redmineclient.url.RemoteUrlIssues;
import jp.redmine.redmineclient.url.RemoteUrl.requests;
import jp.redmine.redmineclient.url.RemoteUrl.versions;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

public class IssueModel extends Connector {

	private int connection_id;
	private Long project_id;

	public IssueModel(Context context, int connectionid, Long projectid) {
		super(context);
		connection_id = connectionid;
		project_id = projectid;
	}


	public List<RedmineIssue> fetchAllData(long offset,long limit){
		final RedmineIssueModel model = new RedmineIssueModel(helperCache);
		List<RedmineIssue> issues = new ArrayList<RedmineIssue>();
		try {
			issues = model.fetchAllById(connection_id, project_id, offset, limit);
		} catch (SQLException e) {
			Log.e("SelectDataTask","doInBackground",e);
		} catch (Throwable e) {
			Log.e("SelectDataTask","doInBackground",e);
		}
		return issues;
	}
	public RedmineIssue fetchItem(int issue_id){
		final RedmineIssueModel model =
			new RedmineIssueModel(helperCache);
		RedmineIssue issue = new RedmineIssue();
		Log.d("SelectDataTask","ParserIssue Start");
		try {
			issue = model.fetchById(connection_id, issue_id);
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserIssue",e);
		}
		return issue;
	}
	protected RedmineConnection getConnection() throws SQLException{
		RedmineConnectionModel mConnection =
			new RedmineConnectionModel(helperStore);
		return mConnection.fetchById(connection_id);
	}

	protected RedmineProject getProject() throws SQLException{
		RedmineProjectModel mProject =
			new RedmineProjectModel(helperCache);
		return mProject.fetchById(project_id);
	}

	protected ClientParam getClientParam(Uri remoteurl){
		ClientParam clientparam = new ClientParam();
		if(remoteurl.getPort() <= 0){
			clientparam.setHttpPort(80);
			clientparam.setHttpsPort(443);
		} else if("https".equals(remoteurl.getScheme())){
			clientparam.setHttpPort(80);
			clientparam.setHttpsPort(remoteurl.getPort());
		} else {
			clientparam.setHttpPort(remoteurl.getPort());
			clientparam.setHttpsPort(443);
		}
		clientparam.setSLLTrustAll(true);
		clientparam.setTimeout(30000);
		return clientparam;
	}

	private boolean isGZipHttpResponse(HttpResponse response) {
		Header header = response.getEntity().getContentEncoding();
		if (header == null) return false;
		String value = header.getValue();
		return (!TextUtils.isEmpty(value) && value.contains("gzip"));
	}

	public List<RedmineIssue> fetchData(long offset,long limit) throws SQLException, XmlPullParserException, IOException{
		final RedmineFilterModel mFilter =
			new RedmineFilterModel(helperCache);
		final RedmineIssueModel mIssue =
				new RedmineIssueModel(helperCache);
		RedmineConnection connection = getConnection();
		RedmineProject proj = getProject();

		RedmineFilter filter = mFilter.fetchByCurrnt(connection.getId(), proj);
		if(filter == null)
			filter = mFilter.generateDefault(connection.getId(), proj);

		boolean isRemote = false;
		/*
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 10);	///@todo
		if(filter.getFirst() == null || cal.after(filter.getFirst()))
			filter.setFetched(0);
			*/
		if((offset+limit) > filter.getFetched())
			isRemote = true;

		List<RedmineIssue> issues = null;
		if(isRemote){
			RemoteUrlIssues url = new RemoteUrlIssues();
			RedmineFilterModel.setupUrl(url, filter);
			url.filterOffset((int)offset);
			url.filterLimit((int)limit);
			final IssueModelDataCreationHandler handler =
					new IssueModelDataCreationHandler(helperCache);

			//TODO refactor
			Uri remoteurl;
			url.setupRequest(requests.xml);
			url.setupVersion(versions.v130);
			Builder builder = url.getUrl(connection.getUrl());
			remoteurl = builder.build();


			ParserIssue parser = new ParserIssue();
			parser.registerDataCreation(handler);


			DefaultHttpClient client = ConnectionHelper.createHttpClient(getClientParam(remoteurl));
			if(connection.isAuth()){
				AuthenticationParam param = new AuthenticationParam();
				param.setId(connection.getAuthId());
				param.setPass(connection.getAuthPasswd());
				param.setAddress(remoteurl.getHost());
				if(remoteurl.getPort() <= 0){
					param.setPort("https".equals(remoteurl.getScheme()) ? 443: 80);
				} else {
					param.setPort(remoteurl.getPort());
				}
				ConnectionHelper.setupHttpClientAuthentication(client, param);
			}
			try {
				HttpGet get = new HttpGet(new URI(remoteurl.toString()));
				get.setHeader("X-Redmine-API-Key", connection.getToken());
				get.setHeader("Accept-Encoding", "gzip, deflate");
				HttpResponse response = client.execute(get);
				int status = response.getStatusLine().getStatusCode();
				Log.i("requestGet", "Url: " + get.getURI().toASCIIString());
				Log.i("requestGet", "Status: " + status);
				if (HttpStatus.SC_OK == status) {
					//response.getEntity().writeTo(cbb.getOutputStream());
					InputStream stream = response.getEntity().getContent();

					if (isGZipHttpResponse(response)) {
						stream =  new GZIPInputStream(stream);
					}
					XmlPullParser xmlPullParser = Xml.newPullParser();
					xmlPullParser.setInput(stream, "UTF-8");
					parser.setXml(xmlPullParser);
				} else {
					//TODO error
				}
			} catch (URISyntaxException e) {
				Log.e("requestGet","BackgroundProcess",e);
			} catch (ClientProtocolException e) {
				Log.e("requestGet","BackgroundProcess",e);
			} catch (IOException e) {
				Log.e("requestGet","BackgroundProcess",e);

			}


			parser.parse(proj);

			filter.setFetched(parser.getCount()+filter.getFetched());
			filter.setLast(new Date());

			mFilter.updateCurrent(filter);
		}
		issues = mIssue.fetchAllByFilter(filter,offset,limit);
		if(issues == null)
			issues = new ArrayList<RedmineIssue>();
		return issues;
	}

	public int fetchRemoteData(int offset,int limit){
		final IssueModelDataCreationHandler handler =
			new IssueModelDataCreationHandler(helperCache);

		RedmineConnection info = null;
		RedmineProject proj = null;
		try {
			info = getConnection();
			proj = getProject();
		} catch (SQLException e) {
			Log.e("SelectDataTask","ParserProject",e);
		}
		RemoteUrlIssues url = new RemoteUrlIssues();
		Fetcher<RedmineProject> fetch = new Fetcher<RedmineProject>();
		ParserIssue parser = new ParserIssue();
		parser.registerDataCreation(handler);

		url.filterProject(String.valueOf(proj.getProjectId()));
		url.filterOffset(offset);
		url.filterLimit(limit);
		Log.d("SelectDataTask","ParserProject Start");
		try {
			fetch.setRemoteurl(url);
			fetch.setParser(parser);
			fetch.fetchData(info,proj);

		} catch (XmlPullParserException e) {
			Log.e("SelectDataTask","fetchIssue",e);
		} catch (IOException e) {
			Log.e("SelectDataTask","fetchIssue",e);
		}
		return parser.getCount();
	}
}
