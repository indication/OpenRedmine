package jp.redmine.redmineclient.param;

public class FilterArgument extends ProjectArgument {
	public static final String FILTER_ID = "FILTERID";

	public void setFilterId(int id){
		setArg(FILTER_ID,id);
	}
	public int getFilterId(){
		return getArg(FILTER_ID, -1);
	}
	public boolean hasFilterId(){
		return getFilterId() != -1;
	}

	public void importArgument(FilterArgument arg) {
		setFilterId(arg.getFilterId());
		super.importArgument(arg);
	}
}
