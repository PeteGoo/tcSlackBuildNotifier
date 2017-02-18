package slacknotifications.teamcity;

import org.apache.http.HttpStatus;
import slacknotifications.SlackNotification;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Lokum on 22.06.2016.
 */
public final class NotificationUtility {

    public void doPost(SlackNotification notification){
        try {
            if (notification.isEnabled() && (
                    notification.getFilterBranchName().equalsIgnoreCase(notification.getBranchDisplayName()) ||
                    notification.getFilterBranchName().equalsIgnoreCase("<default>") && notification.getPayload() != null && notification.getPayload().getBranchIsDefault()
            )) {
                notification.post();
                if (notification.getResponse() != null && !notification.getResponse().getOk()) {
                    Loggers.SERVER.error(this.getClass().getSimpleName() + " :: SlackNotification failed : "
                            + notification.getChannel()
                            + " returned error " + notification.getResponse().getError()
                            + " " + notification.getErrorReason());
                }
                else {
                    Loggers.SERVER.info(this.getClass().getSimpleName() + " :: SlackNotification delivered : "
                            + notification.getChannel()
                            + " returned " + notification.getStatus()
                            + " " + notification.getErrorReason());
                }
                Loggers.SERVER.debug(this.getClass().getSimpleName() + ":doPost :: content dump: " + notification.getPayload());
                if (notification.isErrored()){
                    Loggers.SERVER.error(notification.getErrorReason());
                }
                if ((notification.getStatus() == null || notification.getStatus() > HttpStatus.SC_OK))
                    Loggers.ACTIVITIES.warn("SlackNotificationListener :: " + notification.getParam("projectId") + " SlackNotification (url: " + notification.getChannel() + " proxy: " + notification.getProxyHost() + ":" + notification.getProxyPort()+") returned HTTP status " + notification.getStatus());

            } else {
                Loggers.SERVER.info("SlackNotification NOT triggered: "
                        + notification.getParam("buildStatus") + " " + notification.getChannel() + " isEnabled: " + notification.isEnabled() + " filterBranchName: " + notification.getFilterBranchName() + " branchDisplayName: " + notification.getBranchDisplayName());
            }
        } catch (FileNotFoundException e) {
            Loggers.SERVER.warn(this.getClass().getName() + ":doPost :: "
                    + "A FileNotFoundException occurred while attempting to execute SlackNotification (" + notification.getChannel() + "). See the following stacktrace");
            Loggers.SERVER.warn(e);
        } catch (IOException e) {
            Loggers.SERVER.warn(this.getClass().getName() + ":doPost :: "
                    + "An IOException occurred while attempting to execute SlackNotification (" + notification.getChannel() + "). See the following stacktrace");
            Loggers.SERVER.warn(e);
        }
    }
}
