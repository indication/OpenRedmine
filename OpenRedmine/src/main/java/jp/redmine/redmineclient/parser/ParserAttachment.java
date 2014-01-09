package jp.redmine.redmineclient.parser;

import java.io.IOException;
import java.sql.SQLException;

import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineUser;
import jp.redmine.redmineclient.entity.TypeConverter;

import org.xmlpull.v1.XmlPullParserException;

public class ParserAttachment extends BaseParserInternal<RedmineIssue,RedmineAttachment> {

	@Override
	protected String getProveTagName() {
		return "attachment";
	}

	@Override
	protected RedmineAttachment getNewProveTagItem() {
		return new RedmineAttachment();
	}
	@Override
	protected void parseInternal(RedmineIssue con, RedmineAttachment attachment)
			throws XmlPullParserException, IOException, SQLException{


		if("id".equalsIgnoreCase(xml.getName())){
			attachment.setAttachmentId(getTextInteger());
		} else if(equalsTagName("author")){
			RedmineUser user = new RedmineUser();
			setMasterRecord(user);
			attachment.setUser(user);
		} else if(equalsTagName("filename")){
			attachment.setFilename(getNextText());
		} else if(equalsTagName("filesize")){
			attachment.setFilesize(getTextInteger());
		} else if(equalsTagName("content_type")){
			attachment.setContentType(getNextText());
		} else if(equalsTagName("description")){
			attachment.setDescription(getNextText());
		} else if(equalsTagName("content_url")){
			attachment.setContentUrl(getNextText());
		} else if(equalsTagName("created_on")){
			attachment.setCreated(TypeConverter.parseDateTime(getNextText()));
		} else if(equalsTagName("updated_on")){
			attachment.setModified(TypeConverter.parseDateTime(getNextText()));
		}

	}


	@Override
	protected void onTagEnd(RedmineIssue con)
		throws XmlPullParserException, IOException,SQLException {
		// stop parse appears end of the tag.
		if(equalsTagName("attachments")){
			haltParse();
		} else {
			super.onTagEnd(con);
		}
	}
}
