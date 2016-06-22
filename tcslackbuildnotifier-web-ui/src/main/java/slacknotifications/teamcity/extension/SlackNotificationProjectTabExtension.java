package slacknotifications.teamcity.extension;

import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.project.ProjectTab;
import org.jetbrains.annotations.NotNull;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.extension.bean.ProjectAndBuildSlacknotificationsBean;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class SlackNotificationProjectTabExtension extends ProjectTab {
	
	ProjectSettingsManager projSettings;
	String myPluginPath;

	protected SlackNotificationProjectTabExtension(
            PagePlaces pagePlaces, ProjectManager projectManager,
            ProjectSettingsManager settings, PluginDescriptor pluginDescriptor) {
		super("slackNotifications", "Slack", pagePlaces, projectManager);
		this.projSettings = settings;
		myPluginPath = pluginDescriptor.getPluginResourcesPath();
	}

	@Override
	public boolean isAvailable(@NotNull HttpServletRequest request) {
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void fillModel(Map model, HttpServletRequest request,
			 @NotNull SProject project, SUser user) {
		
		List<ProjectAndBuildSlacknotificationsBean> projectAndParents = new ArrayList<ProjectAndBuildSlacknotificationsBean>();
		List<SProject> parentProjects = project.getProjectPath();
		parentProjects.remove(0);
		for (SProject projectParent : parentProjects){
			projectAndParents.add(
					ProjectAndBuildSlacknotificationsBean.newInstance(
							projectParent,
							(SlackNotificationProjectSettings) this.projSettings.getSettings(projectParent.getProjectId(), "slackNotifications"),
							null
							)
					);
		}
		
//		projectAndParents.add(
//				ProjectAndBuildSlacknotificationsBean.newInstance(
//						project,
//						(SlackNotificationProjectSettings) this.projSettings.getSettings(project.getProjectId(), "slackNotifications"),
//						true
//						)
//				);

		model.put("projectAndParents", projectAndParents);
		
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
//		model.put("buildSlackNotificationList", buildSlacknotifications);
    	
    	model.put("projectId", project.getProjectId());
    	model.put("projectExternalId", TeamCityIdResolver.getExternalProjectId(project));
    	model.put("projectName", project.getName());
	}

	@Override
	public String getIncludeUrl() {
		return myPluginPath+ "SlackNotification/projectSlackNotificationTab.jsp";
	}

}
