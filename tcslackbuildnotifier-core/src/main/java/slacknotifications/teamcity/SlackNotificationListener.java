package slacknotifications.teamcity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.ResponsibilityInfo;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.tests.TestName;

import org.apache.commons.httpclient.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import slacknotifications.SlackNotification;
import slacknotifications.teamcity.payload.SlackNotificationPayload;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;


/**
 * SlackNotificationListner
 * Listens for Server events and then triggers the execution of slacknotifications if configured.
 */
public class SlackNotificationListener extends BuildServerAdapter {
    
    private static final String SLACKNOTIFICATIONS_SETTINGS_ATTRIBUTE_NAME = "slackNotifications";
	private final SBuildServer myBuildServer;
    private final ProjectSettingsManager mySettings;
    private final SlackNotificationMainSettings myMainSettings;
    private final SlackNotificationPayloadManager myManager;
    private final SlackNotificationFactory slackNotificationFactory;
    
    
    public SlackNotificationListener(SBuildServer sBuildServer, ProjectSettingsManager settings,
                                     SlackNotificationMainSettings configSettings, SlackNotificationPayloadManager manager,
                                     SlackNotificationFactory factory) {

        myBuildServer = sBuildServer;
        mySettings = settings;
        myMainSettings = configSettings;
        myManager = manager;
        slackNotificationFactory = factory;
        
        Loggers.SERVER.info("SlackNotificationListener :: Starting");
    }
    
    public void register(){
        myBuildServer.addListener(this);
        Loggers.SERVER.info("SlackNotificationListener :: Registering");
    }

	public void getFromConfig(SlackNotification slackNotification, SlackNotificationConfig slackNotificationConfig){
		slackNotification.setUrl(slackNotificationConfig.getChannel());
		slackNotification.setEnabled(slackNotificationConfig.getEnabled());
		//slackNotification.addParams(slackNotificationConfig.getParams());
		slackNotification.setBuildStates(slackNotificationConfig.getBuildStates());
		slackNotification.setProxy(myMainSettings.getProxyConfigForUrl(slackNotificationConfig.getChannel()));
		Loggers.ACTIVITIES.debug("SlackNotificationListener :: SlackNotification proxy set to "
				+ slackNotification.getProxyHost() + " for " + slackNotificationConfig.getChannel());
	}
    
	private void processBuildEvent(SRunningBuild sRunningBuild, BuildStateEnum state) {

			Loggers.SERVER.debug("About to process SlackNotifications for " + sRunningBuild.getProjectId() + " at buildState " + state.getShortName());
			for (SlackNotificationConfigWrapper whcw : getListOfEnabledSlackNotifications(sRunningBuild.getProjectId())){
				SlackNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
				whcw.wh.setContentType(payloadFormat.getContentType());
				
				if (state.equals(BuildStateEnum.BUILD_STARTED)){
					whcw.wh.setPayload(payloadFormat.buildStarted(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild), whcw.whc.getParams(), whcw.whc.getEnabledTemplates()));
					whcw.wh.setEnabled(whcw.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && whcw.wh.getBuildStates().enabled(BuildStateEnum.BUILD_STARTED));
				} else if (state.equals(BuildStateEnum.BUILD_INTERRUPTED)){
					whcw.wh.setPayload(payloadFormat.buildInterrupted(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild), whcw.whc.getParams(), whcw.whc.getEnabledTemplates()));
					whcw.wh.setEnabled(whcw.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && whcw.wh.getBuildStates().enabled(BuildStateEnum.BUILD_INTERRUPTED));
				} else if (state.equals(BuildStateEnum.BEFORE_BUILD_FINISHED)){
					whcw.wh.setPayload(payloadFormat.beforeBuildFinish(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild), whcw.whc.getParams(), whcw.whc.getEnabledTemplates()));
					whcw.wh.setEnabled(whcw.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && whcw.wh.getBuildStates().enabled(BuildStateEnum.BEFORE_BUILD_FINISHED));
				} else if (state.equals(BuildStateEnum.BUILD_FINISHED)){
					whcw.wh.setEnabled(whcw.whc.isEnabledForBuildType(sRunningBuild.getBuildType()) && whcw.wh.getBuildStates().enabled(
							BuildStateEnum.BUILD_FINISHED, 
							sRunningBuild.getStatusDescriptor().isSuccessful(),
							this.hasBuildChangedHistoricalState(sRunningBuild)));
					whcw.wh.setPayload(payloadFormat.buildFinished(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild), whcw.whc.getParams(), whcw.whc.getEnabledTemplates()));;
				}
				
				doPost(whcw.wh, whcw.whc.getPayloadFormat());
				Loggers.ACTIVITIES.debug("SlackNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());
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
						if (myManager.isRegisteredFormat(whc.getPayloadFormat())){
							configs.add(new SlackNotificationConfigWrapper(wh, whc));
						} else {
							Loggers.ACTIVITIES.warn("SlackNotificationListener :: No registered Payload Handler for " + whc.getPayloadFormat());
						}
						wh = null;
		    		} else {
		    			Loggers.ACTIVITIES.debug(this.getClass().getSimpleName() 
		    					+ ":processBuildEvent() :: SlackNotification disabled. Will not process " + whc.getChannel() + " (" + whc.getPayloadFormat() + ")");
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
		Loggers.SERVER.debug("About to process SlackNotifications for " + sBuildType.getProjectId() + " at buildState responsibilityChanged");
		for (SlackNotificationConfigWrapper whcw : getListOfEnabledSlackNotifications(sBuildType.getProjectId())){

						SlackNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
						whcw.wh.setContentType(payloadFormat.getContentType());
						whcw.wh.setPayload(payloadFormat.responsibleChanged(sBuildType, 
									responsibilityInfoOld, 
									responsibilityInfoNew, 
									isUserAction, 
									whcw.whc.getParams(), whcw.whc.getEnabledTemplates()));
						whcw.wh.setEnabled(whcw.whc.isEnabledForBuildType(sBuildType) && whcw.wh.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.wh, whcw.whc.getPayloadFormat());
						Loggers.ACTIVITIES.debug("SlackNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());
		}
     }

	@Override
	public void responsibleChanged(SProject project,
			Collection<TestName> testNames, ResponsibilityEntry entry,
			boolean isUserAction) {
		Loggers.SERVER.debug("About to process SlackNotifications for " + project.getProjectId() + " at buildState responsibilityChanged");
		for (SlackNotificationConfigWrapper whcw : getListOfEnabledSlackNotifications(project.getProjectId())){
						SlackNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
						whcw.wh.setContentType(payloadFormat.getContentType());
						whcw.wh.setPayload(payloadFormat.responsibleChanged(project, 
								testNames, 
								entry, 
									isUserAction, 
									whcw.whc.getParams(), whcw.whc.getEnabledTemplates()));
						whcw.wh.setEnabled(whcw.wh.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.wh, whcw.whc.getPayloadFormat());
						Loggers.ACTIVITIES.debug("SlackNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());

     	}
	}

	@Override
	public void responsibleChanged(SProject project, TestNameResponsibilityEntry oldTestNameResponsibilityEntry, TestNameResponsibilityEntry newTestNameResponsibilityEntry, boolean isUserAction) {
		Loggers.SERVER.debug("About to process SlackNotifications for " + project.getProjectId() + " at buildState responsibilityChanged");
		for (SlackNotificationConfigWrapper whcw : getListOfEnabledSlackNotifications(project.getProjectId())){
						SlackNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
						whcw.wh.setContentType(payloadFormat.getContentType());
						whcw.wh.setPayload(payloadFormat.responsibleChanged(project, 
									oldTestNameResponsibilityEntry, 
									newTestNameResponsibilityEntry, 
									isUserAction, 
									whcw.whc.getParams(), whcw.whc.getEnabledTemplates()));
						whcw.wh.setEnabled(whcw.wh.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.wh, whcw.whc.getPayloadFormat());
						Loggers.ACTIVITIES.debug("SlackNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());

     	}
	}
	
	/**
	 * New version of responsibleChanged, which has some bugfixes, but 
	 * is only available in versions 7.0 and above.    
	 * @param bt
	 * @param oldValue
	 * @param newValue
	 * @since 7.0
	 */
	@Override
	public void responsibleChanged(@NotNull SBuildType sBuildType,
            @NotNull ResponsibilityEntry responsibilityEntryOld,
            @NotNull ResponsibilityEntry responsibilityEntryNew){
		
		Loggers.SERVER.debug("About to process SlackNotifications for " + sBuildType.getProjectId() + " at buildState responsibilityChanged");
		for (SlackNotificationConfigWrapper whcw : getListOfEnabledSlackNotifications(sBuildType.getProjectId())){
						SlackNotificationPayload payloadFormat = myManager.getFormat(whcw.whc.getPayloadFormat());
						whcw.wh.setContentType(payloadFormat.getContentType());
						whcw.wh.setPayload(payloadFormat.responsibleChanged(sBuildType, 
									responsibilityEntryOld, 
									responsibilityEntryNew, 
									whcw.whc.getParams(), whcw.whc.getEnabledTemplates()));
						whcw.wh.setEnabled(whcw.whc.isEnabledForBuildType(sBuildType) && whcw.wh.getBuildStates().enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
						doPost(whcw.wh, whcw.whc.getPayloadFormat());
						Loggers.ACTIVITIES.debug("SlackNotificationListener :: " + myManager.getFormat(whcw.whc.getPayloadFormat()).getFormatDescription());
     	}
	}
	
	public void responsibleRemoved(SProject project, TestNameResponsibilityEntry entry){
		
	}
	
    
	/** doPost used by responsibleChanged
	 * 
	 * @param wh
	 * @param payloadFormat
	 */
	private void doPost(SlackNotification wh, String payloadFormat) {
		try {
			if (wh.isEnabled()){
				wh.post();
				Loggers.SERVER.info(this.getClass().getSimpleName() + " :: SlackNotification triggered : "
						+ wh.getUrl() + " using format " + payloadFormat 
						+ " returned " + wh.getStatus() 
						+ " " + wh.getErrorReason());	
				Loggers.SERVER.debug(this.getClass().getSimpleName() + ":doPost :: content dump: " + wh.getPayload());
				if (wh.isErrored()){
					Loggers.SERVER.error(wh.getErrorReason());
				}
				if ((wh.getStatus() == null || wh.getStatus() > HttpStatus.SC_OK))
					Loggers.ACTIVITIES.warn("SlackNotificationListener :: " + wh.getParam("projectId") + " SlackNotification (url: " + wh.getUrl() + " proxy: " + wh.getProxyHost() + ":" + wh.getProxyPort()+") returned HTTP status " + wh.getStatus().toString());
			} else {
				Loggers.SERVER.debug("SlackNotification NOT triggered: "
						+ wh.getParam("buildStatus") + " " + wh.getUrl());	
			}
		} catch (FileNotFoundException e) {
			Loggers.SERVER.warn(this.getClass().getName() + ":doPost :: " 
					+ "A FileNotFoundException occurred while attempting to execute SlackNotification (" + wh.getUrl() + "). See the following stacktrace");
			Loggers.SERVER.warn(e);
		} catch (IOException e) {
			Loggers.SERVER.warn(this.getClass().getName() + ":doPost :: " 
					+ "An IOException occurred while attempting to execute SlackNotification (" + wh.getUrl() + "). See the following stacktrace");
			Loggers.SERVER.warn(e);
		}
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
		public SlackNotification wh;
		public SlackNotificationConfig whc;
		
		public SlackNotificationConfigWrapper(SlackNotification slackNotification, SlackNotificationConfig slackNotificationConfig) {
			this.wh = slackNotification;
			this.whc = slackNotificationConfig;
		}
	}

}
