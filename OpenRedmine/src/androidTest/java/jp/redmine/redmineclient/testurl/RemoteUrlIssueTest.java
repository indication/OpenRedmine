package jp.redmine.redmineclient.testurl;

import junit.framework.TestCase;

import jp.redmine.redmineclient.url.RemoteUrl.requests;
import jp.redmine.redmineclient.url.RemoteUrlIssue;


public class RemoteUrlIssueTest extends TestCase {

	private final String testurl = "http://test/";
	public RemoteUrlIssueTest() {
	}

	public void testRaw(){
		RemoteUrlIssue issue = new RemoteUrlIssue();
		issue.setupRequest(requests.xml);
		assertEquals(testurl + "issues.xml",
				issue.getUrl(testurl).toString());
	}
}
