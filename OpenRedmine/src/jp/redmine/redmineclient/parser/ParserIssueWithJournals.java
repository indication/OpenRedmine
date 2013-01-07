package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineProject;

public class ParserIssueWithJournals extends ParserIssue {

	private List<RedmineJournal> journals;
	private RedmineJournal journal;
	private int lastdepthJournal;
	@Override
	protected void parseInternal(RedmineProject con, RedmineIssue item)
			throws XmlPullParserException, IOException{
		if(journal!= null && xml.getDepth() > lastdepthJournal)
			parseInternalJournal(con,item);
		else
			super.parseInternal(con, item);
	}
	protected void parseInternalJournal(RedmineProject con, RedmineIssue item)
	throws XmlPullParserException, IOException{


		if(equalsTagName("")){

		} else if(equalsTagName("")){

		}


	}


	protected void onParseStart(RedmineProject con)
	throws XmlPullParserException, IOException, SQLException {
		journal = null;
		journals = null;
		lastdepthJournal = 0;
		super.onParseStart(con);
	}
	@Override
	protected void onTagStart(RedmineProject con)
		throws XmlPullParserException, IOException, SQLException {
		if(equalsTagName("journals")){
			journals = new ArrayList<RedmineJournal>();
		} else if(equalsTagName("journal")){
			journal = new RedmineJournal();
			lastdepthJournal = xml.getDepth();
		} else if(journals != null){
			super.onTagStart(con);
		}
	}

	@Override
	protected void onTagEnd(RedmineProject con)
		throws XmlPullParserException, IOException,SQLException {
		if(equalsTagName("journal")){
			journals.add(journal);
			journal = null;
		} else if(equalsTagName("journals") && lastdepthJournal == xml.getDepth()){
			journals = null;
			lastdepthJournal = 0;
		}
		super.onTagEnd(con);
	}

}
