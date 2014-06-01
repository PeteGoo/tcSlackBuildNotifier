package slacknotifications.teamcity.settings;

import java.util.Iterator;
import java.util.List;

import jetbrains.buildServer.serverSide.MainConfigProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;

import org.jdom.Element;

import slacknotifications.SlackNotificationProxyConfig;
import slacknotifications.teamcity.Loggers;

public class SlackNotificationMainSettings implements MainConfigProcessor {
	private static final String NAME = SlackNotificationMainSettings.class.getName();
	private SlackNotificationMainConfig slackNotificationMainConfig;
	private SBuildServer server;

    public SlackNotificationMainSettings(SBuildServer server){
		Loggers.SERVER.debug(NAME + " :: Constructor called");
		this.server = server;
		slackNotificationMainConfig = new SlackNotificationMainConfig();
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
    	Loggers.SERVER.info("SlackNotificationMainSettings: re-reading main settings");
    	Loggers.SERVER.debug(NAME + ":readFrom :: " + rootElement.toString());
    	SlackNotificationMainConfig tempConfig = new SlackNotificationMainConfig();
    	Element slackNotificationsElement = rootElement.getChild("slacknotifications");
    	if(slackNotificationsElement != null){
            if(slackNotificationsElement.getAttribute("defaultChannel") != null)
            {
                tempConfig.setDefaultChannel(slackNotificationsElement.getAttributeValue("defaultChannel"));
            }
            if(slackNotificationsElement.getAttribute("teamName") != null)
            {
                tempConfig.setTeamName(slackNotificationsElement.getAttributeValue("teamName"));
            }
            if(slackNotificationsElement.getAttribute("token") != null)
            {
                tempConfig.setToken(slackNotificationsElement.getAttributeValue("token"));
            }
            if(slackNotificationsElement.getAttribute("iconurl") != null)
            {
                tempConfig.setIconUrl(slackNotificationsElement.getAttributeValue("iconurl"));
            }
            if(slackNotificationsElement.getAttribute("botname") != null)
            {
                tempConfig.setBotName(slackNotificationsElement.getAttributeValue("botname"));
            }
			Element extraInfoElement = slackNotificationsElement.getChild("info");
	        if(extraInfoElement != null)
	        {
	        	if ((extraInfoElement.getAttribute("text") != null) 
	        	 && (extraInfoElement.getAttribute("url")  != null)){
	        		tempConfig.setSlackNotificationInfoText(extraInfoElement.getAttributeValue("text"));
	        		tempConfig.setSlackNotificationInfoUrl(extraInfoElement.getAttributeValue("url"));
	        		Loggers.SERVER.debug(NAME + ":readFrom :: info text " + tempConfig.getSlackNotificationInfoText());
	        		Loggers.SERVER.debug(NAME + ":readFrom :: info url  " + tempConfig.getSlackNotificationInfoUrl());
	        	}
	        	if (extraInfoElement.getAttribute("show-reading") != null){
	        		tempConfig.setSlackNotificationShowFurtherReading(Boolean.parseBoolean(extraInfoElement.getAttributeValue("show-reading")));
	        		Loggers.SERVER.debug(NAME + ":readFrom :: show reading " + tempConfig.getSlackNotificationShowFurtherReading().toString());
	        	}
	        }
    		Element proxyElement = slackNotificationsElement.getChild("proxy");
	        if(proxyElement != null)
	        {
	        	if (proxyElement.getAttribute("proxyShortNames") != null){
	        		tempConfig.setProxyShortNames(Boolean.parseBoolean(proxyElement.getAttributeValue("proxyShortNames")));
	        	}
	        	
	        	if (proxyElement.getAttribute("host") != null){
	        		tempConfig.setProxyHost(proxyElement.getAttributeValue("host"));
	        	}
	        	
	        	if (proxyElement.getAttribute("port") != null){
	        		tempConfig.setProxyPort(Integer.parseInt(proxyElement.getAttributeValue("port")));
	        	}
	
	        	if (proxyElement.getAttribute("username") != null){
	        		tempConfig.setProxyUsername(proxyElement.getAttributeValue("username"));
	        	}
	
	        	if (proxyElement.getAttribute("password") != null){
	        		tempConfig.setProxyPassword(proxyElement.getAttributeValue("password"));
	        	}
	
	    		List<Element> namedChildren = proxyElement.getChildren("noproxy");
	            if(namedChildren.size() > 0) {
					for(Iterator<Element> i = namedChildren.iterator(); i.hasNext();)
			        {
						Element e = i.next();
						String url = e.getAttributeValue("url");
						tempConfig.addNoProxyUrl(url);
						Loggers.SERVER.debug(NAME + ":readFrom :: noProxyUrl " + url);
			        }
		        }
	    	}
    	}
        this.slackNotificationMainConfig = tempConfig;
    }

    public void writeTo(Element parentElement)
    /* Is passed an (probably empty) Element by TC, which is expected to be populated from the settings
     * in memory. 
     */
    {
    	Loggers.SERVER.info("SlackNotificationMainSettings: re-writing main settings");
    	Loggers.SERVER.debug(NAME + ":writeTo :: " + parentElement.toString());
    	Element el = new Element("slackNotification");
        if(	  slackNotificationMainConfig != null
           && slackNotificationMainConfig.getProxyHost() != null && slackNotificationMainConfig.getProxyHost().length() > 0
           && slackNotificationMainConfig.getProxyPort() != null && slackNotificationMainConfig.getProxyPort() > 0 )
        {
        	el.addContent(slackNotificationMainConfig.getProxyAsElement());
			Loggers.SERVER.debug(NAME + "writeTo :: proxyHost " + slackNotificationMainConfig.getProxyHost().toString());
			Loggers.SERVER.debug(NAME + "writeTo :: proxyPort " + slackNotificationMainConfig.getProxyPort().toString());
        }
        
        
        if(slackNotificationMainConfig != null && slackNotificationMainConfig.getInfoUrlAsElement() != null){
        	el.addContent(slackNotificationMainConfig.getInfoUrlAsElement());
			Loggers.SERVER.debug(NAME + "writeTo :: infoText " + slackNotificationMainConfig.getSlackNotificationInfoText().toString());
			Loggers.SERVER.debug(NAME + "writeTo :: InfoUrl  " + slackNotificationMainConfig.getSlackNotificationInfoUrl().toString());
			Loggers.SERVER.debug(NAME + "writeTo :: show-reading  " + slackNotificationMainConfig.getSlackNotificationShowFurtherReading().toString());
        }
        
        parentElement.addContent(el);
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

    public Boolean getSlackNotificationShowFurtherReading(){
    	return this.slackNotificationMainConfig.getSlackNotificationShowFurtherReading();
    }
    
	public void dispose() {
		Loggers.SERVER.debug(NAME + ":dispose() called");
	}

	public SlackNotificationProxyConfig getProxyConfigForUrl(String url) {
		return this.slackNotificationMainConfig.getProxyConfigForUrl(url);	}


}
