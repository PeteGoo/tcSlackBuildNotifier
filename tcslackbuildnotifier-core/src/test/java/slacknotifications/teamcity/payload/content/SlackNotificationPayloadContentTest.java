package slacknotifications.teamcity.payload.content;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.testframework.SlackNotificationMockingFramework;
import slacknotifications.testframework.SlackNotificationMockingFrameworkImpl;

public class SlackNotificationPayloadContentTest {

	SlackNotificationMockingFramework framework;

	@Before 
	public void setup() throws JDOMException, IOException{
	}

    @Ignore
	@Test
	public void testGetBuildStatusHtml() throws JDOMException, IOException {
		framework = SlackNotificationMockingFrameworkImpl.create(BuildStateEnum.BUILD_FINISHED);
		framework.loadSlackNotificationConfigXml(new File("src/test/resources/project-settings-test-all-states-enabled.xml"));
		
		final String htmlStatus = "<span class=\"tcSlackNotificationsMessage\"><a href=\"http://test.server/project.html?projectId=ATestProject\">Test Project</a> :: <a href=\"http://test.server/viewType.html?buildTypeId=TestBuild\">Test Build</a> # <a href=\"http://test.server/viewLog.html?buildTypeId=TestBuild&buildId=123456\"><strong>TestBuild01</strong></a> has <strong>finished</strong> with a status of <a href=\"http://test.server/viewLog.html?buildTypeId=TestBuild&buildId=123456\"> <strong>success</strong></a> and was triggered by <strong>SubVersion</strong></span>";
		//						   <span class="tcSlackNotificationsMessage"><a href="http://test.server/project.html?projectId=project1">Test Project</a> :: <a href="http://test.server/viewType.html?buildTypeId=bt1">Test Build</a> # <a href="http://test.server/viewLog.html?buildTypeId=bt1&buildId=123456"><strong>TestBuild01</strong></a> has <strong>finished</strong> with a status of <a href="http://test.server/viewLog.html?buildTypeId=bt1&buildId=123456"> <strong>success</strong></a> and was triggered by <strong>SubVersion</strong></span>

        SlackNotificationPayloadContent content = framework.getSlackNotificationContent();
		System.out.println(content.getBuildStatusHtml());
		assertTrue(content.getBuildStatusHtml().equals(htmlStatus));
	}

	@Test
	public void testCustomBuildStatusHtml() throws JDOMException, IOException {
		framework = SlackNotificationMockingFrameworkImpl.create(BuildStateEnum.BUILD_FINISHED);
		framework.loadSlackNotificationConfigXml(new File("src/test/resources/project-settings-test-all-states-enabled-with-branch-and-custom-templates.xml"));
		SlackNotificationPayloadContent content = framework.getSlackNotificationContent();
		System.out.println(content.getBuildStatusHtml());
		assertTrue(content.getBuildStatusHtml().equals("master ATestProject"));
	}
}
