package slacknotifications.teamcity.settings;

import com.intellij.openapi.util.JDOMUtil;
import jetbrains.buildServer.configuration.ChangeListener;
import jetbrains.buildServer.configuration.FileWatcher;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.FileUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import slacknotifications.SlackNotificationProxyConfig;
import slacknotifications.teamcity.Loggers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SlackNotificationMainConfig implements ChangeListener {
	private final FileWatcher myChangeObserver;
	private final File myConfigDir;
	private final File myConfigFile;
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
    private Boolean showCommits = true;
    private Boolean showCommitters = true;
    private int maxCommitsToDisplay = 5;
	private boolean configFileExists;


	public SlackNotificationMainConfig(ServerPaths serverPaths) {
		noProxyUrls = new ArrayList<String>();
		noProxyPatterns = new ArrayList<Pattern>();
		singleHostPattern = Pattern.compile(SINGLE_HOST_REGEX); 
		hostnameOnlyPattern = Pattern.compile(HOSTNAME_ONLY_REGEX);

		this.myConfigDir = new File(serverPaths.getConfigDir(), "slack");
		this.myConfigFile = new File(this.myConfigDir, "slack-config.xml");
		reloadConfiguration();

		this.myChangeObserver = new FileWatcher(this.myConfigFile);
		this.myChangeObserver.setSleepingPeriod(10000L);
		this.myChangeObserver.registerListener(this);
		this.myChangeObserver.start();
	}

	private void reloadConfiguration() {
		Loggers.ACTIVITIES.info("Loading configuration file: " + this.myConfigFile.getAbsolutePath());

		FileUtil.copyResourceIfNotExists(getClass(), "/config_templates/slack-config.xml", new File(this.myConfigDir, "slack-config.xml"));

		Document document = parseFile(this.myConfigFile);
		if (document != null)
		{
			Element rootElement = document.getRootElement();
			readConfigurationFromXmlElement(rootElement);
		}
	}

	private Document parseFile(File configFile)
	{
		try
		{
			if (configFile.isFile()) {
				return JDOMUtil.loadDocument(configFile);
			}
		}
		catch (JDOMException e)
		{
			Loggers.ACTIVITIES.error("Failed to parse xml configuration file: " + configFile.getAbsolutePath(), e);
		}
		catch (IOException e)
		{
			Loggers.ACTIVITIES.error("I/O error occurred on attempt to parse xml configuration file: " + configFile.getAbsolutePath(), e);
		}
		return null;
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
		} else if (this.proxyShortNames == false && this.isUrlShortName(url)){
			/* If the hostname part of the URL does not contain a dot, and we have proxyShortNames unset
			 * then don't proxy the URL. 
			 */
			return false;
		} else {
			/* Else loop around the patterns matching the URL and don't 
			 * proxy the URL if we have a match.
			 */
	    	for(Iterator<Pattern> noProxyPattern = noProxyPatterns.iterator(); noProxyPattern.hasNext();)
	    	{	
	    		Pattern tempPat = noProxyPattern.next();
	    		if (tempPat.matcher(this.getHostNameFromUrl(url)).find()){
	    			return false;
	    		}
	    	}
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

	public synchronized void save()
	{
		this.myChangeObserver.runActionWithDisabledObserver(new Runnable()
		{
			public void run()
			{
				FileUtil.processXmlFile(SlackNotificationMainConfig.this.myConfigFile, new FileUtil.Processor() {
					public void process(Element rootElement) {
						rootElement.setAttribute("defaultChannel", SlackNotificationMainConfig.this.defaultChannel);
						rootElement.setAttribute("teamName", SlackNotificationMainConfig.this.teamName);
						rootElement.setAttribute("token", SlackNotificationMainConfig.this.token);
						rootElement.setAttribute("iconurl", SlackNotificationMainConfig.this.iconUrl);
						rootElement.setAttribute("botname", SlackNotificationMainConfig.this.botName);
						if(SlackNotificationMainConfig.this.showBuildAgent != null){
							rootElement.setAttribute("showBuildAgent", Boolean.toString(SlackNotificationMainConfig.this.showBuildAgent));
						}
						if(SlackNotificationMainConfig.this.showElapsedBuildTime != null) {
							rootElement.setAttribute("showElapsedBuildTime", Boolean.toString(SlackNotificationMainConfig.this.showElapsedBuildTime));
						}
						if(SlackNotificationMainConfig.this.showCommits != null) {
							rootElement.setAttribute("showCommits", Boolean.toString(SlackNotificationMainConfig.this.showCommits));
						}
						if(SlackNotificationMainConfig.this.showCommitters != null) {
							rootElement.setAttribute("showCommitters", Boolean.toString(SlackNotificationMainConfig.this.showCommitters));
						}
						rootElement.setAttribute("maxCommitsToDisplay", Integer.toString(SlackNotificationMainConfig.this.maxCommitsToDisplay));

						Element el = new Element("slackNotification");
						boolean hasProxyDetails = false;
						if(	  getProxyHost() != null && getProxyHost().length() > 0
								&& getProxyPort() != null && getProxyPort() > 0 )
						{
							el.addContent(getProxyAsElement());
							Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + "writeTo :: proxyHost " + getProxyHost().toString());
							Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + "writeTo :: proxyPort " + getProxyPort().toString());
							hasProxyDetails = true;
						}


						if(getInfoUrlAsElement() != null){
							el.addContent(getInfoUrlAsElement());
							Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + "writeTo :: infoText " + getSlackNotificationInfoText().toString());
							Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + "writeTo :: InfoUrl  " + getSlackNotificationInfoUrl().toString());
							Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + "writeTo :: show-reading  " + getSlackNotificationShowFurtherReading().toString());
							hasProxyDetails = true;
						}

						if(hasProxyDetails) {
							rootElement.addContent(el);
						}
					}
				});
			}
		});
	}

	@Override
	public void changeOccured(String s) {
		reloadConfiguration();
	}

	public boolean getConfigFileExists() {
		return configFileExists;
	}

	void readConfigurationFromXmlElement(Element slackNotificationsElement) {
        if(slackNotificationsElement != null){
            if(slackNotificationsElement.getAttribute("defaultChannel") != null)
            {
                setDefaultChannel(slackNotificationsElement.getAttributeValue("defaultChannel"));
            }
            if(slackNotificationsElement.getAttribute("teamName") != null)
            {
                setTeamName(slackNotificationsElement.getAttributeValue("teamName"));
            }
            if(slackNotificationsElement.getAttribute("token") != null)
            {
                setToken(slackNotificationsElement.getAttributeValue("token"));
            }
            if(slackNotificationsElement.getAttribute("iconurl") != null)
            {
                setIconUrl(slackNotificationsElement.getAttributeValue("iconurl"));
            }
            if(slackNotificationsElement.getAttribute("botname") != null)
            {
                setBotName(slackNotificationsElement.getAttributeValue("botname"));
            }
            if(slackNotificationsElement.getAttribute("showBuildAgent") != null)
            {
                setShowBuildAgent(Boolean.parseBoolean(slackNotificationsElement.getAttributeValue("showBuildAgent")));
            }
            if(slackNotificationsElement.getAttribute("showElapsedBuildTime") != null)
            {
                setShowElapsedBuildTime(Boolean.parseBoolean(slackNotificationsElement.getAttributeValue("showElapsedBuildTime")));
            }
            if(slackNotificationsElement.getAttribute("showCommits") != null)
            {
                setShowCommits(Boolean.parseBoolean(slackNotificationsElement.getAttributeValue("showCommits")));
            }
            if(slackNotificationsElement.getAttribute("showCommitters") != null)
            {
                setShowCommitters(Boolean.parseBoolean(slackNotificationsElement.getAttributeValue("showCommitters")));
            }
            if(slackNotificationsElement.getAttribute("maxCommitsToDisplay") != null)
            {
                setMaxCommitsToDisplay(Integer.parseInt(slackNotificationsElement.getAttributeValue("maxCommitsToDisplay")));
            }

            Element proxyElement = slackNotificationsElement.getChild("proxy");
            if(proxyElement != null)
            {
                if (proxyElement.getAttribute("proxyShortNames") != null){
                    setProxyShortNames(Boolean.parseBoolean(proxyElement.getAttributeValue("proxyShortNames")));
                }

                if (proxyElement.getAttribute("host") != null){
                    setProxyHost(proxyElement.getAttributeValue("host"));
                }

                if (proxyElement.getAttribute("port") != null){
                    setProxyPort(Integer.parseInt(proxyElement.getAttributeValue("port")));
                }

                if (proxyElement.getAttribute("username") != null){
                    setProxyUsername(proxyElement.getAttributeValue("username"));
                }

                if (proxyElement.getAttribute("password") != null){
                    setProxyPassword(proxyElement.getAttributeValue("password"));
                }

                List<Element> namedChildren = proxyElement.getChildren("noproxy");
                if(namedChildren.size() > 0) {
                    for(Iterator<Element> i = namedChildren.iterator(); i.hasNext();)
                    {
                        Element e = i.next();
                        String url = e.getAttributeValue("url");
                        addNoProxyUrl(url);
                        Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + ":readFrom :: noProxyUrl " + url);
                    }
                }
            }
        }
    }
}