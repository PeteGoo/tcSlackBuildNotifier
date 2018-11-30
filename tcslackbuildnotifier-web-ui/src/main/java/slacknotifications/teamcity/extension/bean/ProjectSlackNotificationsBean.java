package slacknotifications.teamcity.extension.bean;

import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import slacknotifications.teamcity.BuildState;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;

import java.util.*;

public class ProjectSlackNotificationsBean {
	String projectId;
	Map<String, SlacknotificationConfigAndBuildTypeListHolder> slackNotificationList;
	
	
	public static ProjectSlackNotificationsBean build(SlackNotificationProjectSettings projSettings, SProject project, SlackNotificationMainSettings mainSettings){
		ProjectSlackNotificationsBean bean = new ProjectSlackNotificationsBean();
		List<SBuildType> projectBuildTypes = TeamCityIdResolver.getOwnBuildTypes(project);
		
		bean.projectId = TeamCityIdResolver.getInternalProjectId(project);
		bean.slackNotificationList = new LinkedHashMap<String, SlacknotificationConfigAndBuildTypeListHolder>();

		/* Create a "new" config with blank stuff so that clicking the "new" button has a bunch of defaults to load in */
		SlackNotificationConfig newBlankConfig = new SlackNotificationConfig("", "", "", true, new BuildState().setAllEnabled(), true, true, null, true, true, true, true);
		newBlankConfig.setUniqueKey("new");
		/* And add it to the list */
		addSlackNotificationConfigHolder(bean, projectBuildTypes, newBlankConfig, mainSettings);
		
		/* Iterate over the rest of the slacknotifications in this project and add them to the json config */
		for (SlackNotificationConfig config : projSettings.getSlackNotificationsAsList()){
			addSlackNotificationConfigHolder(bean, projectBuildTypes, config, mainSettings);
		}
		
		return bean;
		
	}
	
	public static ProjectSlackNotificationsBean build(SlackNotificationProjectSettings projSettings, SBuildType sBuildType, SProject project, SlackNotificationMainSettings mainSettings){
		ProjectSlackNotificationsBean bean = new ProjectSlackNotificationsBean();
		List<SBuildType> projectBuildTypes = TeamCityIdResolver.getOwnBuildTypes(project);
		Set<String> enabledBuildTypes = new HashSet<String>();
		enabledBuildTypes.add(sBuildType.getBuildTypeId());
		
		bean.projectId = TeamCityIdResolver.getInternalProjectId(project);
		bean.slackNotificationList = new LinkedHashMap<String, SlacknotificationConfigAndBuildTypeListHolder>();
		
		/* Create a "new" config with blank stuff so that clicking the "new" button has a bunch of defaults to load in */
		SlackNotificationConfig newBlankConfig = new SlackNotificationConfig("", "", "", true, new BuildState().setAllEnabled(), false, false, enabledBuildTypes, true, true, true, true);
		newBlankConfig.setUniqueKey("new");
		/* And add it to the list */
		addSlackNotificationConfigHolder(bean, projectBuildTypes, newBlankConfig, mainSettings);
		
		/* Iterate over the rest of the slacknotifications in this project and add them to the json config */
		for (SlackNotificationConfig config : projSettings.getBuildSlackNotificationsAsList(sBuildType)){
			addSlackNotificationConfigHolder(bean, projectBuildTypes, config, mainSettings);
		}
		
		return bean;
		
	}


	private static void addSlackNotificationConfigHolder(ProjectSlackNotificationsBean bean,
			List<SBuildType> projectBuildTypes, SlackNotificationConfig config, SlackNotificationMainSettings mainSettings) {
		SlacknotificationConfigAndBuildTypeListHolder holder = new SlacknotificationConfigAndBuildTypeListHolder(config, mainSettings);
		for (SBuildType sBuildType : projectBuildTypes){
			holder.addSlackNotificationBuildType(new SlacknotificationBuildTypeEnabledStatusBean(
													sBuildType.getBuildTypeId(), 
													sBuildType.getName(), 
													config.isEnabledForBuildType(sBuildType)
													)
										);
		}
		bean.slackNotificationList.put(holder.getUniqueKey(), holder);
	}
}
