package slacknotifications.teamcity.extension;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import slacknotifications.teamcity.BuildState;
import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.extension.bean.ProjectSlackNotificationsBean;
import slacknotifications.teamcity.extension.bean.ProjectSlackNotificationsBeanJsonSerialiser;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class SlackNotificationAjaxEditPageController extends BaseController {

	    protected static final String BEFORE_FINISHED = "BeforeFinished";
		protected static final String BUILD_INTERRUPTED = "BuildInterrupted";
		protected static final String BUILD_STARTED = "BuildStarted";
		protected static final String BUILD_BROKEN = "BuildBroken";
		protected static final String BUILD_FIXED = "BuildFixed";
		protected static final String BUILD_FAILED = "BuildFailed";
		protected static final String BUILD_SUCCESSFUL = "BuildSuccessful";
		
		private final WebControllerManager myWebManager;
	    private SBuildServer myServer;
	    private ProjectSettingsManager mySettings;
	    private final String myPluginPath;
	    private final SlackNotificationPayloadManager myManager;
	    
	    public SlackNotificationAjaxEditPageController(SBuildServer server, WebControllerManager webManager,
                                                       ProjectSettingsManager settings, SlackNotificationProjectSettings whSettings, SlackNotificationPayloadManager manager,
                                                       PluginDescriptor pluginDescriptor) {
	        super(server);
	        myWebManager = webManager;
	        myServer = server;
	        mySettings = settings;
	        myPluginPath = pluginDescriptor.getPluginResourcesPath();
	        myManager = manager;
	    }

	    public void register(){
	      myWebManager.registerController("/slacknotifications/ajaxEdit.html", this);
	    }
	    
	    protected static void checkAndAddBuildState(HttpServletRequest r, BuildState state, BuildStateEnum myBuildState, String varName){
	    	if ((r.getParameter(varName) != null)
	    		&& (r.getParameter(varName).equalsIgnoreCase("on"))){
	    		state.enable(myBuildState);
	    	} else {
	    		state.disable(myBuildState);;
	    	}
	    }
	    
	    protected static void checkAndAddBuildStateIfEitherSet(HttpServletRequest r, BuildState state, BuildStateEnum myBuildState, String varName, String otherVarName){
	    	if ((r.getParameter(varName) != null)
	    			&& (r.getParameter(varName).equalsIgnoreCase("on"))){
	    		state.enable(myBuildState);
	    	} else if ((r.getParameter(otherVarName) != null)
	    			&& (r.getParameter(otherVarName).equalsIgnoreCase("on"))){
		    	state.enable(myBuildState);
	    	} else {
	    		state.disable(myBuildState);;
	    	}
	    }

	    @Nullable
	    protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    	
	        HashMap<String,Object> params = new HashMap<String,Object>();
	        
	        SUser myUser = SessionUser.getUser(request);
	        SProject myProject = null;
	        SlackNotificationProjectSettings projSettings = null;
	    	
	    	if (request.getMethod().equalsIgnoreCase("post")){
	    		if ((request.getParameter("projectId") != null)
	    			&& request.getParameter("projectId").startsWith("project")){
	    		    	projSettings = (SlackNotificationProjectSettings) mySettings.getSettings(request.getParameter("projectId"), "slackNotifications");
	    		    	myProject = this.myServer.getProjectManager().findProjectById(request.getParameter("projectId"));

			    		if ((projSettings != null) && (myProject != null)
			    				&& (myUser.isPermissionGrantedForProject(myProject.getProjectId(), Permission.EDIT_PROJECT))){
			    			if ((request.getParameter("submitAction") != null ) 
			    				&& (request.getParameter("submitAction").equals("removeSlackNotification"))
			    				&& (request.getParameter("removedSlackNotificationId") != null)){
			    					projSettings.deleteSlackNotification(request.getParameter("removedSlackNotificationId"), myProject.getProjectId());
			    					if(projSettings.updateSuccessful()){
			    						myProject.persist();
			    						params.put("messages", "<errors />");
			    					} else {
			    						params.put("messages", "<errors><error id=\"messageArea\">The slacknotifications was not found. Have the SlackNotifications been edited on disk or by another user?</error></errors>");
			    					}
			    					
			    			} else if ((request.getParameter("submitAction") != null ) 
				    				&& (request.getParameter("submitAction").equals("updateSlackNotification"))){
			    				if((request.getParameter("channel") != null )
				    				&& (request.getParameter("channel").length() > 0 )){
			    					
			    					if (request.getParameter("slackNotificationId") != null){
			    						Boolean enabled = false;
                                        Boolean mentionChannelEnabled = false;
										Boolean mentionSlackUserEnabled = false;
			    						Boolean buildTypeAll = false;
			    						Boolean buildTypeSubProjects = false;
			    						Set<String> buildTypes = new HashSet<String>();
			    						if ((request.getParameter("slackNotificationsEnabled") != null )
			    								&& (request.getParameter("slackNotificationsEnabled").equalsIgnoreCase("on"))){
			    							enabled = true;
			    						}
                                        if ((request.getParameter("mentionChannelEnabled") != null )
                                                && (request.getParameter("mentionChannelEnabled").equalsIgnoreCase("on"))){
                                            mentionChannelEnabled = true;
                                        }
										if ((request.getParameter("mentionSlackUserEnabled") != null )
												&& (request.getParameter("mentionSlackUserEnabled").equalsIgnoreCase("on"))){
											mentionSlackUserEnabled = true;
										}
			    						BuildState states = new BuildState();
			    						
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_SUCCESSFUL, BUILD_SUCCESSFUL);
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_FAILED, BUILD_FAILED);
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_FIXED, BUILD_FIXED);
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_BROKEN, BUILD_BROKEN);
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_STARTED, BUILD_STARTED);
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_INTERRUPTED, BUILD_INTERRUPTED);	
			    						checkAndAddBuildState(request, states, BuildStateEnum.BEFORE_BUILD_FINISHED, BEFORE_FINISHED);
			    						checkAndAddBuildStateIfEitherSet(request, states, BuildStateEnum.BUILD_FINISHED, BUILD_SUCCESSFUL,BUILD_FAILED);
			    						checkAndAddBuildState(request, states, BuildStateEnum.RESPONSIBILITY_CHANGED, "ResponsibilityChanged");
			    						
			    						if ((request.getParameter("buildTypeSubProjects") != null ) && (request.getParameter("buildTypeSubProjects").equalsIgnoreCase("on"))){
			    							buildTypeSubProjects = true;
			    						}
			    						if ((request.getParameter("buildTypeAll") != null ) && (request.getParameter("buildTypeAll").equalsIgnoreCase("on"))){
			    							buildTypeAll = true;
			    						} else {
			    							if (request.getParameterValues("buildTypeId") != null){
			    								String[] types = request.getParameterValues("buildTypeId");
			    								for (String string : types) {
			    									buildTypes.add(string);
												}
			    							}
			    						}
		    						
			    						if (request.getParameter("slackNotificationId").equals("new")){
			    							projSettings.addNewSlackNotification(myProject.getProjectId(),request.getParameter("channel"), request.getParameter("team"), enabled,
			    														states, buildTypeAll, buildTypeSubProjects, buildTypes, mentionChannelEnabled, mentionSlackUserEnabled);
			    							if(projSettings.updateSuccessful()){
			    								myProject.persist();
			    	    						params.put("messages", "<errors />");
			    							} else {
			    								params.put("message", "<errors><error id=\"\">" + projSettings.getUpdateMessage() + "</error>");
			    							}
			    						} else {
			    							projSettings.updateSlackNotification(myProject.getProjectId(),request.getParameter("slackNotificationId"),
			    														request.getParameter("channel"), enabled,
			    														states, buildTypeAll, buildTypeSubProjects, buildTypes, mentionChannelEnabled, mentionSlackUserEnabled);
			    							if(projSettings.updateSuccessful()){
			    								myProject.persist();
			    	    						params.put("messages", "<errors />");
			    							} else {
			    								params.put("message", "<errors><error id=\"\">" + projSettings.getUpdateMessage() + "</error>");
			    							}
			    						}
			    					} // TODO Need to handle slackNotificationId being null
			    						
			    				} else {
			    					if ((request.getParameter("channel") == null )
				    				|| (request.getParameter("channel").length() == 0)){
			    						params.put("messages", "<errors><error id=\"emptySlackNotificationChannel\">Please enter a channel.</error></errors>");
			    					}
			    				}
				    			
			    			}
			    		} else {
			    			params.put("messages", "<errors><error id=\"messageArea\">You do not appear to have permission to edit SlackNotifications.</error></errors>");
			    		}
	    		}
	    	}

	    	if (request.getMethod().equalsIgnoreCase("get")
	        		&& request.getParameter("projectId") != null 
	        		&& request.getParameter("projectId").startsWith("project")){
	        	
		    	SlackNotificationProjectSettings projSettings1 = (SlackNotificationProjectSettings) mySettings.getSettings(request.getParameter("projectId"), "slackNotifications");
		    	SProject project = this.myServer.getProjectManager().findProjectById(request.getParameter("projectId"));
		    	
		    	String message = projSettings1.getSlackNotificationsAsString();
		    	
		    	params.put("haveProject", "true");
		    	params.put("messages", message);
		    	params.put("projectId", project.getProjectId());
		    	params.put("projectExternalId", TeamCityIdResolver.getExternalProjectId(project));
		    	params.put("projectName", project.getName());
		    	
		    	params.put("slackNotificationCount", projSettings1.getSlackNotificationsCount());
		    	if (projSettings1.getSlackNotificationsCount() == 0){
		    		params.put("noSlackNotifications", "true");
		    		params.put("slackNotifications", "false");
		    	} else {
		    		params.put("noSlackNotifications", "false");
		    		params.put("slackNotifications", "true");
		    		params.put("slackNotificationList", projSettings.getSlackNotificationsAsList());
		    		params.put("slackNotificationsDisabled", !projSettings.isEnabled());
		    		params.put("slackNotificationsEnabledAsChecked", projSettings.isEnabledAsChecked());
		    		params.put("projectSlackNotificationsAsJson", ProjectSlackNotificationsBeanJsonSerialiser.serialise(ProjectSlackNotificationsBean.build(projSettings, project)));
		    	}
	        } else {
	        	params.put("haveProject", "false");
	        }
	        
	        return new ModelAndView(myPluginPath + "SlackNotification/ajaxEdit.jsp", params);
	    }
}
