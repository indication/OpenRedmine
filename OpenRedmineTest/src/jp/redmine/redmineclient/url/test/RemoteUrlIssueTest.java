package jp.redmine.redmineclient.url.test;

import jp.redmine.redmineclient.url.RemoteUrl.requests;
import jp.redmine.redmineclient.url.RemoteUrlIssue;
import junit.framework.TestCase;

public class RemoteUrlIssueTest extends TestCase {

	private final String testurl = "http://test/";
	public RemoteUrlIssueTest() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public RemoteUrlIssueTest(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void testRaw(){
		RemoteUrlIssue issue = new RemoteUrlIssue();
		issue.setupRequest(requests.xml);
		assertEquals(testurl + "/issues.json",
				issue.getUrl(testurl).toString());
	}
}
