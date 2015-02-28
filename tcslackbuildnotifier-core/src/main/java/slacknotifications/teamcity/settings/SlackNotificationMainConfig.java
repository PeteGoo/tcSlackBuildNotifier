package slacknotifications.teamcity.settings;

import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.text.StringUtil;
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

public class SlackNotificationMainConfig implements ChangeListener {
    public static final String DEFAULT_BOTNAME = "TeamCity";
    public static final String DEFAULT_ICONURL = "https://raw.githubusercontent.com/PeteGoo/tcSlackBuildNotifier/master/docs/TeamCity32.png";


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
    private boolean enabled = true;
	
	public final String SINGLE_HOST_REGEX = "^[^./~`'\"]+(?:/.*)?$";
	public final String HOSTNAME_ONLY_REGEX = "^([^/]+)(?:/.*)?$";
    private SlackNotificationContentConfig content;
    private boolean configFileExists;




	public SlackNotificationMainConfig(ServerPaths serverPaths) {
        this.content = new SlackNotificationContentConfig();
		this.myConfigDir = new File(serverPaths.getConfigDir(), "slack");
		this.myConfigFile = new File(this.myConfigDir, "slack-config.xml");
        configFileExists = this.myConfigFile.exists();
		reloadConfiguration();

		this.myChangeObserver = new FileWatcher(this.myConfigFile);
		this.myChangeObserver.setSleepingPeriod(10000L);
		this.myChangeObserver.registerListener(this);
		this.myChangeObserver.start();
	}

    public void refresh(){
        reloadConfiguration();
    }

	private void reloadConfiguration() {
		Loggers.ACTIVITIES.info("Loading configuration file: " + this.myConfigFile.getAbsolutePath());

		myConfigDir.mkdirs();
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

	public String getProxySettingsAsString(){
    	return " host:" + this.proxyHost + " port: " + this.proxyPort;
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

	public String getSlackNotificationInfoUrl() {
		return slacknotificationInfoUrl;
	}

	public String getSlackNotificationInfoText() {
		return slacknotificationInfoText;
	}

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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


	public synchronized void save()
	{
		this.myChangeObserver.runActionWithDisabledObserver(new Runnable()
		{
			public void run()
			{
				FileUtil.processXmlFile(SlackNotificationMainConfig.this.myConfigFile, new FileUtil.Processor() {
					public void process(Element rootElement) {
                        rootElement.setAttribute("enabled", Boolean.toString(SlackNotificationMainConfig.this.enabled));
                        rootElement.setAttribute("teamName", emptyIfNull(SlackNotificationMainConfig.this.teamName));
						rootElement.setAttribute("defaultChannel", emptyIfNull(SlackNotificationMainConfig.this.defaultChannel));
                        rootElement.setAttribute("teamName", emptyIfNull(SlackNotificationMainConfig.this.teamName));
						rootElement.setAttribute("token", emptyIfNull(SlackNotificationMainConfig.this.token));
						rootElement.setAttribute("iconurl", emptyIfNull(SlackNotificationMainConfig.this.content.getIconUrl()));
						rootElement.setAttribute("botname", emptyIfNull(SlackNotificationMainConfig.this.content.getBotName()));
						if(SlackNotificationMainConfig.this.content.getShowBuildAgent() != null){
							rootElement.setAttribute("showBuildAgent", Boolean.toString(SlackNotificationMainConfig.this.content.getShowBuildAgent()));
						}
						if(SlackNotificationMainConfig.this.content.getShowElapsedBuildTime() != null) {
							rootElement.setAttribute("showElapsedBuildTime", Boolean.toString(SlackNotificationMainConfig.this.content.getShowElapsedBuildTime()));
						}
						if(SlackNotificationMainConfig.this.content.getShowCommits() != null) {
							rootElement.setAttribute("showCommits", Boolean.toString(SlackNotificationMainConfig.this.content.getShowCommits()));
						}
						if(SlackNotificationMainConfig.this.content.getShowCommitters() != null) {
							rootElement.setAttribute("showCommitters", Boolean.toString(SlackNotificationMainConfig.this.content.getShowCommitters()));
						}
						rootElement.setAttribute("maxCommitsToDisplay", Integer.toString(SlackNotificationMainConfig.this.content.getMaxCommitsToDisplay()));

                        rootElement.removeChildren("proxy");
                        rootElement.removeChildren("info");

						if(getProxyHost() != null && getProxyHost().length() > 0
								&& getProxyPort() != null && getProxyPort() > 0 )
						{
							rootElement.addContent(getProxyAsElement());
							Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + "writeTo :: proxyHost " + getProxyHost().toString());
							Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + "writeTo :: proxyPort " + getProxyPort().toString());
						}

						if(getInfoUrlAsElement() != null){
                            rootElement.addContent(getInfoUrlAsElement());
							Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + "writeTo :: infoText " + getSlackNotificationInfoText().toString());
							Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + "writeTo :: InfoUrl  " + getSlackNotificationInfoUrl().toString());
							Loggers.SERVER.debug(SlackNotificationMainConfig.class.getName() + "writeTo :: show-reading  " + getSlackNotificationShowFurtherReading().toString());
						}
					}
				});
			}
		});
	}

    private String emptyIfNull(String str){
        return str == null ? "" : str;
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
            content.setEnabled(true);
            if(slackNotificationsElement.getAttribute("enabled") != null)
            {
                setEnabled(Boolean.parseBoolean(slackNotificationsElement.getAttributeValue("enabled")));
            }
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
                content.setIconUrl(slackNotificationsElement.getAttributeValue("iconurl"));
            }
            if(slackNotificationsElement.getAttribute("botname") != null)
            {
                content.setBotName(slackNotificationsElement.getAttributeValue("botname"));
            }
            if(slackNotificationsElement.getAttribute("showBuildAgent") != null)
            {
                content.setShowBuildAgent(Boolean.parseBoolean(slackNotificationsElement.getAttributeValue("showBuildAgent")));
            }
            if(slackNotificationsElement.getAttribute("showElapsedBuildTime") != null)
            {
                content.setShowElapsedBuildTime(Boolean.parseBoolean(slackNotificationsElement.getAttributeValue("showElapsedBuildTime")));
            }
            if(slackNotificationsElement.getAttribute("showCommits") != null)
            {
                content.setShowCommits(Boolean.parseBoolean(slackNotificationsElement.getAttributeValue("showCommits")));
            }
            if(slackNotificationsElement.getAttribute("showCommitters") != null)
            {
                content.setShowCommitters(Boolean.parseBoolean(slackNotificationsElement.getAttributeValue("showCommitters")));
            }
            if(slackNotificationsElement.getAttribute("maxCommitsToDisplay") != null)
            {
                content.setMaxCommitsToDisplay(Integer.parseInt(slackNotificationsElement.getAttributeValue("maxCommitsToDisplay")));
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
            }
            else {
                setProxyHost(null);
                setProxyPort(null);
                setProxyUsername(null);
                setProxyPassword(null);
            }
        }
    }

    public SlackNotificationProxyConfig getProxyConfig() {
        return new SlackNotificationProxyConfig(proxyHost, proxyPort, proxyUsername, proxyPassword);
    }

    public SlackNotificationContentConfig getContent() {
        return content;
    }
}