package jp.redmine.redmineclient.url;

import android.net.Uri;
import android.text.format.DateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineFilterSortItem;

public class RemoteUrlIssues extends RemoteUrl {
	private HashMap<String,String> params = new HashMap<String,String>();

	public static void setupFilter(RemoteUrlIssues url,RedmineFilter filter, boolean isFetchAll){
		if(filter.getAssigned() != null)
			url.filterAssigned(String.valueOf(filter.getAssigned().getUserId()));
		if(filter.getAuthor() != null)
			url.filterAuthor(String.valueOf(filter.getAuthor().getUserId()));

		if(filter.getProject() != null)
			url.filterProject(String.valueOf(filter.getProject().getProjectId()));
		if(filter.getTracker() != null)
			url.filterTracker(String.valueOf(filter.getTracker().getTrackerId()));

		if(filter.getPriority() != null)
			url.filterPriority(String.valueOf(filter.getPriority().getPriorityId()));
		if(filter.getCategory() != null)
			url.filterCategory(String.valueOf(filter.getCategory().getCategoryId()));
		if(filter.getVersion() != null)
			url.filterVersion(String.valueOf(filter.getVersion().getVersionId()));
		if(filter.getStatus() != null)
			url.filterStatus(String.valueOf(filter.getStatus().getStatusId()));
		else if(isFetchAll)
			url.filterStatus("*");

		//setup sort
		for(RedmineFilterSortItem item : filter.getSortList()){
			url.addSort(item.getRemoteKey(), item.isAscending());
		}

	}
	public void filterLimit(int limit){
		params.put("limit", Integer.toString(limit));
	}
	public void filterOffset(int offset){
		params.put("offset", Integer.toString(offset));
	}

	public void filterQuery(String query){
		params.put("query_id", query);
	}

	public void filterStatus(String status){
		params.put("status_id", status);
	}
	public void filterPriority(String priority) {
		params.put("priority_id", priority);
	}
	public void filterCategory(String category) {
		params.put("category_id", category);
	}

	public void filterAssigned(String assigned_to_id){
		params.put("assigned_to_id", assigned_to_id);
	}
	public void filterAuthor(String author_id){
		params.put("author_id", author_id);
	}
	public void filterTracker(String tracker){
		params.put("tracker_id", tracker);
	}
	public void filterVersion(String version) {
		params.put("fixed_version_id", version);
	}

	public void filterProject(String status){
		params.put("project_id", status);
	}
	public void filterCreated(Date from,Date to){
		filterDate("created_on",from,to);
	}
	public void filterModified(Date from,Date to){
		filterDate("modified_on",from,to);
	}
	public void addSort(String column,boolean isAscending){
		StringBuilder sb = new StringBuilder();
		if(params.containsKey("sort")){
			sb.append(params.get("sort"));
			sb.append(",");
			params.remove("sort");
		}
		sb.append(column);
		if(!isAscending)
			sb.append(":desc");
		params.put("sort",sb.toString());
	}
	public void clearSort(){
		if(params.containsKey("sort")){
			params.remove("sort");
		}
	}

	private void filterDate(String key,Date from,Date to){
		StringBuilder sb = new StringBuilder();
		if(from == null && to != null){
			sb.append("<");
			sb.append(DateFormat.format("yyyy-MM-dd", to));
		} else if(from != null && to == null){
			sb.append(">");
			sb.append(DateFormat.format("yyyy-MM-dd", from));
		} else if(from != null && to != null){
			sb.append("<>");
			sb.append(DateFormat.format("yyyy-MM-dd", from));
			sb.append("|");
			sb.append(DateFormat.format("yyyy-MM-dd", to));
		}
		params.put(key, sb.toString());
	}

	@Override
	public Uri.Builder getUrl(String baseurl) {
		Uri.Builder url = convertUrl(baseurl);
		url.appendEncodedPath("issues."+ getExtension());
		for(Entry<String,String> data : params.entrySet()){
			if(data.getValue() != null){
				url.appendQueryParameter(data.getKey(), data.getValue());
			}
		}
		return url;
	}
}
