package slacknotifications.testframework;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.jdom.JDOMException;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildHistory;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationImpl;
import slacknotifications.teamcity.*;
import slacknotifications.teamcity.SlackNotificationListener;
import slacknotifications.teamcity.payload.SlackNotificationPayload;
import slacknotifications.teamcity.payload.SlackNotificationPayloadDefaultTemplates;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.payload.content.ExtraParametersMap;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;
import slacknotifications.teamcity.payload.format.SlackNotificationPayloadJson;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;
import slacknotifications.testframework.util.ConfigLoaderUtil;

public class SlackNotificationMockingFrameworkImpl implements SlackNotificationMockingFramework {
	
	SlackNotificationPayloadContent content;
	SlackNotificationConfig slackNotificationConfig;
	SBuildServer sBuildServer = mock(SBuildServer.class);
	BuildHistory buildHistory = mock(BuildHistory.class);
	ProjectSettingsManager settings = mock(ProjectSettingsManager.class);
	ProjectManager projectManager = mock(ProjectManager.class);
	SlackNotificationMainSettings configSettings = mock(SlackNotificationMainSettings.class);
	SlackNotificationPayloadManager manager = mock(SlackNotificationPayloadManager.class);
	SlackNotificationPayload payload = new SlackNotificationPayloadJson(manager);
	SlackNotificationProjectSettings projSettings;
	SlackNotificationFactory factory = mock(SlackNotificationFactory.class);
	SlackNotification slacknotification = mock (SlackNotification.class);
	SlackNotification slackNotificationImpl;
	SlackNotification spySlackNotification;
	SFinishedBuild previousSuccessfulBuild = mock(SFinishedBuild.class);
	SFinishedBuild previousFailedBuild = mock(SFinishedBuild.class);
	List<SFinishedBuild> finishedSuccessfulBuilds = new ArrayList<SFinishedBuild>();
	List<SFinishedBuild> finishedFailedBuilds = new ArrayList<SFinishedBuild>();
	SBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
	SBuildType sBuildType02 = new MockSBuildType("Test Build-2", "A Test Build 02", "bt2");
	SBuildType sBuildType03 = new MockSBuildType("Test Build-2", "A Test Build 03", "bt3");
	SRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, "SubVersion", Status.NORMAL, "Running", "TestBuild01");
	SProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
	SProject sProject02 = new MockSProject("Test Project 02", "A test project 02", "project2", "TestProjectNumber02", sBuildType);
	SProject sProject03 = new MockSProject("Test Project 03", "A test sub project 03", "project3", "TestProjectNumber02_TestProjectNumber03", sBuildType);
	
	SBuildType build2 = mock(SBuildType.class);
	SBuildType build3 = mock(SBuildType.class);
	
	SlackNotificationListener whl;
	SortedMap<String, String> extraParameters;
	BuildStateEnum buildstateEnum;
	
	private SlackNotificationMockingFrameworkImpl() {
		slackNotificationImpl = new SlackNotificationImpl();
		spySlackNotification = spy(slackNotificationImpl);
		whl = new SlackNotificationListener(sBuildServer, settings, configSettings, manager, factory);
		projSettings = new SlackNotificationProjectSettings();
		when(factory.getSlackNotification()).thenReturn(spySlackNotification);
		when(manager.isRegisteredFormat("JSON")).thenReturn(true);
		when(manager.getFormat("JSON")).thenReturn(payload);
		when(manager.getServer()).thenReturn(sBuildServer);
		when(projectManager.findProjectById("project01")).thenReturn(sProject);
		when(sBuildServer.getHistory()).thenReturn(buildHistory);
		when(sBuildServer.getRootUrl()).thenReturn("http://test.server");
		when(sBuildServer.getProjectManager()).thenReturn(projectManager);
		when(previousSuccessfulBuild.getBuildStatus()).thenReturn(Status.NORMAL);
		when(previousSuccessfulBuild.isPersonal()).thenReturn(false);
		when(previousFailedBuild.getBuildStatus()).thenReturn(Status.FAILURE);
		when(previousFailedBuild.isPersonal()).thenReturn(false);
		finishedSuccessfulBuilds.add(previousSuccessfulBuild);
		finishedFailedBuilds.add(previousFailedBuild);
		((MockSBuildType) sBuildType).setProject(sProject);
		when(settings.getSettings(sRunningBuild.getProjectId(), "slackNotifications")).thenReturn(projSettings);
		
		when(build2.getBuildTypeId()).thenReturn("bt2");
		when(build2.getInternalId()).thenReturn("bt2");
		when(build2.getName()).thenReturn("This is Build 2");
		when(build3.getBuildTypeId()).thenReturn("bt3");
		when(build3.getInternalId()).thenReturn("bt3");
		when(build3.getName()).thenReturn("This is Build 3");
		((MockSProject) sProject).addANewBuildTypeToTheMock(build2);
		((MockSProject) sProject).addANewBuildTypeToTheMock(build3);
		((MockSProject) sProject02).addANewBuildTypeToTheMock(sBuildType02);
		((MockSProject) sProject03).addANewBuildTypeToTheMock(sBuildType03);
		((MockSProject) sProject03).setParentProject(sProject02);
		((MockSProject) sProject02).addChildProjectToMock(sProject03);
		whl.register();
		
	}

	public static SlackNotificationMockingFramework create(BuildStateEnum buildState, ExtraParametersMap extraParameters) {
		SlackNotificationMockingFrameworkImpl framework = new SlackNotificationMockingFrameworkImpl();
		framework.buildstateEnum = buildState;
		framework.extraParameters = extraParameters;
		framework.content = new SlackNotificationPayloadContent(framework.sBuildServer, framework.sRunningBuild, framework.previousSuccessfulBuild, buildState, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates());
		return framework;
	}

	@Override
	public SBuildServer getServer() {
		return sBuildServer;
	}

	@Override
	public SRunningBuild getRunningBuild() {
		return sRunningBuild;
	}
	
	@Override
	public SlackNotificationPayloadContent getSlackNotificationContent() {
		return content;
	}

	@Override
	public void loadSlackNotificationConfigXml(File xmlConfigFile) throws JDOMException, IOException {
		//slackNotificationConfig = new SlackNotificationConfig(ConfigLoaderUtil.getFullConfigElement(xmlConfigFile));
		slackNotificationConfig = ConfigLoaderUtil.getFirstSlackNotificationInConfig(xmlConfigFile);
		this.content = new SlackNotificationPayloadContent(this.sBuildServer, this.sRunningBuild, this.previousSuccessfulBuild, this.buildstateEnum, extraParameters, slackNotificationConfig.getEnabledTemplates());
		
	}
	
	@Override
	public void loadSlackNotificationProjectSettingsFromConfigXml(File xmlConfigFile) throws IOException, JDOMException{
		projSettings.readFrom(ConfigLoaderUtil.getFullConfigElement(xmlConfigFile).getChild("slacknotifications"));
	}
	
	@Override
	public SlackNotificationConfig getSlackNotificationConfig() {
		return slackNotificationConfig;
	}

	@Override
	public SlackNotificationProjectSettings getSlackNotificationProjectSettings() {
		return projSettings;
	}

	@Override
	public SlackNotificationPayloadManager getSlackNotificationPayloadManager() {
		return manager;
	}

	@Override
	public SBuildType getSBuildType() {
		return sBuildType;
	}

	@Override
	public SBuildType getSBuildTypeFromSubProject() {
		return sBuildType03;
	}

}
