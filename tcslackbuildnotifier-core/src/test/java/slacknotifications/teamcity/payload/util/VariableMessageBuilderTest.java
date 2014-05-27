package slacknotifications.teamcity.payload.util;

import static org.mockito.Mockito.mock;

import java.util.SortedMap;
import java.util.TreeMap;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SFinishedBuild;

import org.junit.Before;
import org.junit.Test;

import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.MockSBuildType;
import slacknotifications.teamcity.MockSProject;
import slacknotifications.teamcity.MockSRunningBuild;
import slacknotifications.teamcity.payload.SlackNotificationPayloadDefaultTemplates;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;

public class VariableMessageBuilderTest {
	
	MockSBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
	MockSRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, "SubVersion", Status.NORMAL, "Running", "TestBuild01");
	SFinishedBuild previousSuccessfulBuild = mock(SFinishedBuild.class);
	MockSProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
	SBuildServer sBuildServer;
	SortedMap<String, String> extraParameters;

	@Before
	public void setup(){
		sBuildType.setProject(sProject);
		extraParameters = new TreeMap<String, String>();
		sBuildServer = mock(SBuildServer.class);
	}
	
	@Test
	public void testBuild() {
		SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(sBuildServer, sRunningBuild, previousSuccessfulBuild, BuildStateEnum.BEFORE_BUILD_FINISHED, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates());
		VariableMessageBuilder builder = VariableMessageBuilder.create("This is a test ${buildFullName}", new SlackNotificationBeanUtilsVariableResolver(content));
		System.out.println(builder.build());
		System.out.println(content.getBuildFullName());
	}

}
