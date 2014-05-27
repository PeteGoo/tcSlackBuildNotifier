/**
 * 
 */
package slacknotifications.teamcity.payload.format;

import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.ResponsibilityInfo;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.tests.TestName;
import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.payload.SlackNotificationPayload;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;

public abstract class SlackNotificationPayloadGeneric implements SlackNotificationPayload {
	
	SlackNotificationPayloadManager myManager;
	
	public SlackNotificationPayloadGeneric(SlackNotificationPayloadManager manager){
		this.setPayloadManager(manager);
	}

	@Override
	public void setPayloadManager(SlackNotificationPayloadManager slackNotificationPayloadManager){
		myManager = slackNotificationPayloadManager;
	}
	
	public abstract void register();
		
	
	@Override
	public String beforeBuildFinish(SRunningBuild runningBuild, SFinishedBuild previousBuild,
			SortedMap<String,String> extraParameters, Map<String,String> templates) {
		SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(myManager.getServer(), runningBuild, previousBuild, BuildStateEnum.BEFORE_BUILD_FINISHED, extraParameters, templates);
		return getStatusAsString(content);
	}

	/**
	 * buildChangedStatus has been deprecated because it alluded to build history status, which was incorrect. 
	 * It will no longer be called by the SlackNotificationListener
	 */
	@Deprecated
	@Override
	public String buildChangedStatus(SRunningBuild runningBuild, SFinishedBuild previousBuild,
			Status oldStatus, Status newStatus,
			SortedMap<String,String> extraParameters, Map<String,String> templates) {
		return "";
	}

	@Override
	public String buildFinished(SRunningBuild runningBuild, SFinishedBuild previousBuild,
			SortedMap<String,String> extraParameters, Map<String,String> templates) {
		SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(myManager.getServer(), runningBuild, previousBuild, BuildStateEnum.BUILD_FINISHED, extraParameters, templates);
		return getStatusAsString(content);
	}

	@Override
	public String buildInterrupted(SRunningBuild runningBuild, SFinishedBuild previousBuild,
			SortedMap<String,String> extraParameters, Map<String,String> templates) {
		SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(myManager.getServer(), runningBuild, previousBuild, BuildStateEnum.BUILD_INTERRUPTED, extraParameters, templates);
		return getStatusAsString(content);
	}

	@Override
	public String buildStarted(SRunningBuild runningBuild, SFinishedBuild previousBuild, 
			SortedMap<String,String> extraParameters, Map<String,String> templates) {
		SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(myManager.getServer(), runningBuild, previousBuild, BuildStateEnum.BUILD_STARTED, extraParameters, templates);
		return getStatusAsString(content);
	}

	/** Used by versions of TeamCity less than 7.0
	 */
	@Override
	public String responsibleChanged(SBuildType buildType,
			ResponsibilityInfo responsibilityInfoOld,
			ResponsibilityInfo responsibilityInfoNew, boolean isUserAction,
			SortedMap<String,String> extraParameters, Map<String,String> templates) {
		
		SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(myManager.getServer(), buildType, BuildStateEnum.RESPONSIBILITY_CHANGED, extraParameters, templates);
		String oldUser = "Nobody";
		String newUser = "Nobody";
		try {
			oldUser = responsibilityInfoOld.getResponsibleUser().getDescriptiveName();
		} catch (Exception e) {}
		try {
			newUser = responsibilityInfoNew.getResponsibleUser().getDescriptiveName();
		} catch (Exception e) {}
		content.setMessage("Build " + buildType.getFullName().toString()
				+ " has changed responsibility from " 
				+ oldUser
				+ " to "
				+ newUser
				+ " with comment '" 
				+ responsibilityInfoNew.getComment().toString().trim()
				+ "'"
			);
		content.setText(buildType.getFullName().toString()
				+ " changed responsibility from " 
				+ oldUser
				+ " to "
				+ newUser
				+ " with comment '" 
				+ responsibilityInfoNew.getComment().toString().trim()
				+ "'"
			);
		
		content.setComment(responsibilityInfoNew.getComment());
		return getStatusAsString(content);
	}

	/** Used by versions of TeamCity 7.0 and above
	 */
	@Override
	public String responsibleChanged(SBuildType buildType,
			ResponsibilityEntry responsibilityEntryOld,
			ResponsibilityEntry responsibilityEntryNew,
			SortedMap<String,String> extraParameters, Map<String,String> templates) {
		
		SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(myManager.getServer(), buildType, BuildStateEnum.RESPONSIBILITY_CHANGED, extraParameters, templates);
		String oldUser = "Nobody";
		String newUser = "Nobody";
		if (responsibilityEntryOld.getState() != ResponsibilityEntry.State.NONE) {
			  oldUser = responsibilityEntryOld.getResponsibleUser().getDescriptiveName();
		}
		if (responsibilityEntryNew.getState() != ResponsibilityEntry.State.NONE) {
			newUser = responsibilityEntryNew.getResponsibleUser().getDescriptiveName();
		}
		content.setMessage("Build " + buildType.getFullName().toString()
				+ " has changed responsibility from " 
				+ oldUser
				+ " to "
				+ newUser
				+ " with comment '" 
				+ responsibilityEntryNew.getComment()
				+ "'"
			);
		content.setText(buildType.getFullName().toString().toString().trim()
				+ " changed responsibility from " 
				+ oldUser
				+ " to "
				+ newUser
				+ " with comment '" 
				+ responsibilityEntryNew.getComment().toString().trim()
				+ "'"
			);
		
		content.setComment(responsibilityEntryNew.getComment());
		return getStatusAsString(content);
	}
	
	@Override
	public String responsibleChanged(SProject project,
			TestNameResponsibilityEntry oldTestNameResponsibilityEntry,
			TestNameResponsibilityEntry newTestNameResponsibilityEntry,
			boolean isUserAction, SortedMap<String,String> extraParameters, Map<String,String> templates) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String responsibleChanged(SProject project,
			Collection<TestName> testNames, ResponsibilityEntry entry,
			boolean isUserAction, SortedMap<String,String> extraParameters, Map<String,String> templates) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected abstract String getStatusAsString(SlackNotificationPayloadContent content);

	public abstract String getContentType();

	public abstract Integer getRank();

	public abstract void setRank(Integer rank);

	public abstract String getCharset();

	
}
