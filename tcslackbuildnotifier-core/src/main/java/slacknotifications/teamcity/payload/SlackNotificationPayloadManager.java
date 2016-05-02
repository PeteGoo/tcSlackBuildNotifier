
package slacknotifications.teamcity.payload;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.tests.TestName;
import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.Loggers;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;

import java.util.Collection;

public class SlackNotificationPayloadManager {

    SBuildServer server;

    public SlackNotificationPayloadManager(SBuildServer server){
        this.server = server;
        Loggers.SERVER.info("SlackNotificationPayloadManager :: Starting");
    }


    public SlackNotificationPayloadContent beforeBuildFinish(SRunningBuild runningBuild, SFinishedBuild previousBuild) {
        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BEFORE_BUILD_FINISHED);
        return content;
    }


    public SlackNotificationPayloadContent buildFinished(SRunningBuild runningBuild, SFinishedBuild previousBuild) {
        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BUILD_FINISHED);
        return content;
    }

    public SlackNotificationPayloadContent buildInterrupted(SRunningBuild runningBuild, SFinishedBuild previousBuild) {
        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BUILD_INTERRUPTED);
        return content;
    }

    public SlackNotificationPayloadContent buildStarted(SRunningBuild runningBuild, SFinishedBuild previousBuild) {
        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BUILD_STARTED);
        return content;
    }

    /** Used by versions of TeamCity less than 7.0
     */
    public SlackNotificationPayloadContent responsibleChanged(SBuildType buildType,
                                     ResponsibilityInfo responsibilityInfoOld,
                                     ResponsibilityInfo responsibilityInfoNew, boolean isUserAction) {

        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, buildType, BuildStateEnum.RESPONSIBILITY_CHANGED);
        String oldUser = "Nobody";
        String newUser = "Nobody";
        try {
            oldUser = responsibilityInfoOld.getResponsibleUser().getDescriptiveName();
        } catch (Exception e) {}
        try {
            newUser = responsibilityInfoNew.getResponsibleUser().getDescriptiveName();
        } catch (Exception e) {}

        content.setText(buildType.getFullName() 
                        + " changed responsibility from "
                        + oldUser
                        + " to "
                        + newUser
                        + " with comment '"
                        + responsibilityInfoNew.getComment().trim()
                        + "'"
        );

        return content;
    }

    /** Used by versions of TeamCity 7.0 and above
     */
    public SlackNotificationPayloadContent responsibleChanged(SBuildType buildType,
                                     ResponsibilityEntry responsibilityEntryOld,
                                     ResponsibilityEntry responsibilityEntryNew) {

        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, buildType, BuildStateEnum.RESPONSIBILITY_CHANGED);
        String oldUser = "Nobody";
        String newUser = "Nobody";
        if (responsibilityEntryOld.getState() != ResponsibilityEntry.State.NONE) {
            oldUser = responsibilityEntryOld.getResponsibleUser().getDescriptiveName();
        }
        if (responsibilityEntryNew.getState() != ResponsibilityEntry.State.NONE) {
            newUser = responsibilityEntryNew.getResponsibleUser().getDescriptiveName();
        }


        content.setText(buildType.getFullName().trim()
                        + " changed responsibility from "
                        + oldUser
                        + " to "
                        + newUser
                        + " with comment '"
                        + responsibilityEntryNew.getComment().trim()
                        + "'"
        );

        return content;
    }

    public SlackNotificationPayloadContent responsibleChanged(SProject project,
                                     TestNameResponsibilityEntry oldTestNameResponsibilityEntry,
                                     TestNameResponsibilityEntry newTestNameResponsibilityEntry,
                                     boolean isUserAction) {
        // TODO Auto-generated method stub
        return null;
    }

    public SlackNotificationPayloadContent responsibleChanged(SProject project,
                                     Collection<TestName> testNames, ResponsibilityEntry entry,
                                     boolean isUserAction) {
        // TODO Auto-generated method stub
        return null;
    }

/*
	HashMap<String, SlackNotificationPayload> formats = new HashMap<String,SlackNotificationPayload>();
	Comparator<SlackNotificationPayload> rankComparator = new SlackNotificationPayloadRankingComparator();
	List<SlackNotificationPayload> orderedFormatCollection = new ArrayList<SlackNotificationPayload>();
	SBuildServer server;
	
	public SlackNotificationPayloadManager(SBuildServer server){
		this.server = server;
		Loggers.SERVER.info("SlackNotificationPayloadManager :: Starting");
	}
	
	public void registerPayloadFormat(SlackNotificationPayload payloadFormat){
		Loggers.SERVER.info(this.getClass().getSimpleName() + " :: Registering payload " 
				+ payloadFormat.getFormatShortName() 
				+ " with rank of " + payloadFormat.getRank());
		formats.put(payloadFormat.getFormatShortName(),payloadFormat);
		this.orderedFormatCollection.add(payloadFormat);
		
		Collections.sort(this.orderedFormatCollection, rankComparator);
		Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Payloads list is " + this.orderedFormatCollection.size() + " items long. Payloads are ranked in the following order..");
		for (SlackNotificationPayload pl : this.orderedFormatCollection){
			Loggers.SERVER.debug(this.getClass().getSimpleName() + " :: Payload Name: " + pl.getFormatShortName() + " Rank: " + pl.getRank());
		}
	}

	public SlackNotificationPayload getFormat(String formatShortname){
		if (formats.containsKey(formatShortname)){
			return formats.get(formatShortname);
		}
		return null;
	}
	
	public Boolean isRegisteredFormat(String format){
		return formats.containsKey(format);
	}
	
	public Set<String> getRegisteredFormats(){
		return formats.keySet();
	}
	
	public Collection<SlackNotificationPayload> getRegisteredFormatsAsCollection(){
		return orderedFormatCollection;
	}

	public SBuildServer getServer() {
		return server;
	}	
*/
	
}
