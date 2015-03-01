package slacknotifications.teamcity.extension;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.extension.bean.ProjectSlackNotificationsBean;
import slacknotifications.teamcity.extension.bean.ProjectSlackNotificationsBeanJsonSerialiser;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;


public class SlackNotificationIndexPageController extends BaseController {

	    private final WebControllerManager myWebManager;
	    private final SlackNotificationMainSettings myMainSettings;
	    private SBuildServer myServer;
	    private ProjectSettingsManager mySettings;
	    private PluginDescriptor myPluginDescriptor;
	    private final SlackNotificationPayloadManager myManager;

	    public SlackNotificationIndexPageController(SBuildServer server, WebControllerManager webManager,
                                                    ProjectSettingsManager settings, PluginDescriptor pluginDescriptor, SlackNotificationPayloadManager manager,
                                                    SlackNotificationMainSettings configSettings) {
	        super(server);
	        myWebManager = webManager;
	        myServer = server;
	        mySettings = settings;
	        myPluginDescriptor = pluginDescriptor;
	        myMainSettings = configSettings;
	        myManager = manager;
	    }

	    public void register(){
	      myWebManager.registerController("/slacknotifications/index.html", this);
	    }

	    @Nullable
	    protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    	
	        HashMap<String,Object> params = new HashMap<String,Object>();
	        params.put("jspHome",this.myPluginDescriptor.getPluginResourcesPath());
        	params.put("includeJquery", Boolean.toString(this.myServer.getServerMajorVersion() < 7));
        	params.put("rootContext", myServer.getServerRootPath());
	        
	    	if (myMainSettings.getInfoUrl() != null && myMainSettings.getInfoUrl().length() > 0){
	    		params.put("moreInfoText", "<li><a href=\"" + myMainSettings.getInfoUrl() + "\">" + myMainSettings.getInfoText() + "</a></li>");
	    		if (myMainSettings.getSlackNotificationShowFurtherReading()){
	    			params.put("ShowFurtherReading", "ALL");
	    		} else {
	    			params.put("ShowFurtherReading", "SINGLE");
	    		}
	    	} else if (myMainSettings.getSlackNotificationShowFurtherReading()){
	    		params.put("ShowFurtherReading", "DEFAULT");
	    	} else {
	    		params.put("ShowFurtherReading", "NONE");
	    	}
	        
	        if(request.getParameter("projectId") != null){
	        	
	        	SProject project = TeamCityIdResolver.findProjectById(this.myServer.getProjectManager(), request.getParameter("projectId"));
	        	if (project != null){
	        		
			    	SlackNotificationProjectSettings projSettings = (SlackNotificationProjectSettings)
			    			mySettings.getSettings(project.getProjectId(), "slackNotifications");
			    	
			        SUser myUser = SessionUser.getUser(request);
			        params.put("hasPermission", myUser.isPermissionGrantedForProject(project.getProjectId(), Permission.EDIT_PROJECT));
			    	
			    	String message = projSettings.getSlackNotificationsAsString();
			    	
			    	params.put("haveProject", "true");
			    	params.put("messages", message);
			    	params.put("projectId", project.getProjectId());
			    	params.put("buildTypeList", project.getBuildTypes());
			    	params.put("projectExternalId", TeamCityIdResolver.getExternalProjectId(project));
			    	params.put("projectName", project.getName());
			    	
			    	logger.debug(myMainSettings.getInfoText() + myMainSettings.getInfoUrl() + myMainSettings.getProxySettingsAsString());
			    	
			    	params.put("slackNotificationCount", projSettings.getSlackNotificationsCount());
			    	
			    	if (projSettings.getSlackNotificationsCount() == 0){
			    		params.put("noSlackNotifications", "true");
			    		params.put("slackNotifications", "false");
			    		params.put("projectSlackNotificationsAsJson", ProjectSlackNotificationsBeanJsonSerialiser.serialise(ProjectSlackNotificationsBean.build(projSettings, project, myMainSettings)));
			    	} else {
			    		params.put("noSlackNotifications", "false");
			    		params.put("slackNotifications", "true");
			    		params.put("slackNotificationList", projSettings.getSlackNotificationsAsList());
			    		params.put("slackNotificationsDisabled", !projSettings.isEnabled());
			    		params.put("slackNotificationsEnabledAsChecked", projSettings.isEnabledAsChecked());
			    		params.put("projectSlackNotificationsAsJson", ProjectSlackNotificationsBeanJsonSerialiser.serialise(ProjectSlackNotificationsBean.build(projSettings, project, myMainSettings)));

			    	}
		    	} else {
		    		params.put("haveProject", "false");
		    		params.put("errorReason", "The project requested does not appear to be valid.");
		    	}
        	} else if (request.getParameter("buildTypeId") != null){
        		SBuildType sBuildType = TeamCityIdResolver.findBuildTypeById(this.myServer.getProjectManager(), request.getParameter("buildTypeId"));
        		if (sBuildType != null){
		        	SProject project = sBuildType.getProject();
		        	if (project != null){
		        		
				    	SlackNotificationProjectSettings projSettings = (SlackNotificationProjectSettings)
				    			mySettings.getSettings(project.getProjectId(), "slackNotifications");
				    	
				    	SUser myUser = SessionUser.getUser(request);
				        params.put("hasPermission", myUser.isPermissionGrantedForProject(project.getProjectId(), Permission.EDIT_PROJECT));
				    	
				    	List<SlackNotificationConfig> configs = projSettings.getBuildSlackNotificationsAsList(sBuildType);
				    	params.put("slackNotificationList", configs);
				    	params.put("slackNotificationsDisabled", !projSettings.isEnabled());
				    	params.put("projectId", project.getProjectId());
				    	params.put("haveProject", "true");
				    	params.put("projectName", project.getName());
				    	params.put("projectExternalId", TeamCityIdResolver.getExternalProjectId(project));
				    	params.put("haveBuild", "true");
				    	params.put("buildName", sBuildType.getName());
				    	params.put("buildExternalId", TeamCityIdResolver.getExternalBuildId(sBuildType));
				    	params.put("buildTypeList", project.getBuildTypes());
			    		params.put("noSlackNotifications", configs.size() == 0);
			    		params.put("slackNotifications", configs.size() != 0);
				    	
			    		params.put("projectSlackNotificationsAsJson", ProjectSlackNotificationsBeanJsonSerialiser.serialise(ProjectSlackNotificationsBean.build(projSettings, sBuildType, project, myMainSettings)));
		        	}
        		} else {
		    		params.put("haveProject", "false");
		    		params.put("errorReason", "The build requested does not appear to be valid.");
        		}
	        } else {
	        	params.put("haveProject", "false");
	        	params.put("errorReason", "No project specified.");
	        }

	        return new ModelAndView(myPluginDescriptor.getPluginResourcesPath() + "SlackNotification/index.jsp", params);
	    }
}
