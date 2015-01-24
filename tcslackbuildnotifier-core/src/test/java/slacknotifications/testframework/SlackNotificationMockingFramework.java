package slacknotifications.testframework;

import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.jdom.JDOMException;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;

import java.io.File;
import java.io.IOException;

public interface SlackNotificationMockingFramework {
	
	public SBuildServer getServer();
	public SRunningBuild getRunningBuild();
	public SBuildType getSBuildType();
	public SBuildType getSBuildTypeFromSubProject();
	public SlackNotificationConfig getSlackNotificationConfig();
	public SlackNotificationPayloadContent getSlackNotificationContent();
	public SlackNotificationPayloadManager getSlackNotificationPayloadManager();
	public SlackNotificationProjectSettings getSlackNotificationProjectSettings();
	public void loadSlackNotificationConfigXml(File xmlConfigFile) throws JDOMException, IOException;
	public void loadSlackNotificationProjectSettingsFromConfigXml(File xmlConfigFile) throws IOException, JDOMException;

}
