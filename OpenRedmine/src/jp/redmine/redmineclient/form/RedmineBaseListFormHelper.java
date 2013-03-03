package jp.redmine.redmineclient.form;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

abstract public class RedmineBaseListFormHelper {

	static final private String stateListViewFirstVisiblePosition = "ListViewFirstVisiblePosition";
	static final private String stateListViewTopPosition = "ListViewTopPosition";
	public ListView list;
	public View viewHeader;
	public View viewFooter;
	private int stateFirstPos = 0;
	private int statePosTop = 0;
	private boolean isRestoreState = true;
	public void setList(ListView listview){
		list = listview;

		if(viewHeader != null)
			list.addHeaderView(viewHeader);
		if(viewFooter != null)
			list.addFooterView(viewFooter);
	}

	public void setHeader(View header, Boolean firstVisible){
		if(list != null){
			if(viewHeader != null){
				list.removeHeaderView(viewHeader);
			}
			list.addHeaderView(header);
		}
		viewHeader = header;
		if(firstVisible != null)
			setHeaderViewVisible(firstVisible);
	}
	public void setFooter(View footer, Boolean firstVisible){
		if(list != null){
			if(viewFooter != null){
				list.removeFooterView(viewFooter);
			}
			list.addFooterView(footer);
		}
		viewFooter = footer;
		if(firstVisible != null)
			setFooterViewVisible(firstVisible);
	}
	public void setHeaderViewVisible(boolean isVisible){
		if(viewHeader == null)
			return;
		viewHeader.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}
	public void setFooterViewVisible(boolean isVisible){
		if(viewFooter == null)
			return;
		viewFooter.setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}

	public void onSaveInstanceState(Bundle outState) {
		if(outState == null || list == null)
			return;
		// save the listview status
		if (list.getChildCount() > 0) {
			stateFirstPos = list.getFirstVisiblePosition();
			statePosTop = list.getChildAt(0).getTop();
			outState.putInt(stateListViewFirstVisiblePosition, stateFirstPos);
			outState.putInt(stateListViewTopPosition, statePosTop);
			isRestoreState = true;
		}
	}
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			stateFirstPos = savedInstanceState.getInt(stateListViewFirstVisiblePosition);
			statePosTop = savedInstanceState.getInt(stateListViewTopPosition);
			isRestoreState = true;
		}
	}
	protected void restoreScrollState(){
		if(list == null)
			return;
		if (list.getChildCount() > 0 && isRestoreState) {
			list.setSelectionFromTop(stateFirstPos,statePosTop);
			isRestoreState = false;
		}
	}
}

