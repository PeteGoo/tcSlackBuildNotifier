package slacknotifications.teamcity.extension;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.extension.bean.ProjectSlackNotificationsBean;
import slacknotifications.teamcity.extension.bean.ProjectSlackNotificationsBeanJsonSerialiser;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;


public class SlackNotificationAjaxSettingsListPageController extends BaseController {

	private final WebControllerManager myWebManager;
    private final SlackNotificationMainSettings myMainSettings;

    private SBuildServer myServer;
	private ProjectSettingsManager mySettings;
	private PluginDescriptor myPluginDescriptor;
	private final SlackNotificationPayloadManager myManager;

	    public SlackNotificationAjaxSettingsListPageController(SBuildServer server, WebControllerManager webManager,
                                                               ProjectSettingsManager settings, SlackNotificationPayloadManager manager, PluginDescriptor pluginDescriptor,
                                                               SlackNotificationMainSettings mainSettings) {
	        super(server);
	        myWebManager = webManager;
	        myServer = server;
	        mySettings = settings;
	        myPluginDescriptor = pluginDescriptor;
	        myManager = manager;
            myMainSettings = mainSettings;
	    }

	    public void register(){
	      myWebManager.registerController("/slacknotifications/settingsList.html", this);
	    }

	    @Nullable
	    protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    	
	        HashMap<String,Object> params = new HashMap<String,Object>();
	        params.put("jspHome",this.myPluginDescriptor.getPluginResourcesPath());
	        
	        if(request.getParameter("projectId") != null 
	        		&& request.getParameter("projectId").startsWith("project")){
	        	
	        	SProject project = this.myServer.getProjectManager().findProjectById(request.getParameter("projectId"));
		    	SlackNotificationProjectSettings projSettings = (SlackNotificationProjectSettings)
		    			mySettings.getSettings(request.getParameter("projectId"), "slackNotifications");
		    	
		    		params.put("projectSlackNotificationsAsJson", ProjectSlackNotificationsBeanJsonSerialiser.serialise(ProjectSlackNotificationsBean.build(projSettings, project, myMainSettings)));
	        } else if (request.getParameter("buildTypeId") != null){
        		SBuildType sBuildType = TeamCityIdResolver.findBuildTypeById(this.myServer.getProjectManager(), request.getParameter("buildTypeId"));
        		if (sBuildType != null){
		        	SProject project = sBuildType.getProject();
		        	if (project != null){
				    	SlackNotificationProjectSettings projSettings = (SlackNotificationProjectSettings)
				    			mySettings.getSettings(project.getProjectId(), "slackNotifications");
		        		params.put("projectSlackNotificationsAsJson", ProjectSlackNotificationsBeanJsonSerialiser.serialise(ProjectSlackNotificationsBean.build(projSettings, sBuildType, project, myMainSettings)));
		        	}
        		}
	        
	        } else {
	        	params.put("haveProject", "false");
	        }

	        return new ModelAndView(myPluginDescriptor.getPluginResourcesPath() + "SlackNotification/settingsList.jsp", params);
	        //return new ModelAndView("/SlackNotification/settingsList.jsp", params);
	    }
}
