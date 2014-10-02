package jp.redmine.redmineclient.parser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineProject;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.RedmineWiki;
import jp.redmine.redmineclient.entity.TypeConverter;

public class ParserWiki extends BaseParserInternal<RedmineProject,RedmineWiki> {
	private ParserAttachment parserAttachment = new ParserAttachment();

	@Override
	protected String getProveTagName() {
		return "wiki_page";
	}

	@Override
	protected RedmineWiki getNewProveTagItem() {
		return new RedmineWiki();
	}

	@Override
	protected void parseInternal(RedmineProject con, RedmineWiki item)
			throws XmlPullParserException, IOException, SQLException {
		if(xml.getDepth() < 2)
			return;
		if(equalsTagName("title")){
			item.setTitle(getNextText());
		} else if(equalsTagName("version")){
			item.setVersion(getTextInteger());
		} else if(equalsTagName("text")){
			item.setBody(getNextText());
		} else if(equalsTagName("author")){
			RedmineUser tk = new RedmineUser();
			setMasterRecord(tk);
			item.setAuthor(tk);
		} else if(equalsTagName("parent")){
			item.setParent(getAttributeString("title"));
		} else if(equalsTagName("created_on")){
			item.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if(equalsTagName("updated_on")){
			item.setModified(TypeConverter.parseDateTime(getNextText()));
		} else if(equalsTagName("attachments")){
			final List<RedmineAttachment> attachments = new ArrayList<RedmineAttachment>();
			parserAttachment.setXml(xml);
			DataCreationHandler<RedmineIssue, RedmineAttachment> handler =
					new DataCreationHandler<RedmineIssue, RedmineAttachment>() {
						@Override
						public void onData(RedmineIssue info, RedmineAttachment data)
								throws SQLException {
							attachments.add(data);
						}
					};

			parserAttachment.registerDataCreation(handler);
			parserAttachment.parse(null);
			parserAttachment.unregisterDataCreation(handler);
			item.setAttachments(attachments);

		}

	}
}
