package slacknotifications.teamcity;

import org.apache.http.client.HttpClient;
import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationProxyConfig;

public interface SlackNotificationFactory {
	public abstract SlackNotification getSlackNotification();
	public abstract SlackNotification getSlackNotification(String channel, String proxy,
                                                           Integer proxyPort);
	public abstract SlackNotification getSlackNotification(String string);

    public abstract SlackNotification getSlackNotification(HttpClient httpClient, String string);

	public abstract SlackNotification getSlackNotification(String channel, String proxy,
                                                           String proxyPortString);
	public abstract SlackNotification getSlackNotification(String string, SlackNotificationProxyConfig pc);
}
