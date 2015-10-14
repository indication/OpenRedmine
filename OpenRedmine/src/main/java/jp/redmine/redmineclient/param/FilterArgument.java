package jp.redmine.redmineclient.param;

public class FilterArgument extends ProjectArgument {
	public static final String FILTER_ID = "FILTERID";
	public static final String FIELD_NAME = "FIELD_NAME";
	public static final String FIELD_ID = "FIELD_ID";

	public void setFilterId(int id){
		setArg(FILTER_ID,id);
	}
	public int getFilterId(){
		return getArg(FILTER_ID, -1);
	}
	public boolean hasFilterId(){
		return getFilterId() != -1;
	}

	public void setField(String name, long id){
		setArg(FIELD_NAME, name);
		setArg(FIELD_ID, id);
	}

	public boolean hasField(){
		return getFieldName() != null;
	}

	public String getFieldName(){
		return getArg(FIELD_NAME, (String)null);
	}
	public long getFieldId(){
		return getArg(FIELD_ID, -1);
	}

	public void importArgument(FilterArgument arg) {
		setFilterId(arg.getFilterId());
		if(arg.hasField()) {
			setField(arg.getFieldName(), arg.getFieldId());
		}
		super.importArgument(arg);
	}
}
