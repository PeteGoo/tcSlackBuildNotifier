package slacknotifications.teamcity.settings;

import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.settings.ProjectSettings;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import org.jdom.Element;
import slacknotifications.teamcity.BuildState;
import slacknotifications.teamcity.Loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;


public class SlackNotificationProjectSettings implements ProjectSettings {
	private static final String ENABLED = "enabled";
	private static final String NAME = SlackNotificationProjectSettings.class.getName();
	ProjectSettingsManager psm;
	ProjectSettings ps;
	private Boolean slackNotificationsEnabled = true;
	private Boolean updateSuccess = false;
	private String updateMessage = "";
	private CopyOnWriteArrayList<SlackNotificationConfig> slackNotificationsConfigs;
	
	public SlackNotificationProjectSettings(){
		slackNotificationsConfigs = new CopyOnWriteArrayList<SlackNotificationConfig>();
	}

    @SuppressWarnings("unchecked")
	public void readFrom(Element rootElement)
    /* Is passed an Element by TC, and is expected to load it into the in memory settings object.
     * Old settings should be overwritten.
     */
    {
    	Loggers.SERVER.debug("readFrom :: " + rootElement.toString());
    	CopyOnWriteArrayList<SlackNotificationConfig> configs = new CopyOnWriteArrayList<SlackNotificationConfig>();
    	
    	if (rootElement.getAttribute(ENABLED) != null){
    		this.slackNotificationsEnabled = Boolean.parseBoolean(rootElement.getAttributeValue(ENABLED));
    	}
    	
		List<Element> namedChildren = rootElement.getChildren("slackNotification");
        if(namedChildren.isEmpty())
        {
            this.slackNotificationsConfigs = null;
        } else {
			for(Element e :  namedChildren)
	        {
				SlackNotificationConfig whConfig = new SlackNotificationConfig(e);
				Loggers.SERVER.debug(e.toString());
				configs.add(whConfig);
				Loggers.SERVER.debug(NAME + ":readFrom :: channel " + whConfig.getChannel());
				Loggers.SERVER.debug(NAME + ":readFrom :: enabled " + String.valueOf(whConfig.getEnabled()));
	        }
			this.slackNotificationsConfigs = configs;
    	}
    }

    public void writeTo(Element parentElement)
    /* Is passed an (probably empty) Element by TC, which is expected to be populated from the settings
     * in memory. 
     */
    {
    	Loggers.SERVER.debug(NAME + ":writeTo :: " + parentElement.toString());
    	parentElement.setAttribute(ENABLED, String.valueOf(this.slackNotificationsEnabled));
        if(slackNotificationsConfigs != null)
        {
            for(SlackNotificationConfig whc : slackNotificationsConfigs){
            	Element el = whc.getAsElement();
            	Loggers.SERVER.debug(el.toString());
                parentElement.addContent(el);
				Loggers.SERVER.debug(NAME + ":writeTo :: channel " + whc.getChannel());
				Loggers.SERVER.debug(NAME + ":writeTo :: enabled " + String.valueOf(whc.getEnabled()));
            }
        }
    }
    
    public List<SlackNotificationConfig> getSlackNotificationsAsList(){
    	return this.slackNotificationsConfigs;
    }    
    
    public List<SlackNotificationConfig> getProjectSlackNotificationsAsList(){
    	List<SlackNotificationConfig> projHooks = new ArrayList<SlackNotificationConfig>();
    	for (SlackNotificationConfig config : getSlackNotificationsAsList()){
    		if (config.isEnabledForAllBuildsInProject()){
    			projHooks.add(config);
    		}
    	}
    	return projHooks;
    }    
    
    public List<SlackNotificationConfig> getBuildSlackNotificationsAsList(SBuildType buildType){
    	List<SlackNotificationConfig> buildHooks = new ArrayList<SlackNotificationConfig>();
    	for (SlackNotificationConfig config : getSlackNotificationsAsList()){
    		if (config.isSpecificBuildTypeEnabled(buildType)){
    			buildHooks.add(config);
    		}
    	}
    	return buildHooks;
    }    
        
	
    public String getSlackNotificationsAsString(){
    	String tmpString = "";
    	for(SlackNotificationConfig whConf : slackNotificationsConfigs)
    	{
    		tmpString += whConf.getChannel() + "<br/>";
    	}
    	return tmpString;
    }

    public void deleteSlackNotification(String slackNotificationId, String ProjectId){
        if(this.slackNotificationsConfigs != null)
        {
        	updateSuccess = false;
        	updateMessage = "";
        	List<SlackNotificationConfig> tempSlackNotificationList = new ArrayList<SlackNotificationConfig>();
            for(SlackNotificationConfig whc : slackNotificationsConfigs)
            {
                if (whc.getUniqueKey().equals(slackNotificationId)){
                	Loggers.SERVER.debug(NAME + ":deleteSlackNotification :: Deleting slacknotifications from " + ProjectId + " with Channel " + whc.getChannel());
                	tempSlackNotificationList.add(whc);
                }
            }
            if (!tempSlackNotificationList.isEmpty()){
            	this.updateSuccess = true;
            	this.slackNotificationsConfigs.removeAll(tempSlackNotificationList);
            }
        }    	
    }

	public void updateSlackNotification(String ProjectId, String token, String slackNotificationId, String channel, Boolean enabled, BuildState buildState, boolean buildTypeAll, boolean buildSubProjects, Set<String> buildTypesEnabled, boolean mentionChannelEnabled, boolean mentionSlackUserEnabled, SlackNotificationContentConfig content) {
        if(this.slackNotificationsConfigs != null)
        {
        	updateSuccess = false;
        	updateMessage = "";
            for(SlackNotificationConfig whc : slackNotificationsConfigs)
            {
                if (whc.getUniqueKey().equals(slackNotificationId)){
                	whc.setEnabled(enabled);
					whc.setToken(token);
                	whc.setChannel(channel);
                    whc.setMentionChannelEnabled(mentionChannelEnabled);
					whc.setMentionSlackUserEnabled(mentionSlackUserEnabled);
                	whc.setBuildStates(buildState);
                	whc.enableForSubProjects(buildSubProjects);
                	whc.enableForAllBuildsInProject(buildTypeAll);
                    whc.setHasCustomContent(content.isEnabled());
                    whc.setContent(content);
                	if (!buildTypeAll){
                		whc.clearAllEnabledBuildsInProject();
                		for (String bt : buildTypesEnabled){
                			whc.enableBuildInProject(bt);
                		}
                	}
                	Loggers.SERVER.debug(NAME + ":updateSlackNotification :: Updating slacknotifications from " + ProjectId + " with URL " + whc.getChannel());
                   	this.updateSuccess = true;
                }
            }
        }    			
	}

	public void addNewSlackNotification(String ProjectId, String token, String channel, String teamName, Boolean enabled, BuildState buildState, boolean buildTypeAll, boolean buildTypeSubProjects, Set<String> buildTypesEnabled, boolean mentionChannelEnabled, boolean mentionSlackUserEnabled) {
		this.slackNotificationsConfigs.add(new SlackNotificationConfig(token, channel, teamName, enabled, buildState, buildTypeAll, buildTypeSubProjects, buildTypesEnabled, mentionChannelEnabled, mentionSlackUserEnabled));
		Loggers.SERVER.debug(NAME + ":addNewSlackNotification :: Adding slack notifications to " + ProjectId + " with channel " + channel);
		this.updateSuccess = true;
	}

    public Boolean updateSuccessful(){
    	return this.updateSuccess;
    }
    
	public void dispose() {
		Loggers.SERVER.debug(NAME + ":dispose() called");
	}

	public Integer getSlackNotificationsCount(){
		return this.slackNotificationsConfigs.size();
	}
	
	public Boolean isEnabled() {
		return slackNotificationsEnabled;
	}

	public String isEnabledAsChecked() {
		if (this.slackNotificationsEnabled){
			return "checked ";
		}
		return "";
	}
	
	public List<SlackNotificationConfig> getSlackNotificationsConfigs() {
		return slackNotificationsConfigs;
	}

	public String getUpdateMessage() {
		return updateMessage;
	}

}
