package slacknotifications.teamcity.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import slacknotifications.SlackNotificationProxyConfig;


public class SlackNotificationMainConfig {
	private String slacknotificationInfoUrl = null;
	private String slacknotificationInfoText = null;
	private Boolean slacknotificationShowFurtherReading = true;
	private Integer proxyPort = null;
	private String proxyHost = null;
	private String proxyUsername = null;
	private String proxyPassword = null;
    private String defaultChannel = null;
    private String teamName;
    private String token;
	private Boolean proxyShortNames = false;
	private List<String> noProxyUrls;
	private List<Pattern> noProxyPatterns;
	
	public final String SINGLE_HOST_REGEX = "^[^./~`'\"]+(?:/.*)?$";
	public final String HOSTNAME_ONLY_REGEX = "^([^/]+)(?:/.*)?$";
	private Pattern singleHostPattern, hostnameOnlyPattern ;
    private String iconUrl;
    private String botName;
    private Boolean showBuildAgent;
    private Boolean showElapsedBuildTime;
    private boolean showCommits = true;
    private boolean showCommitters = true;
    private int maxCommitsToDisplay = 5;


    public SlackNotificationMainConfig() {
		noProxyUrls = new ArrayList<String>();
		noProxyPatterns = new ArrayList<Pattern>();
		singleHostPattern = Pattern.compile(SINGLE_HOST_REGEX); 
		hostnameOnlyPattern = Pattern.compile(HOSTNAME_ONLY_REGEX);
	}

	public String getProxyListasString(){
    	return " host:" + this.proxyHost + " port: " + this.proxyPort;
	}
	
	private Pattern generatePatternFromURL(String noProxyUrl){
		if(this.stripProtocolFromUrl(noProxyUrl).startsWith(".")){
			return Pattern.compile("^.+" + Pattern.quote(noProxyUrl), Pattern.UNICODE_CASE);
		} else if (this.stripProtocolFromUrl(noProxyUrl).endsWith(".")){
			return Pattern.compile("^" + Pattern.quote(noProxyUrl) + ".+", Pattern.UNICODE_CASE);
		} else {
			return Pattern.compile("^" + Pattern.quote(noProxyUrl), Pattern.UNICODE_CASE);
		}
	}
	
	public void addNoProxyUrl(String noProxyUrl) {
		noProxyUrls.add(noProxyUrl);
		noProxyPatterns.add(generatePatternFromURL(noProxyUrl));
	}

	public SlackNotificationProxyConfig getProxyConfigForUrl(String url) {
		if(this.matchProxyForURL(url)){
			if (   this.proxyPassword != null && this.proxyPassword.length() > 0 
				&& this.proxyUsername != null && this.proxyUsername.length() > 0 ){
				return new SlackNotificationProxyConfig(this.proxyHost,this.proxyPort, this.proxyUsername, this.proxyPassword);
			} else {
				return new SlackNotificationProxyConfig(this.proxyHost,this.proxyPort);
			}
		} else {
			return null;
		}
	}

	public String stripProtocolFromUrl(String url){
		String tmpURL = url;
		if(tmpURL.length() > "https://".length() 
			&& tmpURL.substring(0,"https://".length()).equalsIgnoreCase("https://"))
		{
				tmpURL = tmpURL.substring("https://".length());
		} else if (tmpURL.length() > "http://".length() 
			&& tmpURL.substring(0,"http://".length()).equalsIgnoreCase("http://"))
		{
				tmpURL = tmpURL.substring("http://".length());
		}
		return tmpURL;
	}
	
	public String getHostNameFromUrl(String url){
		Matcher m = hostnameOnlyPattern.matcher(this.stripProtocolFromUrl(url));
		while (m.find()) {
		    String s = m.group(1);
		    return s;
		}
		return "";
	}
	
	public boolean isUrlShortName(String url){
		return singleHostPattern.matcher(stripProtocolFromUrl(url)).find();
	}
	
	public boolean matchProxyForURL(String url) {
		if ((this.proxyHost == null) 
				|| (this.proxyHost.length() == 0) 
				|| (this.proxyPort == null) 
				|| (!(this.proxyPort > 0))){
			/* If we don't have all the components of a proxy 
			 * configured, don't proxy the URL. 
			 */
			return false;
		}
		
		return true;
	}
	
	public Element getInfoUrlAsElement(){
		/*
			<info url="http://acme.com/" text="Using SlackNotifications in Acme Inc." />
		 */
		if (this.slacknotificationInfoUrl != null && this.slacknotificationInfoUrl.length() > 0){
			Element e = new Element("info");
			e.setAttribute("url", slacknotificationInfoUrl);
			if (this.slacknotificationInfoText != null && this.slacknotificationInfoText.length() > 0){
				e.setAttribute("text", slacknotificationInfoText);
			} else {
				e.setAttribute("text", slacknotificationInfoUrl);
			}
			e.setAttribute("show-reading", slacknotificationShowFurtherReading.toString());
			
			return e;
		}
		return null;
	}
	
	private Element getNoProxyAsElement(String noProxyUurl){
		Element e = new Element("noproxy");
		e.setAttribute("url", noProxyUurl);
		return e;
	}
	
	public Element getProxyAsElement(){
		/*
    		  <proxy host="myproxy.mycompany.com" port="8080" >
      			<noproxy url=".mycompany.com" />
      			<noproxy url="192.168.0." />
    		  </proxy>
		 */
		if (this.getProxyHost() == null || this.getProxyPort() == null){
			return null;
		}
		Element el = new Element("proxy");
		el.setAttribute("host", this.getProxyHost());
		el.setAttribute("port", String.valueOf(this.getProxyPort()));
		if (   this.proxyPassword != null && this.proxyPassword.length() > 0 
			&& this.proxyUsername != null && this.proxyUsername.length() > 0 )
		{
			el.setAttribute("username", this.getProxyUsername());
			el.setAttribute("password", this.getProxyPassword());
			
		}

		if (this.noProxyUrls.size() > 0){
			for (Iterator<String> i = this.noProxyUrls.iterator(); i.hasNext();){
				el.addContent(this.getNoProxyAsElement(i.next()));
			}
		}
		return el;
	}

    public String getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(String defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

    public String getBotName()
    {
        return botName;
    }

    public void setBotName(String botName)
    {
        this.botName = botName;
    }
	
	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

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

	public Boolean isProxyShortNames() {
		return proxyShortNames;
	}

	public void setProxyShortNames(Boolean proxyShortNames) {
		this.proxyShortNames = proxyShortNames;
	}

	public List<String> getNoProxyUrls() {
		return noProxyUrls;
	}

	public void setNoProxyUrls(List<String> noProxyUrls) {
		this.noProxyUrls = noProxyUrls;
	}

	public String getSlackNotificationInfoUrl() {
		return slacknotificationInfoUrl;
	}

	public String getSlackNotificationInfoText() {
		return slacknotificationInfoText;
	}

	public void setSlackNotificationInfoUrl(String slacknotificationInfoUrl) {
		this.slacknotificationInfoUrl = slacknotificationInfoUrl;
	}

	public void setSlackNotificationInfoText(String slacknotificationInfoText) {
		this.slacknotificationInfoText = slacknotificationInfoText;
	}

	public void setSlackNotificationShowFurtherReading(Boolean slacknotificationShowFurtherReading) {
		this.slacknotificationShowFurtherReading = slacknotificationShowFurtherReading;
	}

	public Boolean getSlackNotificationShowFurtherReading() {
		return slacknotificationShowFurtherReading;
	}


    public Boolean getShowBuildAgent() {
        return showBuildAgent;
    }

    public void setShowBuildAgent(Boolean showBuildAgent) {
        this.showBuildAgent = showBuildAgent;
    }

    public Boolean getShowElapsedBuildTime() {
        return showElapsedBuildTime;
    }

    public void setShowElapsedBuildTime(Boolean showElapsedBuildTime) {
        this.showElapsedBuildTime = showElapsedBuildTime;
    }

    public boolean getShowCommits() {
        return showCommits;
    }
	
    public boolean isShowCommits() {
        return showCommits;
    }

    public void setShowCommits(boolean showCommits) {
        this.showCommits = showCommits;
    }
	
    public boolean getShowCommitters() {
        return showCommitters;
    }
	
    public boolean isShowCommitters() {
        return showCommitters;
    }

    public void setShowCommitters(boolean showCommitters) {
        this.showCommitters = showCommitters;
    }

    public int getMaxCommitsToDisplay() {
        return maxCommitsToDisplay;
    }

    public void setMaxCommitsToDisplay(int maxCommitsToDisplay) {
        this.maxCommitsToDisplay = maxCommitsToDisplay;
    }
}