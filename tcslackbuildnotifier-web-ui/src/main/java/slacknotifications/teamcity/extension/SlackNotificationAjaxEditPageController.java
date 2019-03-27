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
import slacknotifications.SlackNotification;
import slacknotifications.teamcity.BuildState;
import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.extension.bean.ProjectSlackNotificationsBean;
import slacknotifications.teamcity.extension.bean.ProjectSlackNotificationsBeanJsonSerialiser;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.settings.SlackNotificationContentConfig;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class SlackNotificationAjaxEditPageController extends BaseController {

	    private static final String PROJECT_ID = "projectId";
	    private static final String SLACK_NOTIFICATION = "slackNotifications";
	    private static final String SUBMIT_ACTION = "submitAction";
	    private static final String ERRORS_TAG = "<errors />";
	    private static final String MESSAGES = "messages";
	    private static final String CHANNEL = "channel";
	    private static final String SLACK_NOTIFICATION_ID = "slackNotificationId";
	    private static final String MAX_COMMITS_DISPLAY = "maxCommitsToDisplay";
	    private static final String BOT_NAME = "botName";
	    private static final String ICON_URL = "iconUrl";
	    private static final String FALSE = "false";
		protected static final String BEFORE_FINISHED = "BeforeFinished";
		protected static final String BUILD_INTERRUPTED = "BuildInterrupted";
		protected static final String BUILD_STARTED = "BuildStarted";
		protected static final String BUILD_BROKEN = "BuildBroken";
		protected static final String BUILD_FIXED = "BuildFixed";
		protected static final String BUILD_FAILED = "BuildFailed";
		protected static final String BUILD_SUCCESSFUL = "BuildSuccessful";
		
		
		private final WebControllerManager myWebManager;
    private final SlackNotificationMainSettings myMainSettings;
    private SBuildServer myServer;
	    private ProjectSettingsManager mySettings;
	    private final String myPluginPath;
	    private final SlackNotificationPayloadManager myManager;

	public SlackNotificationAjaxEditPageController(SBuildServer server, WebControllerManager webManager,
                                                       ProjectSettingsManager settings, SlackNotificationProjectSettings whSettings, SlackNotificationPayloadManager manager,
                                                       PluginDescriptor pluginDescriptor, SlackNotificationMainSettings mainSettings) {
	        super(server);
	        myWebManager = webManager;
	        myServer = server;
	        mySettings = settings;
	        myPluginPath = pluginDescriptor.getPluginResourcesPath();
	        myManager = manager;
            myMainSettings = mainSettings;
	    }

	    public void register(){
	      myWebManager.registerController("/slacknotifications/ajaxEdit.html", this);
	    }
	    
	    protected static void checkAndAddBuildState(HttpServletRequest r, BuildState state, BuildStateEnum myBuildState, String varName){
	    	if ((r.getParameter(varName) != null)
	    		&& ("on".equalsIgnoreCase(r.getParameter(varName)))){
	    		state.enable(myBuildState);
	    	} else {
	    		state.disable(myBuildState);;
	    	}
	    }
	    
	    protected static void checkAndAddBuildStateIfEitherSet(HttpServletRequest r, BuildState state, BuildStateEnum myBuildState, String varName, String otherVarName){
	    	if ((r.getParameter(varName) != null)
	    			&& ("on".equalsIgnoreCase(r.getParameter(varName)))){
	    		state.enable(myBuildState);
	    	} else if ((r.getParameter(otherVarName) != null)
	    			&& ("on".equalsIgnoreCase(r.getParameter(otherVarName)))){
		    	state.enable(myBuildState);
	    	} else {
	    		state.disable(myBuildState);;
	    	}
	    }

	    @Nullable
		@Override
	    protected ModelAndView doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    	
	        HashMap<String,Object> params = new HashMap<String,Object>();
	        
	        SUser myUser = SessionUser.getUser(request);
	        SProject myProject;
	        SlackNotificationProjectSettings projSettings = null;

	    	if ("post".equalsIgnoreCase(request.getMethod())){
	    		if ((request.getParameter(PROJECT_ID) != null)
	    			&& request.getParameter(PROJECT_ID).startsWith("project")){
	    		    	projSettings = (SlackNotificationProjectSettings) mySettings.getSettings(request.getParameter(PROJECT_ID), SLACK_NOTIFICATION);
	    		    	myProject = this.myServer.getProjectManager().findProjectById(request.getParameter(PROJECT_ID));

			    		if ((projSettings != null) && (myProject != null)
			    				&& (myUser.isPermissionGrantedForProject(myProject.getProjectId(), Permission.EDIT_PROJECT))){
			    			if ((request.getParameter(SUBMIT_ACTION) != null ) 
			    				&& ("removeSlackNotification".equals(request.getParameter(SUBMIT_ACTION)))
			    				&& (request.getParameter("removedSlackNotificationId") != null)){
			    					projSettings.deleteSlackNotification(request.getParameter("removedSlackNotificationId"), myProject.getProjectId());
			    					if(projSettings.updateSuccessful()){
			    						myProject.persist();
			    						params.put(MESSAGES, ERRORS_TAG);
			    					} else {
			    						params.put(MESSAGES, "<errors><error id=\"messageArea\">The slacknotifications was not found. Have the SlackNotifications been edited on disk or by another user?</error></errors>");
			    					}
			    					
			    			} else if ((request.getParameter(SUBMIT_ACTION) != null ) 
				    				&& ("updateSlackNotification".equals(request.getParameter(SUBMIT_ACTION)))){
			    				if((request.getParameter(CHANNEL) != null )
				    				&& (request.getParameter(CHANNEL).length() > 0 )){
			    					
			    					if (request.getParameter(SLACK_NOTIFICATION_ID) != null){
			    						Boolean enabled = false;
                                        Boolean mentionChannelEnabled = false;
										Boolean mentionSlackUserEnabled = false;
			    						Boolean buildTypeAll = false;
			    						Boolean buildTypeSubProjects = false;
                                        SlackNotificationContentConfig content = new SlackNotificationContentConfig();
			    						Set<String> buildTypes = new HashSet<String>();
			    						if ((request.getParameter("slackNotificationsEnabled") != null )
			    								&& ("on".equalsIgnoreCase(request.getParameter("slackNotificationsEnabled")))){
			    							enabled = true;
			    						}
                                        if ((request.getParameter("mentionChannelEnabled") != null )
                                                && ("on".equalsIgnoreCase(request.getParameter("mentionChannelEnabled")))){

                                            mentionChannelEnabled = true;
                                        }
										if ((request.getParameter("mentionSlackUserEnabled") != null )
												&& ("on".equalsIgnoreCase(request.getParameter("mentionSlackUserEnabled")))){
											mentionSlackUserEnabled = true;
										}

                                        content.setEnabled((request.getParameter("customContentEnabled") != null )
                                                && ("on".equalsIgnoreCase(request.getParameter("customContentEnabled"))));

                                        if (content.isEnabled()){

                                            if ((request.getParameter(MAX_COMMITS_DISPLAY) != null )
                                                    && (request.getParameter(MAX_COMMITS_DISPLAY).length() > 0)){
                                                content.setMaxCommitsToDisplay(convertToInt(request.getParameter(MAX_COMMITS_DISPLAY), SlackNotificationContentConfig.DEFAULT_MAX_COMMITS));
                                            }

                                            content.setShowBuildAgent((request.getParameter("showBuildAgent") != null )
                                                    && ("on".equals(request.getParameter("showBuildAgent"))));

                                            content.setShowCommits((request.getParameter("showCommits") != null )
                                                    && ("on".equalsIgnoreCase(request.getParameter("showCommits"))));

                                            content.setShowCommitters((request.getParameter("showCommitters") != null)
                                                    && ("on".equalsIgnoreCase(request.getParameter("showCommitters"))));

                                            content.setShowElapsedBuildTime((request.getParameter("showElapsedBuildTime") != null)
                                                    && ("on".equalsIgnoreCase(request.getParameter("showElapsedBuildTime"))));

                                            content.setShowFailureReason((request.getParameter("showFailureReason") != null)
                                                    && ("on".equalsIgnoreCase(request.getParameter("showFailureReason"))));

                                            if ((request.getParameter(BOT_NAME) != null )
                                                    && (request.getParameter(BOT_NAME).length() > 0)){
                                                content.setBotName(request.getParameter(BOT_NAME));
                                            }

                                            if ((request.getParameter(ICON_URL) != null )
                                                    && (request.getParameter(ICON_URL).length() > 0)){
                                                content.setIconUrl(request.getParameter(ICON_URL));
                                            }
                                        }

			    						BuildState states = new BuildState();
			    						
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_SUCCESSFUL, BUILD_SUCCESSFUL);
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_FAILED, BUILD_FAILED);
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_FIXED, BUILD_FIXED);
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_BROKEN, BUILD_BROKEN);
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_STARTED, BUILD_STARTED);
			    						checkAndAddBuildState(request, states, BuildStateEnum.BUILD_INTERRUPTED, BUILD_INTERRUPTED);	
			    						checkAndAddBuildState(request, states, BuildStateEnum.BEFORE_BUILD_FINISHED, BEFORE_FINISHED);
			    						checkAndAddBuildStateIfEitherSet(request, states, BuildStateEnum.BUILD_FINISHED, BUILD_SUCCESSFUL, BUILD_FAILED);
			    						checkAndAddBuildState(request, states, BuildStateEnum.RESPONSIBILITY_CHANGED, "ResponsibilityChanged");
			    						
			    						if ((request.getParameter("buildTypeSubProjects") != null ) && ("on".equalsIgnoreCase(request.getParameter("buildTypeSubProjects")))){
			    							buildTypeSubProjects = true;
			    						}
			    						if ((request.getParameter("buildTypeAll") != null ) && ("on".equalsIgnoreCase(request.getParameter("buildTypeAll")))){
			    							buildTypeAll = true;
			    						} else {
			    							if (request.getParameterValues("buildTypeId") != null){
			    								String[] types = request.getParameterValues("buildTypeId");
			    								for (String string : types) {
			    									buildTypes.add(string);
												}
			    							}
			    						}
		    						
			    						if ("new".equals(request.getParameter(SLACK_NOTIFICATION_ID))){
			    							projSettings.addNewSlackNotification(myProject.getProjectId(), request.getParameter("token"), request.getParameter(CHANNEL), request.getParameter("team"), request.getParameter("filterBranchName"),
													enabled, states, buildTypeAll, buildTypeSubProjects, buildTypes, mentionChannelEnabled, mentionSlackUserEnabled);
			    							if(projSettings.updateSuccessful()){
			    								myProject.persist();
			    	    						params.put(MESSAGES, ERRORS_TAG);
			    							} else {
			    								params.put("message", "<errors><error id=\"\">" + projSettings.getUpdateMessage() + "</error>");
			    							}
			    						} else {
			    							projSettings.updateSlackNotification(myProject.getProjectId(), request.getParameter("token"),
                                                    request.getParameter(SLACK_NOTIFICATION_ID), request.getParameter(CHANNEL),
													request.getParameter("filterBranchName"), enabled, states, buildTypeAll,
													buildTypeSubProjects, buildTypes, mentionChannelEnabled, mentionSlackUserEnabled, content);
			    							if(projSettings.updateSuccessful()){
			    								myProject.persist();
			    	    						params.put(MESSAGES, ERRORS_TAG);
			    							} else {
			    								params.put("message", "<errors><error id=\"\">" + projSettings.getUpdateMessage() + "</error>");
			    							}
			    						}
			    					} // TODO Need to handle slackNotificationId being null
			    						
			    				} else {
			    					if ((request.getParameter(CHANNEL) == null )
				    				|| (request.getParameter(CHANNEL).length() == 0)){
			    						params.put(MESSAGES, "<errors><error id=\"emptySlackNotificationChannel\">Please enter a channel.</error></errors>");
			    					}
			    				}
				    			
			    			}
			    		} else {
			    			params.put(MESSAGES, "<errors><error id=\"messageArea\">You do not appear to have permission to edit SlackNotifications.</error></errors>");
			    		}
	    		}
	    	}

	    	if ("get".equalsIgnoreCase(request.getMethod())
	        		&& request.getParameter(PROJECT_ID) != null 
	        		&& request.getParameter(PROJECT_ID).startsWith("project")){
	        	
		    	SlackNotificationProjectSettings projSettings1 = (SlackNotificationProjectSettings) mySettings.getSettings(request.getParameter(PROJECT_ID), SLACK_NOTIFICATION);
		    	SProject project = this.myServer.getProjectManager().findProjectById(request.getParameter(PROJECT_ID));
		    	
		    	String message = projSettings1.getSlackNotificationsAsString();
		    	
		    	params.put("haveProject", "true");
		    	params.put(MESSAGES, message);
		    	params.put(PROJECT_ID, project.getProjectId());
		    	params.put("projectExternalId", TeamCityIdResolver.getExternalProjectId(project));
		    	params.put("projectName", project.getName());
		    	
		    	params.put("slackNotificationCount", projSettings1.getSlackNotificationsCount());
		    	if (projSettings1.getSlackNotificationsCount() == 0){
		    		params.put("noSlackNotifications", "true");
		    		params.put(SLACK_NOTIFICATION, FALSE);
		    	} else {
		    		params.put("noSlackNotifications", FALSE);
		    		params.put(SLACK_NOTIFICATION, "true");
		    		params.put("slackNotificationList", projSettings.getSlackNotificationsAsList());
		    		params.put("slackNotificationsDisabled", !projSettings.isEnabled());
		    		params.put("slackNotificationsEnabledAsChecked", projSettings.isEnabledAsChecked());
		    		params.put("projectSlackNotificationsAsJson", ProjectSlackNotificationsBeanJsonSerialiser.serialise(ProjectSlackNotificationsBean.build(projSettings, project, myMainSettings)));
		    	}
	        } else {
	        	params.put("haveProject", FALSE);
	        }
	        
	        return new ModelAndView(myPluginPath + "SlackNotification/ajaxEdit.jsp", params);
	    }


    private int convertToInt(String s, int defaultValue){
        try{
            int myInt = Integer.parseInt(s);
            return myInt;
        } catch (NumberFormatException e){
            return defaultValue;
        }
    }
}
