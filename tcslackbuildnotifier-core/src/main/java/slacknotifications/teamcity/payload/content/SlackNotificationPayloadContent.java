package slacknotifications.teamcity.payload.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import jetbrains.buildServer.serverSide.Branch;
import jetbrains.buildServer.serverSide.SBuildRunnerDescriptor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.serverSide.SRunningBuild;
import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.Loggers;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.payload.SlackNotificationPayload;
import slacknotifications.teamcity.payload.SlackNotificationPayloadDefaultTemplates;
import slacknotifications.teamcity.payload.util.SlackNotificationBeanUtilsVariableResolver;
import slacknotifications.teamcity.payload.util.VariableMessageBuilder;

public class SlackNotificationPayloadContent {
		String buildStatus,
		buildResult, buildResultPrevious, buildResultDelta,
		notifyType,
		buildFullName,
		buildName,
		buildId,
		buildTypeId,
		buildInternalTypeId,
		buildExternalTypeId,
		buildStatusUrl,
		buildStatusHtml,
		rootUrl,
		projectName,
		projectId,
		projectInternalId,
		projectExternalId,
		buildNumber,
		agentName,
		agentOs,
		agentHostname,
		triggeredBy,
		comment,
		message,
		text,
		branchName,
		branchDisplayName,
		buildStateDescription;
		Boolean branchIsDefault;
		
		Branch branch;
		List<String> buildRunners;
		ExtraParametersMap extraParameters;
		
		/**
		 * Constructor: Only called by RepsonsibilityChanged.
		 * @param server
		 * @param buildType
		 * @param buildState
		 * @param extraParameters
		 */
		public SlackNotificationPayloadContent(SBuildServer server, SBuildType buildType, BuildStateEnum buildState, Map<String, String> extraParameters, Map<String, String> templates) {
			populateCommonContent(server, buildType, buildState, templates);
			this.extraParameters =  new ExtraParametersMap(extraParameters);
		}

		/**
		 * Constructor: Called by everything except RepsonsibilityChanged.
		 * @param server
		 * @param sRunningBuild
		 * @param previousBuild
		 * @param buildState
		 * @param extraParameters
		 */
		public SlackNotificationPayloadContent(SBuildServer server, SRunningBuild sRunningBuild, SFinishedBuild previousBuild,
                                               BuildStateEnum buildState,
                                               Map<String, String> extraParameters,
                                               Map<String, String> templates) {
			
    		populateCommonContent(server, sRunningBuild, previousBuild, buildState, templates);
    		populateMessageAndText(sRunningBuild, buildState, templates);
    		populateArtifacts(sRunningBuild);
    		this.extraParameters =  new ExtraParametersMap(extraParameters);
		}

		private void populateArtifacts(SRunningBuild runningBuild) {
			//ArtifactsInfo artInfo = new ArtifactsInfo(runningBuild);
			//artInfo.
			
		}

		/**
		 * Used by RepsonsiblityChanged.
		 * Therefore, does not have access to a specific build instance.
		 * @param server
		 * @param buildType
		 * @param state
		 */
		private void populateCommonContent(SBuildServer server, SBuildType buildType, BuildStateEnum state, Map<String,String> templates) {
			setNotifyType(state.getShortName());
			setBuildRunners(buildType.getBuildRunners());
			setBuildFullName(buildType.getFullName().toString());
			setBuildName(buildType.getName());
			setBuildTypeId(TeamCityIdResolver.getBuildTypeId(buildType));
    		setBuildInternalTypeId(TeamCityIdResolver.getInternalBuildId(buildType));
    		setBuildExternalTypeId(TeamCityIdResolver.getExternalBuildId(buildType));
			setProjectName(buildType.getProjectName());
			setProjectId(TeamCityIdResolver.getProjectId(buildType.getProject()));
			setProjectInternalId(TeamCityIdResolver.getInternalProjectId(buildType.getProject()));
			setProjectExternalId(TeamCityIdResolver.getExternalProjectId(buildType.getProject()));
			setBuildStatusUrl(server.getRootUrl() + "/viewLog.html?buildTypeId=" + buildType.getBuildTypeId() + "&buildId=lastFinished");
			setBuildStateDescription(state.getDescriptionSuffix());
		}
		
		private void populateMessageAndText(SRunningBuild sRunningBuild,
				BuildStateEnum state, Map<String,String> templates) {
			// Message is a long form message, for on webpages or in email.
    		setMessage("Build " + sRunningBuild.getBuildType().getFullName().toString() 
    				+ " has " + state.getDescriptionSuffix() + ". This is build number " + sRunningBuild.getBuildNumber() 
    				+ ", has a status of \"" + this.buildResult + "\" and was triggered by " + sRunningBuild.getTriggeredBy().getAsString());
    		
			// Text is designed to be shorter, for use in Text messages and the like.    		
    		setText(sRunningBuild.getBuildType().getFullName().toString() 
    				+ " has " + state.getDescriptionSuffix() + ". Status: " + this.buildResult);
		}

		/**
		 * Used by everything except ResponsibilityChanged. Is passed a valid build instance. 
		 * @param server
		 * @param sRunningBuild
		 * @param previousBuild
		 * @param buildState
		 */
		private void populateCommonContent(SBuildServer server, SRunningBuild sRunningBuild, SFinishedBuild previousBuild,
				BuildStateEnum buildState, Map<String, String> templates) {
			setBuildStatus(sRunningBuild.getStatusDescriptor().getText());
			setBuildResult(sRunningBuild, previousBuild, buildState);
    		setNotifyType(buildState.getShortName());
    		setBuildRunners(sRunningBuild.getBuildType().getBuildRunners());
    		setBuildFullName(sRunningBuild.getBuildType().getFullName().toString());
    		setBuildName(sRunningBuild.getBuildType().getName());
			setBuildId(Long.toString(sRunningBuild.getBuildId()));
			setBuildTypeId(TeamCityIdResolver.getBuildTypeId(sRunningBuild.getBuildType()));
    		setBuildInternalTypeId(TeamCityIdResolver.getInternalBuildId(sRunningBuild.getBuildType()));
    		setBuildExternalTypeId(TeamCityIdResolver.getExternalBuildId(sRunningBuild.getBuildType()));
    		setProjectName(sRunningBuild.getBuildType().getProjectName());
    		setProjectId(TeamCityIdResolver.getProjectId(sRunningBuild.getBuildType().getProject()));
    		setProjectInternalId(TeamCityIdResolver.getInternalProjectId(sRunningBuild.getBuildType().getProject()));
    		setProjectExternalId(TeamCityIdResolver.getExternalProjectId(sRunningBuild.getBuildType().getProject()));
    		setBuildNumber(sRunningBuild.getBuildNumber());
    		setAgentName(sRunningBuild.getAgentName());
    		setAgentOs(sRunningBuild.getAgent().getOperatingSystemName());
    		setAgentHostname(sRunningBuild.getAgent().getHostName());
    		setTriggeredBy(sRunningBuild.getTriggeredBy().getAsString());
    		try {
    			if (sRunningBuild.getBranch() != null){
	    			setBranch(sRunningBuild.getBranch());
	    			setBranchName(getBranch().getName());
	    			setBranchDisplayName(getBranch().getDisplayName());
	    			setBranchIsDefault(getBranch().isDefaultBranch());
    			} else {
    				Loggers.SERVER.debug("SlackNotificationPayloadContent :: Branch is null. Either feature branch support is not configured or Teamcity does not support feature branches on this VCS");
    			}
    			
    		} catch (NoSuchMethodError e){
    			Loggers.SERVER.debug("SlackNotificationPayloadContent :: Could not get Branch Info by calling sRunningBuild.getBranch(). Probably an old version of TeamCity");
    		}
    		setBuildStatusUrl(server.getRootUrl() + "/viewLog.html?buildTypeId=" + getBuildTypeId() + "&buildId=" + getBuildId());
    		setBuildStateDescription(buildState.getDescriptionSuffix());
    		setRootUrl(server.getRootUrl());
			setBuildStatusHtml(buildState, templates.get(SlackNotificationPayloadDefaultTemplates.HTML_BUILDSTATUS_TEMPLATE));
		}
		
		
		public String getBuildInternalTypeId() {
			return this.buildInternalTypeId;
		}

		public void setBuildInternalTypeId(String internalBuildId) {
			this.buildInternalTypeId = internalBuildId;
		}
		
		public String getBuildExternalTypeId() {
			return this.buildExternalTypeId;
		}
		
		public void setBuildExternalTypeId(String externalBuildId) {
			this.buildExternalTypeId = externalBuildId;
		}

		public String getProjectInternalId() {
			return this.projectInternalId;
		}
		
		public void setProjectInternalId(String projectId) {
			this.projectInternalId = projectId;
		}

		private Branch getBranch() {
			return this.branch;
		}
		
		public void setBranch(Branch branch) {
			this.branch = branch;
		}
		
		public String getBranchName() {
			return this.branchName;
		}
			 
		public void setBranchName(String branchName) {
			this.branchName = branchName;
		}
		
		public String getBranchDisplayName() {
			return this.branchDisplayName;
		}
		
		public void setBranchDisplayName(String displayName) {
			this.branchDisplayName = displayName;
		}

		public Boolean getBranchIsDefault() {
			return branchIsDefault;
		}
		
		public Boolean isBranchIsDefault() {
			return branchIsDefault;
		}

		public void setBranchIsDefault(boolean branchIsDefault) {
			this.branchIsDefault = branchIsDefault;
		}

		/**
		 * Determines a useful build result. The one from TeamCity can't be trusted because it
		 * is not set until all the Notifiers have run, of which we are one. 
		 * @param sRunningBuild
		 * @param previousBuild
		 * @param buildState
		 */
		private void setBuildResult(SRunningBuild sRunningBuild,
				SFinishedBuild previousBuild, BuildStateEnum buildState) {

			if (previousBuild != null){
				if (previousBuild.isFinished()){ 
					if (previousBuild.getStatusDescriptor().isSuccessful()){
						this.buildResultPrevious = SlackNotificationPayload.BUILD_STATUS_SUCCESS;
					} else {
						this.buildResultPrevious = SlackNotificationPayload.BUILD_STATUS_FAILURE;
					}
				} else {
					this.buildResultPrevious = SlackNotificationPayload.BUILD_STATUS_RUNNING;
				}
			} else {
				this.buildResultPrevious = SlackNotificationPayload.BUILD_STATUS_UNKNOWN;
			}

			if (buildState == BuildStateEnum.BEFORE_BUILD_FINISHED || buildState == BuildStateEnum.BUILD_FINISHED){ 
				if (sRunningBuild.getStatusDescriptor().isSuccessful()){
					this.buildResult = SlackNotificationPayload.BUILD_STATUS_SUCCESS;
					if (this.buildResultPrevious.equals(this.buildResult)){
						this.buildResultDelta = SlackNotificationPayload.BUILD_STATUS_NO_CHANGE;
					} else {
						this.buildResultDelta = SlackNotificationPayload.BUILD_STATUS_FIXED;
					}
				} else {
					this.buildResult = SlackNotificationPayload.BUILD_STATUS_FAILURE;
					if (this.buildResultPrevious.equals(this.buildResult)){
						this.buildResultDelta = SlackNotificationPayload.BUILD_STATUS_NO_CHANGE;
					} else {
						this.buildResultDelta = SlackNotificationPayload.BUILD_STATUS_BROKEN;
					}
				}
			} else {
				this.buildResult = SlackNotificationPayload.BUILD_STATUS_RUNNING;
				this.buildResultDelta = SlackNotificationPayload.BUILD_STATUS_UNKNOWN;
			}
			
		}

		// Getters and setters
		
		public String getBuildStatus() {
			return buildStatus;
		}

		public void setBuildStatus(String buildStatus) {
			this.buildStatus = buildStatus;
		}

		public String getBuildResult() {
			return buildResult;
		}

		public void setBuildResult(String buildResult) {
			this.buildResult = buildResult;
		}

		public String getBuildResultPrevious() {
			return buildResultPrevious;
		}

		public void setBuildResultPrevious(String buildResultPrevious) {
			this.buildResultPrevious = buildResultPrevious;
		}

		public String getBuildResultDelta() {
			return buildResultDelta;
		}

		public void setBuildResultDelta(String buildResultDelta) {
			this.buildResultDelta = buildResultDelta;
		}

		public String getNotifyType() {
			return notifyType;
		}

		public void setNotifyType(String notifyType) {
			this.notifyType = notifyType;
		}

		public List<String> getBuildRunners() {
			return buildRunners;
		}

		public void setBuildRunners(List<SBuildRunnerDescriptor> list) {
			if (list != null){
				buildRunners = new ArrayList<String>(); 
				for (SBuildRunnerDescriptor runner : list){
					buildRunners.add(runner.getRunType().getDisplayName());
				}
			}
		}

		public String getBuildFullName() {
			return buildFullName;
		}

		public void setBuildFullName(String buildFullName) {
			this.buildFullName = buildFullName;
		}

		public String getBuildName() {
			return buildName;
		}

		public void setBuildName(String buildName) {
			this.buildName = buildName;
		}

		public String getBuildId() {
			return buildId;
		}

		public void setBuildId(String buildId) {
			this.buildId = buildId;
		}

		public String getBuildTypeId() {
			return buildTypeId;
		}

		public void setBuildTypeId(String buildTypeId) {
			this.buildTypeId = buildTypeId;
		}

		public String getProjectName() {
			return projectName;
		}

		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}

		public String getProjectId() {
			return projectId;
		}

		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}
		
		public String getProjectExternalId() {
			return projectExternalId;
		}
		
		public void setProjectExternalId(String projectExternalId) {
			this.projectExternalId = projectExternalId;
		}

		public String getBuildNumber() {
			return buildNumber;
		}

		public void setBuildNumber(String buildNumber) {
			this.buildNumber = buildNumber;
		}

		public String getAgentName() {
			return agentName;
		}

		public void setAgentName(String agentName) {
			this.agentName = agentName;
		}

		public String getAgentOs() {
			return agentOs;
		}

		public void setAgentOs(String agentOs) {
			this.agentOs = agentOs;
		}

		public String getAgentHostname() {
			return agentHostname;
		}

		public void setAgentHostname(String agentHostname) {
			this.agentHostname = agentHostname;
		}

		public String getTriggeredBy() {
			return triggeredBy;
		}

		public void setTriggeredBy(String triggeredBy) {
			this.triggeredBy = triggeredBy;
		}

		public String getBuildStatusUrl() {
			return buildStatusUrl;
		}

		public void setBuildStatusUrl(String buildStatusUrl) {
			this.buildStatusUrl = buildStatusUrl;
		}

		public String getRootUrl() {
			return rootUrl;
		}

		public void setRootUrl(String rootUrl) {
			this.rootUrl = rootUrl;
		}

		public String getBuildStateDescription() {
			return buildStateDescription;
		}

		public void setBuildStateDescription(String buildStateDescription) {
			this.buildStateDescription = buildStateDescription;
		}

		public String getBuildStatusHtml() {
			return buildStatusHtml;
		}

		public void setBuildStatusHtml(String buildStatusHtml) {
			this.buildStatusHtml = buildStatusHtml;
		}

		
		private void setBuildStatusHtml(BuildStateEnum buildState, final String htmlStatusTemplate) {
			
			VariableMessageBuilder builder = VariableMessageBuilder.create(htmlStatusTemplate, new SlackNotificationBeanUtilsVariableResolver(this));
			this.buildStatusHtml = builder.build();
		}		
		
		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getMessage() {
			return message;
		}


		public void setMessage(String message) {
			this.message = message;
		}


		public String getText() {
			return text;
		}


		public void setText(String text) {
			this.text = text;
		}

		public ExtraParametersMap getExtraParameters() {
			if (this.extraParameters.size() > 0){
				VariableMessageBuilder builder;
                SlackNotificationBeanUtilsVariableResolver resolver = new SlackNotificationBeanUtilsVariableResolver(this);
				ExtraParametersMap resolvedParametersMap = new ExtraParametersMap(extraParameters);
				for (Entry<String,String> entry  : extraParameters.getEntriesAsSet()){
					builder = VariableMessageBuilder.create(entry.getValue(), resolver);
					resolvedParametersMap.put(entry.getKey(), builder.build());
				}
				return resolvedParametersMap;
			} else {
				return null;
			}
				
		}

		public void setExtraParameters(SortedMap<String, String> extraParameters) {
			this.extraParameters = new ExtraParametersMap(extraParameters);
		}

		
}