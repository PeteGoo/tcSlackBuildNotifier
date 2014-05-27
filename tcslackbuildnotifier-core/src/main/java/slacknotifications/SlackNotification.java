package slacknotifications;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;

import slacknotifications.teamcity.BuildState;

public interface SlackNotification {

	public abstract void setProxy(SlackNotificationProxyConfig proxyConfig);

	public abstract void setProxy(String proxyHost, Integer proxyPort);

	public abstract void setProxyUserAndPass(String username, String password);

	public abstract void post() throws FileNotFoundException, IOException;

	public abstract Integer getStatus();

	public abstract String getProxyHost();

	public abstract int getProxyPort();

	public abstract String getChannel();

	public abstract void setChannel(String channel);

	public abstract String getParameterisedUrl();

	public abstract String parametersAsQueryString();

	public abstract void addParam(String key, String value);

	public abstract void addParams(List<NameValuePair> paramsList);

	public abstract String getParam(String key);

	public abstract void setFilename(String filename);

	public abstract String getFilename();

	public abstract String getContent();

	public abstract Boolean isEnabled();

	public abstract void setEnabled(Boolean enabled);

	public abstract void setEnabled(String enabled);

	public abstract Boolean isErrored();

	public abstract void setErrored(Boolean errored);

	public abstract String getErrorReason();

	public abstract void setErrorReason(String errorReason);

	public abstract BuildState getBuildStates();
	
	public abstract void setBuildStates(BuildState states);
	
	//public abstract Integer getEventListBitMask();
	//public abstract void setTriggerStateBitMask(Integer triggerStateBitMask);

	public abstract String getProxyUsername();

	public abstract void setProxyUsername(String proxyUsername);

	public abstract String getProxyPassword();

	public abstract void setProxyPassword(String proxyPassword);

	public abstract String getPayload();

	public abstract void setPayload(String payloadContent);

	public abstract void setContentType(String contentType);

	public abstract void setCharset(String charset);

}