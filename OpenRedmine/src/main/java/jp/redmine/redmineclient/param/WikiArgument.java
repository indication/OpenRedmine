package jp.redmine.redmineclient.param;

public class WikiArgument extends IssueArgument {
	public static final String WIKITITLE = "WIKITITLE";

	public void setWikiTitle(String id){
		setArg(WIKITITLE,id);
	}
	public String getWikiTitle(){
		return getArg(WIKITITLE,"");
	}

	public void importArgument(WikiArgument arg) {
		setWikiTitle(arg.getWikiTitle());
		super.importArgument(arg);
	}
}
