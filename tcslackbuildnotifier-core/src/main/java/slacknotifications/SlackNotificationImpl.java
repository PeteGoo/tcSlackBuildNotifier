package slacknotifications;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import slacknotifications.teamcity.BuildState;


public class SlackNotificationImpl implements SlackNotification {
	private String proxyHost;
	private Integer proxyPort = 0;
	private String proxyUsername;
	private String proxyPassword;
	private String channel;
    private String teamName;
    private String token;
    private String username;
    private String iconUrl;
	private String content;
	private String contentType;
	private String charset;
	private String payload;
	private Integer resultCode;
	private HttpClient client;
	private String filename = "";
	private Boolean enabled = false;
	private Boolean errored = false;
	private String errorReason = "";
	private List<NameValuePair> params;
	private BuildState states;
	
/*	This is a bit mask of states that should trigger a SlackNotification.
 *  All ones (11111111) means that all states will trigger the slacknotifications
 *  We'll set that as the default, and then override if we get a more specific bit mask. */ 
	//private Integer EventListBitMask = BuildState.ALL_ENABLED;
	//private Integer EventListBitMask = Integer.parseInt("0",2);
	
	
	public SlackNotificationImpl(){
		this.client = new HttpClient();
		this.params = new ArrayList<NameValuePair>();
	}
	
	public SlackNotificationImpl(String channel){
		this.channel = channel;
		this.client = new HttpClient();
		this.params = new ArrayList<NameValuePair>();
	}
	
	public SlackNotificationImpl(String channel, String proxyHost, String proxyPort){
		this.channel = channel;
		this.client = new HttpClient();
		this.params = new ArrayList<NameValuePair>();
		if (proxyPort.length() != 0) {
			try {
				this.proxyPort = Integer.parseInt(proxyPort);
			} catch (NumberFormatException ex){
				ex.printStackTrace();
			}
		}
		this.setProxy(proxyHost, this.proxyPort);
	}
	
	public SlackNotificationImpl(String channel, String proxyHost, Integer proxyPort){
		this.channel = channel;
		this.client = new HttpClient();
		this.params = new ArrayList<NameValuePair>();
		this.setProxy(proxyHost, proxyPort);
	}
	
	public SlackNotificationImpl(String channel, SlackNotificationProxyConfig proxyConfig){
		this.channel = channel;
		this.client = new HttpClient();
		this.params = new ArrayList<NameValuePair>();
		setProxy(proxyConfig);
	}

	public void setProxy(SlackNotificationProxyConfig proxyConfig) {
		if ((proxyConfig != null) && (proxyConfig.getProxyHost() != null) && (proxyConfig.getProxyPort() != null)){
			this.setProxy(proxyConfig.getProxyHost(), proxyConfig.getProxyPort());
			if (proxyConfig.getCreds() != null){
				this.client.getState().setProxyCredentials(AuthScope.ANY, proxyConfig.getCreds());
			}
		}
	}
	
	public void setProxy(String proxyHost, Integer proxyPort) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		if (this.proxyHost.length() > 0 && !this.proxyPort.equals(0)) {
			this.client.getHostConfiguration().setProxy(this.proxyHost, this.proxyPort);
		}
	}

	public void setProxyUserAndPass(String username, String password){
		this.proxyUsername = username;
		this.proxyPassword = password;
		if (this.proxyUsername.length() > 0 && this.proxyPassword.length() > 0) {
			this.client.getState().setProxyCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		}
	}
	
	public void post() throws FileNotFoundException, IOException{
		if ((this.enabled) && (!this.errored)){
			PostMethod httppost = new PostMethod(String.format("https://slack.com/api/chat.postMessage?token=%s&channel=%s&text=%s&pretty=1", this.token, this.channel, ""));
			if (this.filename.length() > 0){
				File file = new File(this.filename);
			    httppost.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(file)));
			    httppost.setContentChunked(true);
			}
			if (   this.payload != null && this.payload.length() > 0 
				&& this.contentType != null && this.contentType.length() > 0){
				httppost.setRequestEntity(new StringRequestEntity(this.payload, this.contentType, this.charset));
			} else if (this.params.size() > 0){
				NameValuePair[] paramsArray = this.params.toArray(new NameValuePair[this.params.size()]);
				httppost.setRequestBody(paramsArray);
			}
		    try {
		        client.executeMethod(httppost);
		        this.resultCode = httppost.getStatusCode();
		        this.content = httppost.getResponseBodyAsString();
		    } finally {
		        httppost.releaseConnection();
		    }   
		}
	}

	public Integer getStatus(){
		return this.resultCode;
	}
	
	public String getProxyHost() {
		return proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

    public String getTeamName()
    {
        return teamName;
    }

    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String getParameterisedUrl(){
        //TODO: Implement different url logic
		return this.channel +  this.parametersAsQueryString();
	}

	public String parametersAsQueryString(){
		String s = "";
		for (Iterator<NameValuePair> i = this.params.iterator(); i.hasNext();){
			NameValuePair nv = i.next();
			s += "&" + nv.getName() + "=" + nv.getValue(); 
		}
		if (s.length() > 0 ){
			return "?" + s.substring(1);
		}
		return s;
	}
	
	public void addParam(String key, String value){
		this.params.add(new NameValuePair(key, value));
	}

	public void addParams(List<NameValuePair> paramsList){
		for (Iterator<NameValuePair> i = paramsList.iterator(); i.hasNext();){
			this.params.add(i.next()); 
		}		
	}
	
	public String getParam(String key){
		for (Iterator<NameValuePair> i = this.params.iterator(); i.hasNext();){
			NameValuePair nv = i.next();
			if (nv.getName().equals(key)){
				return nv.getValue();
			}
		}		
		return "";
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public String getContent() {
		return content;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnabled(String enabled){
		if (enabled.toLowerCase().equals("true")){
			this.enabled = true;
		} else {
			this.enabled = false;
		}
	}

	public Boolean isErrored() {
		return errored;
	}

	public void setErrored(Boolean errored) {
		this.errored = errored;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

//	public Integer getEventListBitMask() {
//		return EventListBitMask;
//	}
//
//	public void setTriggerStateBitMask(Integer triggerStateBitMask) {
//		EventListBitMask = triggerStateBitMask;
//	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payloadContent) {
		this.payload = payloadContent;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;

	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public BuildState getBuildStates() {
		return states;
	}

	@Override
	public void setBuildStates(BuildState states) {
		this.states = states;
	}
}
