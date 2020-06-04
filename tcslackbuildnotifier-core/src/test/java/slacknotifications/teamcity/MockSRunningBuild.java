package slacknotifications.teamcity;

import jetbrains.buildServer.AgentRestrictor;
import jetbrains.buildServer.BuildProblemData;
import jetbrains.buildServer.StatusDescriptor;
import jetbrains.buildServer.issueTracker.Issue;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.parameters.ParametersProvider;
import jetbrains.buildServer.parameters.ValueResolver;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifacts;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode;
import jetbrains.buildServer.serverSide.artifacts.SArtifactDependency;
import jetbrains.buildServer.serverSide.buildLog.BuildLog;
import jetbrains.buildServer.serverSide.comments.Comment;
import jetbrains.buildServer.serverSide.impl.RunningBuildState;
import jetbrains.buildServer.serverSide.userChanges.CanceledInfo;
import jetbrains.buildServer.serverSide.vcs.VcsLabel;
import jetbrains.buildServer.tests.TestInfo;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.users.UserSet;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.vcs.VcsException;
import jetbrains.buildServer.vcs.VcsRootInstanceEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class MockSRunningBuild implements SRunningBuild {

	private SBuildType sBuildType;
	private MockSBuildAgent sBuildAgent;
	private String buildNumber;
	private MockTriggeredBy triggeredBy;
	private Status status;
	private String statusText;
	private long buildId = 123456;

	public MockSRunningBuild(SBuildType buildType, String triggeredBy, Status status, String statusText, String buildNumber) {
		this.sBuildType = buildType;
		this.sBuildAgent = new MockSBuildAgent("Test Agent", 
									"agent.hostname.domain.name", 
									"192.168.0.1",
									1, 
									"Linux, version 2.6.27.21" );
		sBuildAgent.setRunningBuild(this);
		this.triggeredBy = new MockTriggeredBy(triggeredBy);
		this.status = status;
		this.statusText = statusText;
		this.buildNumber = buildNumber;
	}

	public void addBuildMessage(BuildMessage1 arg0) {
		// TODO Auto-generated method stub

	}

	public void addBuildMessages(List<BuildMessage1> arg0) {
		// TODO Auto-generated method stub

	}

	public SBuildAgent getAgent() {
		return sBuildAgent;
	}

	@Override
	public int getAgentId() {
		return 0;
	}

	public String getAgentAccessCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCompletedPercent() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getCurrentPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getLastBuildActivityTimestamp() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getQueuedAgentId() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSignature() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getTimeSpentSinceLastBuildActivity() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isInterrupted() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isProbablyHanging() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setBuildNumber(String arg0) {
		this.buildNumber = arg0;
	}

	public void setBuildStatus(Status arg0) {
		this.status = arg0;
	}

	public void setInterrupted(RunningBuildState arg0, User arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	public void setSignature(int arg0) {
		// TODO Auto-generated method stub

	}

	public void stop(User arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public Date convertToAgentTime(Date arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Date convertToServerTime(Date arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public File getArtifactsDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	public Comment getBuildComment() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBuildDescription() {
		return this.sBuildType.getDescription();
	}

	public BuildLog getBuildLog() {
		// TODO Auto-generated method stub
		return null;
	}

	public BuildStatistics getBuildStatistics(BuildStatisticsOptions arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public SBuildType getBuildType() {
		return this.sBuildType;
	}

	public List<SVcsModification> getChanges(SelectPrevBuildPolicy arg0,
			boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getClientStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public UserSet<SUser> getCommitters(SelectPrevBuildPolicy arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SVcsModification> getContainingChanges() {
		// TODO Auto-generated method stub
		return null;
	}

	public DownloadedArtifacts getDownloadedArtifacts() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getFileContent(String arg0) throws VcsException {
		// TODO Auto-generated method stub
		return null;
	}

	public BuildStatistics getFullStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<VcsLabel> getLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	public SUser getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

	public DownloadedArtifacts getProvidedArtifacts() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getQueuedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRequestor() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<BuildRevision> getRevisions() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getServerStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public ShortStatistics getShortStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getTags() {
		// TODO Auto-generated method stub
		return null;
	}

	public TriggeredBy getTriggeredBy() {
		// TODO Auto-generated method stub
		return this.triggeredBy;
	}

	public ValueResolver getValueResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isArtifactsExists() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOutOfChangesSequence() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPinned() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isResponsibleNeeded() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUsedByOtherBuilds() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setBuildComment(User arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public void setTags(List<String> arg0) {
		// TODO Auto-generated method stub

	}

	public String getAgentName() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getBuildId() {
		return this.buildId ;
	}

	public String getBuildNumber() {
		return this.buildNumber;
	}

	public Status getBuildStatus() {
		return this.status;
	}

	public String getBuildTypeId() {
		return this.sBuildType.getBuildTypeId();
	}

	public String getBuildTypeName() {
		return this.sBuildType.getName();
	}

	public CanceledInfo getCanceledInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getFinishDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getLogMessages(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProjectId() {
		// TODO Auto-generated method stub
		return this.getBuildType().getProjectId();
	}

	public Date getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public StatusDescriptor getStatusDescriptor() {
		return new StatusDescriptor(this.status, this.statusText);
	}

	public List<TestInfo> getTestMessages(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPersonal() {
		// TODO Auto-generated method stub
		return false;
	}

	public BuildPromotion getBuildPromotion() {
		// TODO Auto-generated method stub
		return null;
	}

	public SBuild getSequenceBuild() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getDurationEstimate() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getDurationOvertime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getElapsedTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getEstimationForTimeLeft() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<SArtifactDependency> getArtifactDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	public BuildArtifacts getArtifacts(BuildArtifactsViewMode arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getBuildOwnParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFirstInternalError() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFirstInternalErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRawBuildNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Issue> getRelatedIssues() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isInternalError() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStartedOnAgent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setTags(User arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		
	}

	public List<String> getCompilationErrorMessages() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<TestInfo> getTestMessages(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public ParametersProvider getParametersProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isOutdated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Nullable
	@Override
	public SFinishedBuild getRecentlyFinishedBuild() {
		return null;
	}

	@Override
	public boolean isAgentLessBuild() {
		return false;
	}

	@Override
	public boolean isCompositeBuild() {
		return false;
	}

	public List<VcsRootInstanceEntry> getVcsRootEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeZone getClientTimeZone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isHasInternalArtifactsOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHasRelatedIssues() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AgentRestrictor getQueuedAgentRestrictor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addBuildProblem(BuildProblemData arg0) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public BuildProblemData addUserBuildProblem(User arg0, String arg1) {
//		 TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Branch getBranch() {
		// TODO Auto-generated method stub
		return new MockBranch();
	}

	@Override
	public List<BuildProblemData> getFailureReasons() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void muteBuildProblems(@NotNull SUser sUser, boolean b, @NotNull String s) {

	}

	@Override
	public BuildProblemData addUserBuildProblem(@NotNull SUser sUser, @NotNull String s) {
		return null;
	}

	@Override
	public SFinishedBuild getPreviousFinished() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasBuildProblemOfType(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public void muteBuildProblems(User arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
//
//	}

	// From 8.0
	
	@Override
	public BigDecimal getStatisticValue(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, BigDecimal> getStatisticValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@NotNull
	@Override
	public Collection<SBuildFeatureDescriptor> getBuildFeaturesOfType(@NotNull String s) {
		return null;
	}

	@Override
	public String getBuildTypeExternalId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProjectExternalId() {
		// TODO Auto-generated method stub
		return null;
	}

}
