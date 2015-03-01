package slacknotifications.teamcity.extension.bean;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import org.jdom.JDOMException;
import org.junit.Test;
import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;
import slacknotifications.testframework.SlackNotificationMockingFramework;
import slacknotifications.testframework.SlackNotificationMockingFrameworkImpl;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.mockito.Mockito.mock;

public class ProjectSlackNotificationsBeanTest {

	SortedMap<String, String> map = new TreeMap<String, String>();
	SlackNotificationMockingFramework framework;
    SBuildServer sBuildServer = mock(SBuildServer.class);

	@Test
	public void JsonSerialisationTest() throws JDOMException, IOException {
        ServerPaths serverPaths = mock(ServerPaths.class);
        SlackNotificationMainSettings myMainSettings = new SlackNotificationMainSettings(sBuildServer, serverPaths);
        framework = SlackNotificationMockingFrameworkImpl.create(BuildStateEnum.BUILD_FINISHED);
		framework.loadSlackNotificationProjectSettingsFromConfigXml(new File("../tcslackbuildnotifier-core/src/test/resources/project-settings-test-all-states-enabled-with-specific-builds.xml"));
		ProjectSlackNotificationsBean slacknotificationsConfig = ProjectSlackNotificationsBean.build(framework.getSlackNotificationProjectSettings() , framework.getServer().getProjectManager().findProjectById("project01"), myMainSettings);
		System.out.println(ProjectSlackNotificationsBeanJsonSerialiser.serialise(slacknotificationsConfig));
	}
	
	@Test
	public void JsonBuildSerialisationTest() throws JDOMException, IOException {
        ServerPaths serverPaths = mock(ServerPaths.class);
        SlackNotificationMainSettings myMainSettings = new SlackNotificationMainSettings(sBuildServer, serverPaths);
        framework = SlackNotificationMockingFrameworkImpl.create(BuildStateEnum.BUILD_FINISHED);
		framework.loadSlackNotificationProjectSettingsFromConfigXml(new File("../tcslackbuildnotifier-core/src/test/resources/project-settings-test-all-states-enabled-with-specific-builds.xml"));
		ProjectSlackNotificationsBean slacknotificationsConfig = ProjectSlackNotificationsBean.build(framework.getSlackNotificationProjectSettings() ,framework.getSBuildType() ,framework.getServer().getProjectManager().findProjectById("project01"), myMainSettings);
		System.out.println(ProjectSlackNotificationsBeanJsonSerialiser.serialise(slacknotificationsConfig));
	}

}
