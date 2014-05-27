package slacknotifications.teamcity;

import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationImpl;
import slacknotifications.SlackNotificationProxyConfig;

public class SlackNotificationFactoryImpl implements SlackNotificationFactory {
	public SlackNotification getSlackNotification(){
		return new SlackNotificationImpl();
	}

	public SlackNotification getSlackNotification(String url, String proxy, Integer proxyPort) {
		return new SlackNotificationImpl(url, proxy, proxyPort);
	}

	public SlackNotification getSlackNotification(String url) {
		return new SlackNotificationImpl(url);
	}

	public SlackNotification getSlackNotification(String url, String proxy, String proxyPort) {
		return new SlackNotificationImpl(url, proxy, proxyPort);
	}

	public SlackNotification getSlackNotification(String url, SlackNotificationProxyConfig proxyConfig) {
		return new SlackNotificationImpl(url, proxyConfig);
	}
}
