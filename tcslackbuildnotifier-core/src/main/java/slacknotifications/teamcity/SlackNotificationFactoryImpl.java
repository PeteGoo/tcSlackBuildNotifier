package slacknotifications.teamcity;


import org.apache.http.client.HttpClient;
import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationImpl;
import slacknotifications.SlackNotificationProxyConfig;

public class SlackNotificationFactoryImpl implements SlackNotificationFactory {
	public SlackNotification getSlackNotification(){
		return new SlackNotificationImpl();
	}

	public SlackNotification getSlackNotification(String channel, String proxy, Integer proxyPort) {
		return new SlackNotificationImpl(channel, proxy, proxyPort);
	}

	public SlackNotification getSlackNotification(String channel) {
		return new SlackNotificationImpl(channel);
	}

    @Override
    public SlackNotification getSlackNotification(HttpClient httpClient, String channel) {
        return new SlackNotificationImpl(httpClient, channel);
    }

    public SlackNotification getSlackNotification(String channel, String proxy, String proxyPort) {
		return new SlackNotificationImpl(channel, proxy, proxyPort);
	}

	public SlackNotification getSlackNotification(String channel, SlackNotificationProxyConfig proxyConfig) {
		return new SlackNotificationImpl(channel, proxyConfig);
	}
}
