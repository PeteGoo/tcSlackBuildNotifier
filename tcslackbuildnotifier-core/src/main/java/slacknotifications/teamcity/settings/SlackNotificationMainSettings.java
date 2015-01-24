package slacknotifications.teamcity.settings;

import jetbrains.buildServer.serverSide.MainConfigProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import org.jdom.Element;
import slacknotifications.SlackNotificationProxyConfig;
import slacknotifications.teamcity.Loggers;

public class SlackNotificationMainSettings implements MainConfigProcessor {
	private static final String NAME = SlackNotificationMainSettings.class.getName();
	private SlackNotificationMainConfig slackNotificationMainConfig;
	private SBuildServer server;
    private ServerPaths serverPaths;

    public SlackNotificationMainSettings(SBuildServer server, ServerPaths serverPaths){
        this.serverPaths = serverPaths;
        Loggers.SERVER.debug(NAME + " :: Constructor called");
		this.server = server;
		slackNotificationMainConfig = new SlackNotificationMainConfig(serverPaths);

	}

    public void register(){
        Loggers.SERVER.debug(NAME + ":: Registering");
        server.registerExtension(MainConfigProcessor.class, "slacknotifications", this);
    }
    
	public String getProxyListasString(){
		return this.slackNotificationMainConfig.getProxyListasString();
	}
	
    @SuppressWarnings("unchecked")
    public void readFrom(Element rootElement)
    /* Is passed an Element by TC, and is expected to persist it to the settings object.
     * Old settings should be overwritten.
     */
    {
        if(slackNotificationMainConfig.getConfigFileExists()){
            // The MainConfigProcessor approach has been deprecated.
            // Instead we will use our own config file so we have better control over when it is persisted
            return;
        }
    	Loggers.SERVER.info("SlackNotificationMainSettings: re-reading main settings using old-style MainConfigProcessor. From now on we will use the slack/slack-config.xml file instead of main-config.xml");
    	Loggers.SERVER.debug(NAME + ":readFrom :: " + rootElement.toString());
    	SlackNotificationMainConfig tempConfig = new SlackNotificationMainConfig(serverPaths);
    	Element slackNotificationsElement = rootElement.getChild("slacknotifications");
        tempConfig.readConfigurationFromXmlElement(slackNotificationsElement);
        this.slackNotificationMainConfig = tempConfig;
        tempConfig.save();
    }

    public void writeTo(Element parentElement)
    /* Is passed an (probably empty) Element by TC, which is expected to be populated from the settings
     * in memory. 
     */
    {

    }
    
    public String getProxyForUrl(String url){
    	return this.slackNotificationMainConfig.getProxyConfigForUrl(url).getProxyHost();
    }

    public String getInfoText(){
    	return this.slackNotificationMainConfig.getSlackNotificationInfoText();
    }

    public String getInfoUrl(){
    	return this.slackNotificationMainConfig.getSlackNotificationInfoUrl();
    }

    public String getDefaultChannel() {
        return this.slackNotificationMainConfig.getDefaultChannel();
    }

    public String getTeamName() {
        return this.slackNotificationMainConfig.getTeamName();
    }

    public String getToken() {
        return this.slackNotificationMainConfig.getToken();
    }

    public String getIconUrl()
    {
        return this.slackNotificationMainConfig.getIconUrl();
    }

    public String getBotName()
    {
        return this.slackNotificationMainConfig.getBotName();
    }


    public Boolean getShowBuildAgent() {
        return this.slackNotificationMainConfig.getShowBuildAgent();
    }

    public Boolean getShowElapsedBuildTime() {
        return this.slackNotificationMainConfig.getShowElapsedBuildTime();
    }

    public boolean getShowCommits(){
        return this.slackNotificationMainConfig.getShowCommits();
    }
	
    public boolean getShowCommitters(){
        return this.slackNotificationMainConfig.getShowCommitters();
    }

    public Boolean getSlackNotificationShowFurtherReading(){
    	return this.slackNotificationMainConfig.getSlackNotificationShowFurtherReading();
    }
    
	public void dispose() {
		Loggers.SERVER.debug(NAME + ":dispose() called");
	}

	public SlackNotificationProxyConfig getProxyConfigForUrl(String url) {
		return this.slackNotificationMainConfig.getProxyConfigForUrl(url);	}


    public int getMaxCommitsToDisplay() {
        return this.slackNotificationMainConfig.getMaxCommitsToDisplay();
    }
}
