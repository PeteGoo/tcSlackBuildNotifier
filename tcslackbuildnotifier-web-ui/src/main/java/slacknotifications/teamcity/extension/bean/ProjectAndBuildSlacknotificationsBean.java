package slacknotifications.teamcity.extension.bean;

import java.util.ArrayList;
import java.util.List;

import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;

public class ProjectAndBuildSlacknotificationsBean {
	SProject project;
	SlackNotificationProjectSettings slackNotificationProjectSettings;
	List<SlackNotificationConfig> projectSlacknotifications;
	List<BuildSlacknotificationsBean> buildSlacknotifications;
	
	public static ProjectAndBuildSlacknotificationsBean newInstance (SProject project, SlackNotificationProjectSettings settings, SBuildType sBuild) {
		ProjectAndBuildSlacknotificationsBean bean = new ProjectAndBuildSlacknotificationsBean();
		bean.project = project;
		bean.slackNotificationProjectSettings = settings;
		
		bean.projectSlacknotifications = settings.getProjectSlackNotificationsAsList();
		bean.buildSlacknotifications = new ArrayList<BuildSlacknotificationsBean>();
		
		if (sBuild != null && sBuild.getProjectId().equals(project.getProjectId())){
			bean.buildSlacknotifications.add(new BuildSlacknotificationsBean(sBuild, settings.getBuildSlackNotificationsAsList(sBuild)));
		}
		return bean;
	}

	public int getProjectSlacknotificationCount(){
		return this.projectSlacknotifications.size();
	}

	public int getBuildSlacknotificationCount(){
		return this.buildSlacknotifications.size();
	}
	
	public SProject getProject() {
		return project;
	}

	public SlackNotificationProjectSettings getSlackNotificationProjectSettings() {
		return slackNotificationProjectSettings;
	}

	public List<SlackNotificationConfig> getProjectSlacknotifications() {
		return projectSlacknotifications;
	}

	public List<BuildSlacknotificationsBean> getBuildSlacknotifications() {
		return buildSlacknotifications;
	}
	
	public String getExternalProjectId(){
		return TeamCityIdResolver.getExternalProjectId(project);
	}

}
