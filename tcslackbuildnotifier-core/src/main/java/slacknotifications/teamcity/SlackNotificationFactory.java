package slacknotifications.teamcity;

import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationProxyConfig;

public interface SlackNotificationFactory {
	public abstract SlackNotification getSlackNotification();
	public abstract SlackNotification getSlackNotification(String url, String proxy,
                                                           Integer proxyPort);
	public abstract SlackNotification getSlackNotification(String string);
	public abstract SlackNotification getSlackNotification(String url, String proxy,
                                                           String proxyPortString);
	public abstract SlackNotification getSlackNotification(String string, SlackNotificationProxyConfig pc);
}
