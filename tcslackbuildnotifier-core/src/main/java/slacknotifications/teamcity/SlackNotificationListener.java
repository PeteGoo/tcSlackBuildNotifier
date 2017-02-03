package slacknotifications.teamcity;

import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slacknotifications.SlackNotification;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationContentConfig;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * SlackNotificationListner
 * Listens for Server events and then triggers the execution of slacknotifications if configured.
 */
public class SlackNotificationListener extends BuildServerAdapter {
    
    private static final String SLACKNOTIFICATIONS_SETTINGS_ATTRIBUTE_NAME = "slackNotifications";
    private static final String BUILD_STATE_MESSAGE_END = " at buildState responsibilityChanged";
    private static final String BUILD_STATE_MESSAGE_START = "About to process SlackNotifications for ";
	private final SBuildServer myBuildServer;
    private final ProjectSettingsManager mySettings;
    private final SlackNotificationMainSettings myMainSettings;
    private final SlackNotificationPayloadManager myManager;
    private final SlackNotificationFactory slackNotificationFactory;
	private NotificationUtility notificationUtility;

    public SlackNotificationListener(){
        myBuildServer = null;
        mySettings = null;
        myMainSettings = null;
        myManager = null;
        slackNotificationFactory = null;
		notificationUtility = new NotificationUtility();
    }

    public SlackNotificationListener(SBuildServer sBuildServer, ProjectSettingsManager settings,
                                     SlackNotificationMainSettings configSettings, SlackNotificationPayloadManager manager,
                                     SlackNotificationFactory factory) {

        myBuildServer = sBuildServer;
        mySettings = settings;
        myMainSettings = configSettings;
        myManager = manager;
        slackNotificationFactory = factory;
		notificationUtility = new NotificationUtility();
        Loggers.SERVER.info("SlackNotificationListener :: Starting");
    }
    
    public void register(){
        myBuildServer.addListener(this);
        Loggers.SERVER.info("SlackNotificationListener :: Registering");
    }

	public void getFromConfig(SlackNotification slackNotification, SlackNotificationConfig slackNotificationConfig){
        slackNotification.setChannel(StringUtil.isEmpty(slackNotificationConfig.getChannel()) ? myMainSettings.getDefaultChannel() : slackNotificationConfig.getChannel());
        slackNotification.setTeamName(myMainSettings.getTeamName());
        slackNotification.setToken(StringUtil.isEmpty(slackNotificationConfig.getToken()) ? myMainSettings.getToken() : slackNotificationConfig.getToken());
        slackNotification.setIconUrl(myMainSettings.getIconUrl());
        slackNotification.setBotName(myMainSettings.getBotName());
		slackNotification.setEnabled(myMainSettings.getEnabled() && slackNotificationConfig.getEnabled());
		slackNotification.setBuildStates(slackNotificationConfig.getBuildStates());
		slackNotification.setProxy(myMainSettings.getProxyConfig());
        slackNotification.setShowBuildAgent(myMainSettings.getShowBuildAgent());
        slackNotification.setShowElapsedBuildTime(myMainSettings.getShowElapsedBuildTime());
        slackNotification.setShowCommits(myMainSettings.getShowCommits());
        slackNotification.setShowCommitters(myMainSettings.getShowCommitters());
        slackNotification.setFilterBranchName(myMainSettings.getFilterBranchName());
        slackNotification.setShowFailureReason(myMainSettings.getShowFailureReason() == null ? SlackNotificationContentConfig.DEFAULT_SHOW_FAILURE_REASON : myMainSettings.getShowFailureReason());
        slackNotification.setMaxCommitsToDisplay(myMainSettings.getMaxCommitsToDisplay());
        slackNotification.setMentionChannelEnabled(slackNotificationConfig.getMentionChannelEnabled());
		slackNotification.setMentionSlackUserEnabled(slackNotificationConfig.getMentionSlackUserEnabled());
		slackNotification.setMentionWhoTriggeredEnabled(slackNotificationConfig.isMentionWhoTriggeredEnabled());
		slackNotification.setMentionHereEnabled(slackNotificationConfig.getMentionHereEnabled());
        slackNotification.setShowElapsedBuildTime(myMainSettings.getShowElapsedBuildTime());
        if(slackNotificationConfig.getContent() != null && slackNotificationConfig.getContent().isEnabled()) {
            slackNotification.setBotName(slackNotificationConfig.getContent().getBotName());
            slackNotification.setIconUrl(slackNotificationConfig.getContent().getIconUrl());
            slackNotification.setMaxCommitsToDisplay(slackNotificationConfig.getContent().getMaxCommitsToDisplay());
            slackNotification.setShowBuildAgent(slackNotificationConfig.getContent().getShowBuildAgent());
            slackNotification.setShowElapsedBuildTime(slackNotificationConfig.getContent().getShowElapsedBuildTime());
            slackNotification.setShowCommits(slackNotificationConfig.getContent().getShowCommits());
            slackNotification.setShowCommitters(slackNotificationConfig.getContent().getShowCommitters());
            slackNotification.setShowTriggeredBy(slackNotificationConfig.getContent().getShowTriggeredBy());
            slackNotification.setShowFailureReason(slackNotificationConfig.getContent().getShowFailureReason() == null ? SlackNotificationContentConfig.DEFAULT_SHOW_FAILURE_REASON : slackNotificationConfig.getContent().getShowFailureReason());
        }
		Loggers.ACTIVITIES.debug("SlackNotificationListener :: SlackNotification proxy set to "
				+ slackNotification.getProxyHost() + " for " + slackNotificationConfig.getChannel());
	}
    
	private void processBuildEvent(SRunningBuild sRunningBuild, BuildStateEnum state) {

			Loggers.SERVER.debug("About to process Slack notifications for " + sRunningBuild.getProjectId() + " at buildState " + state.getShortName());
			for (SlackNotificationConfigWrapper slackNotificationConfigWrapper : getListOfEnabledSlackNotifications(sRunningBuild.getProjectId())){

                if (state.equals(BuildStateEnum.BUILD_STARTED)){
					slackNotificationConfigWrapper.slackNotification.setPayload(myManager.buildStarted(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
					slackNotificationConfigWrapper.slackNotification.setEnabled(slackNotificationConfigWrapper.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && slackNotificationConfigWrapper.slackNotification.getBuildStates().enabled(BuildStateEnum.BUILD_STARTED));
				} else if (state.equals(BuildStateEnum.BUILD_INTERRUPTED)){
					slackNotificationConfigWrapper.slackNotification.setPayload(myManager.buildInterrupted(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
					slackNotificationConfigWrapper.slackNotification.setEnabled(slackNotificationConfigWrapper.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && slackNotificationConfigWrapper.slackNotification.getBuildStates().enabled(BuildStateEnum.BUILD_INTERRUPTED));
				} else if (state.equals(BuildStateEnum.BEFORE_BUILD_FINISHED)){
					slackNotificationConfigWrapper.slackNotification.setPayload(myManager.beforeBuildFinish(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
					slackNotificationConfigWrapper.slackNotification.setEnabled(slackNotificationConfigWrapper.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && slackNotificationConfigWrapper.slackNotification.getBuildStates().enabled(BuildStateEnum.BEFORE_BUILD_FINISHED));
				} else if (state.equals(BuildStateEnum.BUILD_FINISHED)){
					slackNotificationConfigWrapper.slackNotification.setEnabled(slackNotificationConfigWrapper.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && slackNotificationConfigWrapper.slackNotification.getBuildStates().enabled(
							BuildStateEnum.BUILD_FINISHED, 
							sRunningBuild.getStatusDescriptor().isSuccessful(),
							this.hasBuildChangedHistoricalState(sRunningBuild)));
					slackNotificationConfigWrapper.slackNotification.setPayload(myManager.buildFinished(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));;
				}
				
				doPost(slackNotificationConfigWrapper.slackNotification);
				//Loggers.ACTIVITIES.debug("SlackNotificationListener :: " + myManager.getFormat(slackNotificationConfigWrapper.whc.getPayloadFormat()).getFormatDescription());
	    	}
	}

	/** 
	 * Build a list of Enabled SlackNotifications to pass to the POSTing logic.
	 * @param projectId
	 * @return
	 */
	private List<SlackNotificationConfigWrapper> getListOfEnabledSlackNotifications(String projectId) {
		List<SlackNotificationConfigWrapper> configs = new ArrayList<SlackNotificationConfigWrapper>();
		List<SProject> projects = new ArrayList<SProject>();
		SProject myProject = myBuildServer.getProjectManager().findProjectById(projectId);
		projects.addAll(myProject.getProjectPath());
		for (SProject project : projects){
			SlackNotificationProjectSettings projSettings = (SlackNotificationProjectSettings) mySettings.getSettings(project.getProjectId(), SLACKNOTIFICATIONS_SETTINGS_ATTRIBUTE_NAME);
	    	if (projSettings.isEnabled()){
		    	for (SlackNotificationConfig whc : projSettings.getSlackNotificationsConfigs()){
		    		if (whc.isEnabledForSubProjects() == false && !myProject.getProjectId().equals(project.getProjectId())){
		    			// Sub-projects are disabled and we are a subproject.
		    			if (Loggers.ACTIVITIES.isDebugEnabled()){
			    			Loggers.ACTIVITIES.debug(this.getClass().getSimpleName() + ":getListOfEnabledSlackNotifications() "
			    					+ ":: subprojects not enabled. myProject is: " + myProject.getProjectId() + ". slacknotifications project is: " + project.getProjectId());
		    			}
		    			continue;
		    		}
		    		
		    		if (whc.getEnabled()){
						SlackNotification wh = slackNotificationFactory.getSlackNotification();
						this.getFromConfig(wh, whc);

                        configs.add(new SlackNotificationConfigWrapper(wh, whc));

						 
		    		} else {
		    			Loggers.ACTIVITIES.debug(this.getClass().getSimpleName() 
		    					+ ":processBuildEvent() :: SlackNotification disabled. Will not process " + whc.getChannel());
		    		}
				}
	    	} else {
	    		Loggers.ACTIVITIES.debug("SlackNotificationListener :: SlackNotifications are disasbled for  " + projectId);
	    	}
		}
    	return configs;
	}

	@Override
    public void buildStarted(SRunningBuild sRunningBuild){
    	processBuildEvent(sRunningBuild, BuildStateEnum.BUILD_STARTED);
    }	
	
    @Override
    public void buildFinished(SRunningBuild sRunningBuild){
    	processBuildEvent(sRunningBuild, BuildStateEnum.BUILD_FINISHED);
    }    

    @Override
    public void buildInterrupted(SRunningBuild sRunningBuild) {
    	processBuildEvent(sRunningBuild, BuildStateEnum.BUILD_INTERRUPTED);
    }      

    @Override
    public void beforeBuildFinish(SRunningBuild sRunningBuild) {
    	processBuildEvent(sRunningBuild, BuildStateEnum.BEFORE_BUILD_FINISHED);
	}
    
    @Deprecated
    /** This method has been removed from the TeamCity API as of version 7.1
     * 
     * @param sBuildType
     * @param responsibilityInfoOld
     * @param responsibilityInfoNew
     * @param isUserAction
     */
    public void responsibleChanged(@NotNull SBuildType sBuildType, 
    							   @NotNull ResponsibilityInfo responsibilityInfoOld, 
    							   @NotNull ResponsibilityInfo responsibilityInfoNew, 
    							   boolean isUserAction) {
    	
    	if (myBuildServer.getServerMajorVersion() >= 7){
    		return;
    	}
		Loggers.SERVER.debug(BUILD_STATE_MESSAGE_START + sBuildType.getProjectId() + BUILD_STATE_MESSAGE_END);
		for (SlackNotificationConfigWrapper whcw : getListOfEnabledSlackNotifications(sBuildType.getProjectId())){

						//SlackNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
                        whcw.slackNotification.setPayload(myManager.responsibleChanged(sBuildType,
                                responsibilityInfoOld,
                                responsibilityInfoNew,
                                isUserAction));
						whcw.slackNotification.setEnabled(whcw.whc.isEnabledForBuildType(sBuildType) && whcw.slackNotification.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.slackNotification);
						//Loggers.ACTIVITIES.debug("SlackNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());
		}
     }

	@Override
	public void responsibleChanged(SProject project,
			Collection<TestName> testNames, ResponsibilityEntry entry,
			boolean isUserAction) {
		Loggers.SERVER.debug(BUILD_STATE_MESSAGE_START + project.getProjectId() + BUILD_STATE_MESSAGE_END);
		for (SlackNotificationConfigWrapper whcw : getListOfEnabledSlackNotifications(project.getProjectId())){
                        whcw.slackNotification.setPayload(myManager.responsibleChanged(project,
                                testNames,
                                entry,
                                isUserAction));
						whcw.slackNotification.setEnabled(whcw.slackNotification.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.slackNotification);
						//Loggers.ACTIVITIES.debug("SlackNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());

     	}
	}

	@Override
	public void responsibleChanged(SProject project, TestNameResponsibilityEntry oldTestNameResponsibilityEntry, TestNameResponsibilityEntry newTestNameResponsibilityEntry, boolean isUserAction) {
		Loggers.SERVER.debug(BUILD_STATE_MESSAGE_START + project.getProjectId() + BUILD_STATE_MESSAGE_END);
		for (SlackNotificationConfigWrapper whcw : getListOfEnabledSlackNotifications(project.getProjectId())){
						//SlackNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
						whcw.slackNotification.setPayload(myManager.responsibleChanged(project,
                                oldTestNameResponsibilityEntry,
                                newTestNameResponsibilityEntry,
                                isUserAction));
						whcw.slackNotification.setEnabled(whcw.slackNotification.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.slackNotification);
						//Loggers.ACTIVITIES.debug("SlackNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());

     	}
	}
	
	/**
	 * New version of responsibleChanged, which has some bugfixes, but 
	 * is only available in versions 7.0 and above.    
	 * @param sBuildType
	 * @param responsibilityEntryOld
	 * @param responsibilityEntryNew
	 * @since 7.0
	 */
	@Override
	public void responsibleChanged(@NotNull SBuildType sBuildType,
            @NotNull ResponsibilityEntry responsibilityEntryOld,
            @NotNull ResponsibilityEntry responsibilityEntryNew){
		
		Loggers.SERVER.debug(BUILD_STATE_MESSAGE_START + sBuildType.getProjectId() + BUILD_STATE_MESSAGE_END);
		for (SlackNotificationConfigWrapper whcw : getListOfEnabledSlackNotifications(sBuildType.getProjectId())){
						//SlackNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
                        whcw.slackNotification.setPayload(myManager.responsibleChanged(sBuildType,
                                responsibilityEntryOld,
                                responsibilityEntryNew));
						whcw.slackNotification.setEnabled(whcw.whc.isEnabledForBuildType(sBuildType) && whcw.slackNotification.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.slackNotification);
						//Loggers.ACTIVITIES.debug("SlackNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());
     	}
	}

	@Override
	public void responsibleRemoved(SProject project, TestNameResponsibilityEntry entry){
		
	}
	
    
	/** doPost used by responsibleChanged
	 * 
	 * @param notification
	 */
	public void doPost(SlackNotification notification) {
		notificationUtility.doPost(notification);
	}

	@Nullable
	private SFinishedBuild getPreviousNonPersonalBuild(SRunningBuild paramSRunningBuild)
	  {
	    List<SFinishedBuild> localList = this.myBuildServer.getHistory().getEntriesBefore(paramSRunningBuild, false);

	    for (SFinishedBuild localSFinishedBuild : localList)
	      if (!(localSFinishedBuild.isPersonal())) return localSFinishedBuild;
	    return null;
	}
	
	private boolean hasBuildChangedHistoricalState(SRunningBuild sRunningBuild){
		SFinishedBuild previous = getPreviousNonPersonalBuild(sRunningBuild);
		if (previous != null){
			if (sRunningBuild.getBuildStatus().isSuccessful()){
				return previous.getBuildStatus().isFailed();
			} else if (sRunningBuild.getBuildStatus().isFailed()) {
				return previous.getBuildStatus().isSuccessful();
			}
		}
		return true; 
	}
	
	/**
	 * An inner class to wrap up the SlackNotification and its SlackNotificationConfig into one unit.
	 *
	 */
	
	private class SlackNotificationConfigWrapper {
		 
		private SlackNotification slackNotification;
		
		private SlackNotificationConfig whc;
		
		public SlackNotificationConfigWrapper(SlackNotification slackNotification, SlackNotificationConfig slackNotificationConfig) {
			this.slackNotification = slackNotification;
			this.whc = slackNotificationConfig;
		}
		 public void setSlackNotification(SlackNotification slackNotification){
			 this.slackNotification=slackNotification;
		 }
		 public SlackNotification getSlackNotification(){
			 return slackNotification;
		 }
		 
		 public void setSlackNotificationConfig(SlackNotificationConfig whc){
			 this.whc=whc;
		 }
		 public SlackNotificationConfig getSlackNotificationConfig(){
			 return whc;
		 }
	}

}
