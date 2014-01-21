package jp.redmine.redmineclient.param;

public class WikiArgument extends IssueArgument {
	public static final String WIKITITLE = "WIKITITLE";

	public void setWikiTitle(String id){
		setArg(WIKITITLE,id);
	}
	public String getWikiTitle(){
		return getArg(WIKITITLE,"");
	}

	@Override
	public void importArgument(Core arg) {
		setWikiTitle(((WikiArgument)arg).getWikiTitle());
		super.importArgument(arg);
	}
}
