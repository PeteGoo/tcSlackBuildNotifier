package slacknotifications.teamcity.settings;

import slacknotifications.teamcity.Loggers;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsFactory;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;


public class SlackNotificationProjectSettingsFactory implements ProjectSettingsFactory {
	
	public SlackNotificationProjectSettingsFactory(ProjectSettingsManager projectSettingsManager){
		Loggers.SERVER.info("SlackNotificationProjectSettingsFactory :: Registering");
		projectSettingsManager.registerSettingsFactory("slackNotifications", this);
	}

	public SlackNotificationProjectSettings createProjectSettings(String projectId) {
		Loggers.SERVER.info("SlackNotificationProjectSettingsFactory: re-reading settings for " + projectId);
		SlackNotificationProjectSettings whs = new SlackNotificationProjectSettings();
		return whs;
	}


}
