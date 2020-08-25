package slacknotifications.teamcity.payload.util;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import org.junit.Before;
import org.junit.Test;
import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.MockSBuildType;
import slacknotifications.teamcity.MockSProject;
import slacknotifications.teamcity.MockSRunningBuild;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;

import static org.mockito.Mockito.mock;

public class VariableMessageBuilderTest {
	
	MockSBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
	MockSRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, "SubVersion", Status.NORMAL, "Running", "TestBuild01", false);
	SFinishedBuild previousSuccessfulBuild = mock(SFinishedBuild.class);
	MockSProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
	SBuildServer sBuildServer;

	@Before
	public void setup(){
		sBuildType.setProject(sProject);
		sBuildServer = mock(SBuildServer.class);
	}
	
	@Test
	public void testBuild() {
		SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(sBuildServer, sRunningBuild, previousSuccessfulBuild, BuildStateEnum.BEFORE_BUILD_FINISHED);
		VariableMessageBuilder builder = VariableMessageBuilder.create("This is a test ${buildFullName}", new SlackNotificationBeanUtilsVariableResolver(content));
		System.out.println(builder.build());
		System.out.println(content.getBuildFullName());
	}

}
