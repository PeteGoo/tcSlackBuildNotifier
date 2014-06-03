
package slacknotifications.teamcity.payload;

import java.util.*;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.tests.TestName;
import slacknotifications.SlackNotification;
import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.Loggers;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;

public class SlackNotificationPayloadManager {

    SBuildServer server;

    public SlackNotificationPayloadManager(SBuildServer server){
        this.server = server;
        Loggers.SERVER.info("SlackNotificationPayloadManager :: Starting");
    }

    @Deprecated
    public String buildChangedStatus(SRunningBuild runningBuild, SFinishedBuild previousBuild,
                                     Status oldStatus, Status newStatus,
                                     SortedMap<String,String> extraParameters, Map<String,String> templates) {
        return "";
    }

    public SlackNotificationPayloadContent beforeBuildFinish(SRunningBuild runningBuild, SFinishedBuild previousBuild,
                                    SortedMap<String,String> extraParameters, Map<String,String> templates) {
        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BEFORE_BUILD_FINISHED, extraParameters, templates);
        return content;
    }


    public SlackNotificationPayloadContent buildFinished(SRunningBuild runningBuild, SFinishedBuild previousBuild,
                                SortedMap<String,String> extraParameters, Map<String,String> templates) {
        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BUILD_FINISHED, extraParameters, templates);
        return content;
    }

    public SlackNotificationPayloadContent buildInterrupted(SRunningBuild runningBuild, SFinishedBuild previousBuild,
                                   SortedMap<String,String> extraParameters, Map<String,String> templates) {
        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BUILD_INTERRUPTED, extraParameters, templates);
        return content;
    }

    public SlackNotificationPayloadContent buildStarted(SRunningBuild runningBuild, SFinishedBuild previousBuild,
                               SortedMap<String,String> extraParameters, Map<String,String> templates) {
        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, runningBuild, previousBuild, BuildStateEnum.BUILD_STARTED, extraParameters, templates);
        return content;
    }

    /** Used by versions of TeamCity less than 7.0
     */
    public SlackNotificationPayloadContent responsibleChanged(SBuildType buildType,
                                     ResponsibilityInfo responsibilityInfoOld,
                                     ResponsibilityInfo responsibilityInfoNew, boolean isUserAction,
                                     SortedMap<String,String> extraParameters, Map<String,String> templates) {

        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, buildType, BuildStateEnum.RESPONSIBILITY_CHANGED, extraParameters, templates);
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
        return content;
    }

    /** Used by versions of TeamCity 7.0 and above
     */
    public SlackNotificationPayloadContent responsibleChanged(SBuildType buildType,
                                     ResponsibilityEntry responsibilityEntryOld,
                                     ResponsibilityEntry responsibilityEntryNew,
                                     SortedMap<String,String> extraParameters, Map<String,String> templates) {

        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent(server, buildType, BuildStateEnum.RESPONSIBILITY_CHANGED, extraParameters, templates);
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
        return content;
    }

    public SlackNotificationPayloadContent responsibleChanged(SProject project,
                                     TestNameResponsibilityEntry oldTestNameResponsibilityEntry,
                                     TestNameResponsibilityEntry newTestNameResponsibilityEntry,
                                     boolean isUserAction, SortedMap<String,String> extraParameters, Map<String,String> templates) {
        // TODO Auto-generated method stub
        return null;
    }

    public SlackNotificationPayloadContent responsibleChanged(SProject project,
                                     Collection<TestName> testNames, ResponsibilityEntry entry,
                                     boolean isUserAction, SortedMap<String,String> extraParameters, Map<String,String> templates) {
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
