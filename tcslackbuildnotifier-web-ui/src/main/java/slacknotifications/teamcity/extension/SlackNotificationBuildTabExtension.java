package slacknotifications.teamcity.extension;

import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.openapi.buildType.BuildTypeTab;
import org.jetbrains.annotations.NotNull;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.extension.bean.ProjectAndBuildSlacknotificationsBean;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class SlackNotificationBuildTabExtension extends BuildTypeTab {
	
	private static final String  SLACK_NOTIFICATIONS = "slackNotifications";
	SlackNotificationProjectSettings settings;
	ProjectSettingsManager projSettings;
	String myPluginPath;

	protected SlackNotificationBuildTabExtension(
            PagePlaces pagePlaces, ProjectManager projectManager,
            ProjectSettingsManager settings, WebControllerManager manager,
            PluginDescriptor pluginDescriptor) {
		//super(myTitle, myTitle, null, projectManager);
		super(SLACK_NOTIFICATIONS, "Slack", manager, projectManager);
		this.projSettings = settings;
		myPluginPath = pluginDescriptor.getPluginResourcesPath();
	}

	public boolean isAvailable(@NotNull HttpServletRequest request) {
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void fillModel(Map model, HttpServletRequest request,
			 @NotNull SBuildType buildType, SUser user) {
		this.settings = 
			(SlackNotificationProjectSettings)this.projSettings.getSettings(buildType.getProject().getProjectId(), SLACK_NOTIFICATIONS);
		
		List<ProjectAndBuildSlacknotificationsBean> projectAndParents = new ArrayList<ProjectAndBuildSlacknotificationsBean>();
		List<SProject> parentProjects = buildType.getProject().getProjectPath();
		parentProjects.remove(0);
		for (SProject projectParent : parentProjects){
			projectAndParents.add(
					ProjectAndBuildSlacknotificationsBean.newInstance(
							projectParent,
							(SlackNotificationProjectSettings) this.projSettings.getSettings(projectParent.getProjectId(), SLACK_NOTIFICATIONS),
							buildType
							)
					);
		}
		
//		projectAndParents.add(
//				ProjectAndBuildSlacknotificationsBean.newInstance(
//						project,
//						(SlackNotificationProjectSettings) this.projSettings.getSettings(project.getProjectId(), SLACK_NOTIFICATIONS),
//						true
//						)
//				);

		model.put("projectAndParents", projectAndParents);
    	
//    	List<SlackNotificationConfig> projectSlacknotifications = this.settings.getProjectSlackNotificationsAsList();
//    	List<SlackNotificationConfig> buildSlacknotifications = this.settings.getBuildSlackNotificationsAsList(buildType);
//    	
//    	model.put("projectSlackNotificationCount", projectSlacknotifications.size());
//    	if (projectSlacknotifications.size() == 0){
//    		model.put("noProjectSlackNotifications", "true");
//    		model.put("projectSlackNotifications", "false");
//    	} else {
//    		model.put("noProjectSlackNotifications", "false");
//    		model.put("projectSlackNotifications", "true");
//    		model.put("projectSlackNotificationList", projectSlacknotifications);
//    		model.put("projectSlackNotificationsDisabled", !this.settings.isEnabled());
//    	}
//    	
//    	model.put("buildSlackNotificationCount", buildSlacknotifications.size());
//    	if (buildSlacknotifications.size() == 0){
//    		model.put("noBuildSlackNotifications", "true");
//    		model.put("buildSlackNotifications", "false");
//    	} else {
//    		model.put("noBuildSlackNotifications", "false");
//    		model.put("buildSlackNotifications", "true");
//    		model.put("buildSlackNotificationList", buildSlacknotifications);
//    	}
//    	

    	model.put("projectId", buildType.getProject().getProjectId());
    	model.put("projectExternalId", TeamCityIdResolver.getExternalProjectId(buildType.getProject()));
    	model.put("projectName", buildType.getProject().getName());
    	
    	model.put("buildTypeId", buildType.getBuildTypeId());
    	model.put("buildExternalId", TeamCityIdResolver.getExternalBuildId(buildType));
    	model.put("buildName", buildType.getName());
	}

	@Override
	public String getIncludeUrl() {
		//return myPluginPath + "SlackNotification/buildSlackNotificationTab.jsp";
		return myPluginPath + "SlackNotification/projectSlackNotificationTab.jsp";
	}


	
}
