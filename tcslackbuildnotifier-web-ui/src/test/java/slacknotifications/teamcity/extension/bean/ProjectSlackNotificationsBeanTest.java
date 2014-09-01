package slacknotifications.teamcity.extension.bean;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jdom.JDOMException;
import org.junit.Test;

import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.testframework.SlackNotificationMockingFramework;
import slacknotifications.testframework.SlackNotificationMockingFrameworkImpl;

public class ProjectSlackNotificationsBeanTest {

	SortedMap<String, String> map = new TreeMap<String, String>();
	SlackNotificationMockingFramework framework;

	@Test
	public void JsonSerialisationTest() throws JDOMException, IOException {
		framework = SlackNotificationMockingFrameworkImpl.create(BuildStateEnum.BUILD_FINISHED);
		framework.loadSlackNotificationProjectSettingsFromConfigXml(new File("../tcslackbuildnotifier-core/src/test/resources/project-settings-test-all-states-enabled-with-specific-builds.xml"));
		ProjectSlackNotificationsBean slacknotificationsConfig = ProjectSlackNotificationsBean.build(framework.getSlackNotificationProjectSettings() ,framework.getServer().getProjectManager().findProjectById("project01"));
		System.out.println(ProjectSlackNotificationsBeanJsonSerialiser.serialise(slacknotificationsConfig));
	}
	
	@Test
	public void JsonBuildSerialisationTest() throws JDOMException, IOException {
		framework = SlackNotificationMockingFrameworkImpl.create(BuildStateEnum.BUILD_FINISHED);
		framework.loadSlackNotificationProjectSettingsFromConfigXml(new File("../tcslackbuildnotifier-core/src/test/resources/project-settings-test-all-states-enabled-with-specific-builds.xml"));
		ProjectSlackNotificationsBean slacknotificationsConfig = ProjectSlackNotificationsBean.build(framework.getSlackNotificationProjectSettings() ,framework.getSBuildType() ,framework.getServer().getProjectManager().findProjectById("project01"));
		System.out.println(ProjectSlackNotificationsBeanJsonSerialiser.serialise(slacknotificationsConfig));
	}

}
