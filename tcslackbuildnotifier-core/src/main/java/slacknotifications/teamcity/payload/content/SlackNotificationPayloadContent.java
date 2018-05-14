package slacknotifications.teamcity.payload.content;

import jetbrains.buildServer.BuildProblemData;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.tests.TestInfo;
import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.Loggers;
import slacknotifications.teamcity.TeamCityIdResolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SlackNotificationPayloadContent {
	final PayloadContentCommits payloadCommits;
	String buildStatus;
    String buildResult;
    String buildResultPrevious;
    String buildResultDelta;
    String notifyType;
    String buildFullName;
    String buildName;
    String buildId;
    String buildTypeId;
    String buildInternalTypeId;
    String buildExternalTypeId;
    String buildStatusUrl;
    String buildStatusHtml;
    String rootUrl;
    String projectName;
    String projectId;
    String projectInternalId;
    String projectExternalId;
    String buildNumber;
    String agentName;
    String agentOs;
    String agentHostname;
    String triggeredBy;
    String comment;
    String message;
    String text;
    String branchName;
    String branchDisplayName;
    String buildStateDescription;
    String progressSummary;
	private boolean isFirstFailedBuild;

    Boolean branchIsDefault;

    Branch branch;

/*
    public final static String BUILD_STATUS_FAILURE   = "failure";
    public final static String BUILD_STATUS_SUCCESS   = "success";
    public final static String BUILD_STATUS_RUNNING   = "running";
    public final static String BUILD_STATUS_NO_CHANGE = "unchanged";
    public final static String BUILD_STATUS_FIXED     = "fixed";
    public final static String BUILD_STATUS_BROKEN    = "broken";
    public final static String BUILD_STATUS_UNKNOWN   = "unknown";*/

    public final static String BUILD_STATUS_FAILURE   = "Failed";
    public final static String BUILD_STATUS_SUCCESS   = "Succeeded";
    public final static String BUILD_STATUS_RUNNING   = "Running";
    public final static String BUILD_STATUS_NO_CHANGE = "Unchanged";
    public final static String BUILD_STATUS_FIXED     = "Fixed";
    public final static String BUILD_STATUS_BROKEN    = "Broken";
    public final static String BUILD_STATUS_UNKNOWN   = "Unknown";
    private String buildLink;
    private String color;
    private long elapsedTime;
    private boolean isComplete;
    private ArrayList<String> failedBuildMessages = new ArrayList<String>();
    private ArrayList<String> failedTestNames = new ArrayList<String>();

    public SlackNotificationPayloadContent(){
		payloadCommits = new PayloadContentCommits();
        }

    /**
		 * Constructor: Only called by RepsonsibilityChanged.
		 * @param server
		 * @param buildType
		 * @param buildState
		 */
		public SlackNotificationPayloadContent(SBuildServer server, SBuildType buildType, BuildStateEnum buildState) {
			payloadCommits = new PayloadContentCommits();
			populateCommonContent(server, buildType, buildState);
		}

		/**
		 * Constructor: Called by everything except RepsonsibilityChanged.
		 * @param server
		 * @param sRunningBuild
		 * @param previousBuild
		 * @param buildState
		 */
		public SlackNotificationPayloadContent(SBuildServer server, SRunningBuild sRunningBuild, SFinishedBuild previousBuild,
                                               BuildStateEnum buildState) {
			payloadCommits = new PayloadContentCommits();
            populateCommonContent(server, sRunningBuild, previousBuild, buildState);
    		populateMessageAndText(sRunningBuild, buildState);
			payloadCommits.populateCommits(sRunningBuild);
    		populateArtifacts(sRunningBuild);
            populateResults(sRunningBuild);
		}

    private void populateResults(SRunningBuild sRunningBuild) {
        List<BuildProblemData> failureReasons = sRunningBuild.getFailureReasons();
        if(failureReasons == null){
            return;
        }
        HashSet<String> failureTestNames = new HashSet<String>();
        HashSet<String> failureMessages = new HashSet<String>();
        for(BuildProblemData reason : failureReasons){
            if(reason.getType() == BuildProblemData.TC_FAILED_TESTS_TYPE){
                List<TestInfo> failedTestMessages = sRunningBuild.getTestMessages(0, 2000);
                if(!failedTestMessages.isEmpty()) {
                    for (TestInfo failedTest : failedTestMessages) {
                        failureTestNames.add(failedTest.getName());
                    }
                }
                else {
                    failureMessages.add(reason.getDescription());
                }
            }
            else {
                failureMessages.add(reason.getDescription());
            }
        }
        failedBuildMessages = new ArrayList<String>(failureMessages);
        failedTestNames = new ArrayList<String>(failureTestNames);
    }

    private void populateCommits(SRunningBuild sRunningBuild) {

		payloadCommits.populateCommits(sRunningBuild);
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
		private void populateCommonContent(SBuildServer server, SBuildType buildType, BuildStateEnum state) {
			setBuildFullName(buildType.getFullName());
			setBuildName(buildType.getName());
			setBuildTypeId(TeamCityIdResolver.getBuildTypeId(buildType));
			setBuildStatusUrl(server.getRootUrl() + "/viewLog.html?buildTypeId=" + buildType.getBuildTypeId() + "&buildId=lastFinished");

		}
		
		private void populateMessageAndText(SRunningBuild sRunningBuild,
				BuildStateEnum state) {
			// Message is a long form message, for on webpages or in email.

			// Text is designed to be shorter, for use in Text messages and the like.    		
    		setText(getBuildDescriptionWithLinkSyntax()
                    + " has " + state.getDescriptionSuffix() + ". Status: " + this.buildResult);


		}

    /**
     * Used by everything except ResponsibilityChanged. Is passed a valid build instance.
     *
     * @param server
     * @param sRunningBuild
     * @param previousBuild
     * @param buildState
     */
    private void populateCommonContent(SBuildServer server, SRunningBuild sRunningBuild, SFinishedBuild previousBuild,
                                       BuildStateEnum buildState) {
        setBuildResult(sRunningBuild, previousBuild, buildState);
        setBuildFullName(sRunningBuild.getBuildType().getFullName());
        setBuildName(sRunningBuild.getBuildType().getName());
        setTriggeredBy(sRunningBuild.getTriggeredBy().getAsString());
        setBuildId(Long.toString(sRunningBuild.getBuildId()));
        setBuildTypeId(TeamCityIdResolver.getBuildTypeId(sRunningBuild.getBuildType()));
        setAgentName(sRunningBuild.getAgentName());
        setElapsedTime(sRunningBuild.getElapsedTime());

        try {
            if (sRunningBuild.getBranch() != null) {
                setBranch(sRunningBuild.getBranch());
                setBranchDisplayName(getBranch().getDisplayName());
                setBranchIsDefault(getBranch().isDefaultBranch());
            } else {
                Loggers.SERVER.debug("SlackNotificationPayloadContent :: Branch is null. Either feature branch support is not configured or Teamcity does not support feature branches on this VCS");
            }

        } catch (NoSuchMethodError e) {
            Loggers.SERVER.debug("SlackNotificationPayloadContent :: Could not get Branch Info by calling sRunningBuild.getBranch(). Probably an old version of TeamCity");
        }
        setBuildStatusUrl(server.getRootUrl() + "/viewLog.html?buildTypeId=" + getBuildTypeId() + "&buildId=" + getBuildId());
        String branchSuffix = (getBranchIsDefault() != null && getBranchIsDefault()) || getBranchDisplayName() == null ? "" : (" [" + getBranchDisplayName() + "]");
        setBuildDescriptionWithLinkSyntax(String.format("<" + getBuildStatusUrl() + "|" + getBuildResult() + " - " + sRunningBuild.getBuildType().getFullName() + " #" + sRunningBuild.getBuildNumber() + branchSuffix + ">"));
    }
		
		

		private Branch getBranch() {
			return this.branch;
		}
		
		public void setBranch(Branch branch) {
			this.branch = branch;
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

		public Boolean isMergeBranch() { return this.branchName != null && this.branchName.endsWith("/merge");}

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
						this.buildResultPrevious = BUILD_STATUS_SUCCESS;
					} else {
						this.buildResultPrevious = BUILD_STATUS_FAILURE;
					}
				} else {
					this.buildResultPrevious = BUILD_STATUS_RUNNING;
				}
			} else {
				this.buildResultPrevious = BUILD_STATUS_UNKNOWN;
			}

            isComplete = buildState == BuildStateEnum.BUILD_FINISHED;

			if (buildState == BuildStateEnum.BEFORE_BUILD_FINISHED || buildState == BuildStateEnum.BUILD_FINISHED){ 
				if (sRunningBuild.getStatusDescriptor().isSuccessful()){
					this.buildResult = BUILD_STATUS_SUCCESS;
                    this.color = "good";
					if (this.buildResultPrevious.equals(this.buildResult)){
						this.buildResultDelta = BUILD_STATUS_NO_CHANGE;
					} else {
						this.buildResultDelta = BUILD_STATUS_FIXED;
					}
				} else {
					this.buildResult = BUILD_STATUS_FAILURE;
                    this.color = "danger";
					if (this.buildResultPrevious.equals(this.buildResult)){
						this.buildResultDelta = BUILD_STATUS_NO_CHANGE;
					} else {
						this.buildResultDelta = BUILD_STATUS_BROKEN;
                        this.setFirstFailedBuild(true);
					}
				}
			} else {
				this.buildResult = BUILD_STATUS_RUNNING;
				this.buildResultDelta = BUILD_STATUS_UNKNOWN;
			}
			
		}

		// Getters and setters
		

		public String getBuildResult() {
			return buildResult;
		}

		public void setBuildResult(String buildResult) {
			this.buildResult = buildResult;
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

		public String getTriggeredBy() {
			return triggeredBy;
		}

		public void setTriggeredBy(String triggeredBy) {
			this.triggeredBy = triggeredBy;
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






		public String getAgentName() {
			return agentName;
		}

		public void setAgentName(String agentName) {
			this.agentName = agentName;
		}


		public String getBuildStatusUrl() {
			return buildStatusUrl;
		}

		public void setBuildStatusUrl(String buildStatusUrl) {
			this.buildStatusUrl = buildStatusUrl;
		}


		public String getText() {
			return text;
		}


		public void setText(String text) {
			this.text = text;
		}


    public void setBuildDescriptionWithLinkSyntax(String buildLink) {
        this.buildLink = buildLink;
    }

    public String getBuildDescriptionWithLinkSyntax() {
        return buildLink;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public List<Commit> getCommits() {
		return payloadCommits.getCommits();
	}

    public void setCommits(List<Commit> commits) {
		payloadCommits.setCommits(commits);
	}

    public boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean getIsFirstFailedBuild() {
        return isFirstFailedBuild;
    }

    public void setFirstFailedBuild(boolean isFirstFailedBuild) {
        this.isFirstFailedBuild = isFirstFailedBuild;
    }

    public ArrayList<String> getFailedBuildMessages() {
        return failedBuildMessages;
    }

    public ArrayList<String> getFailedTestNames() {
        return failedTestNames;
    }
}