package slacknotifications.teamcity;

import org.apache.http.client.HttpClient;
import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationProxyConfig;

public interface SlackNotificationFactory {
    SlackNotification getSlackNotification();

    SlackNotification getSlackNotification(String channel, String proxy,
                                           Integer proxyPort);

    SlackNotification getSlackNotification(String string);

    SlackNotification getSlackNotification(HttpClient httpClient, String string);
}
