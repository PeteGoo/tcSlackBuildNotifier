package slacknotifications;

import org.apache.http.NameValuePair;
import org.apache.http.auth.Credentials;
import slacknotifications.teamcity.BuildState;
import slacknotifications.teamcity.payload.content.PostMessageResponse;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface SlackNotification {

	void setProxy(SlackNotificationProxyConfig proxyConfig);

	void setProxy(String proxyHost, Integer proxyPort, Credentials credentials);

	void post() throws IOException;

	Integer getStatus();

	String getProxyHost();

	int getProxyPort();

	String getChannel();

	void setChannel(String channel);

    String getTeamName();

    void setTeamName(String teamName);

    String getToken();

    void setToken(String token);

    String getBotName();

    void setBotName(String botName);

    String getIconUrl();

    void setIconUrl(String iconUrl);

	String getParameterisedUrl();

	String parametersAsQueryString();

	void addParam(String key, String value);

	void addParams(List<NameValuePair> paramsList);

	String getParam(String key);

	void setFilename(String filename);

	String getFilename();

	String getContent();

	Boolean isEnabled();

	void setEnabled(Boolean enabled);

	void setEnabled(String enabled);

	Boolean isErrored();

	void setErrored(Boolean errored);

	String getErrorReason();

	void setErrorReason(String errorReason);

	BuildState getBuildStates();
	
	void setBuildStates(BuildState states);
	
	//public abstract Integer getEventListBitMask();
	//public abstract void setTriggerStateBitMask(Integer triggerStateBitMask);

	String getProxyUsername();

	void setProxyUsername(String proxyUsername);

	String getProxyPassword();

	void setProxyPassword(String proxyPassword);

	SlackNotificationPayloadContent getPayload();

	void setPayload(SlackNotificationPayloadContent payloadContent);

    PostMessageResponse getResponse();

    void setShowBuildAgent(Boolean showBuildAgent);

    void setShowElapsedBuildTime(Boolean showElapsedBuildTime);

    void setShowCommits(boolean showCommits);
	
    void setShowCommitters(boolean showCommitters);

    void setMaxCommitsToDisplay(int maxCommitsToDisplay);

    void setMentionChannelEnabled(boolean mentionChannelEnabled);

	void setMentionSlackUserEnabled(boolean mentionSlackUserEnabled);

    void setShowFailureReason(boolean showFailureReason);
}