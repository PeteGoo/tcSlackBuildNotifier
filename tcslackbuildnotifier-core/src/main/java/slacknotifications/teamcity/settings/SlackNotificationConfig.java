package slacknotifications.teamcity.settings;

import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.serverSide.SBuildType;
import org.jdom.DataConversionException;
import org.jdom.Element;
import slacknotifications.teamcity.BuildState;
import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.settings.converter.SlackNotificationBuildStateConverter;

import java.util.*;

import static slacknotifications.teamcity.BuildStateEnum.*;


public class SlackNotificationConfig {
	private Boolean enabled = true;
	private String uniqueKey = "";
	private String apiToken;
	private String channel;
    private String teamName;
	private BuildState states = new BuildState();
	private SortedMap<String, CustomMessageTemplate> templates; 
	private Boolean allBuildTypesEnabled = true;
	private Boolean subProjectsEnabled = true;
	private Set<String> enabledBuildTypesSet = new HashSet<String>();
    private boolean mentionChannelEnabled;
	private boolean mentionSlackUserEnabled;
    private boolean customContent;
    private SlackNotificationContentConfig content;

    @SuppressWarnings("unchecked")
	public SlackNotificationConfig(Element e) {
		this.content = new SlackNotificationContentConfig();
		int Min = 1000000, Max = 1000000000;
		Integer Rand = Min + (int)(Math.random() * ((Max - Min) + 1));
		this.uniqueKey = Rand.toString();
		this.templates = new TreeMap<String,CustomMessageTemplate>();
		
		if (e.getAttribute("token") != null){
			this.setChannel(e.getAttributeValue("token"));
		}
		if (e.getAttribute("channel") != null){
			this.setChannel(e.getAttributeValue("channel"));
		}

        if (e.getAttribute("teamName") != null){
            this.setTeamName(e.getAttributeValue("teamName"));
        }
		
		if (e.getAttribute("enabled") != null){
			this.setEnabled(Boolean.parseBoolean(e.getAttributeValue("enabled")));
		}

		if (e.getAttribute("statemask") != null){
			this.setBuildStates(SlackNotificationBuildStateConverter.convert(Integer.parseInt(e.getAttributeValue("statemask"))));
		}

		if (e.getAttribute("key") != null){
			this.setUniqueKey(e.getAttributeValue("key"));
		}

        if (e.getAttribute("mention-channel-enabled") != null){
            this.setMentionChannelEnabled(Boolean.parseBoolean(e.getAttributeValue("mention-channel-enabled")));
        }

		if (e.getAttribute("mention-slack-user-enabled") != null){
			this.setMentionSlackUserEnabled(Boolean.parseBoolean(e.getAttributeValue("mention-slack-user-enabled")));
		}
		
		if(e.getChild("states") != null){
			Element eStates = e.getChild("states");
			List<Element> statesList = eStates.getChildren("state");
			if (!statesList.isEmpty()){
				for(Element eState : statesList)
				{
					try {
						states.setEnabled(BuildStateEnum.findBuildState(eState.getAttributeValue("type")), 
										  eState.getAttribute("enabled").getBooleanValue());
					} catch (DataConversionException e1) {e1.printStackTrace();}
				}
			}
		}
		
		if(e.getChild("build-types") != null){
			Element eTypes = e.getChild("build-types");
			if (eTypes.getAttribute("enabled-for-all") != null){
				try {
					this.enableForAllBuildsInProject(eTypes.getAttribute("enabled-for-all").getBooleanValue());
				} catch (DataConversionException e1) {e1.printStackTrace();}
			}
			if (eTypes.getAttribute("enabled-for-subprojects") != null){
				try {
					this.enableForSubProjects(eTypes.getAttribute("enabled-for-subprojects").getBooleanValue());
				} catch (DataConversionException e1) {e1.printStackTrace();}
			}
			if (!isEnabledForAllBuildsInProject()){
				List<Element> typesList = eTypes.getChildren("build-type");
				if (!typesList.isEmpty()){
					for(Element eType : typesList)
					{
						if (eType.getAttributeValue("id")!= null){
							enabledBuildTypesSet.add(eType.getAttributeValue("id"));
						}
					}
				}
			}
		}
		

		
		if(e.getChild("custom-templates") != null){
			Element eParams = e.getChild("custom-templates");
			List<Element> templateList = eParams.getChildren("custom-template");
			if (!templateList.isEmpty()){
				for(Element eParam : templateList)
				{
					this.templates.put(
							eParam.getAttributeValue(CustomMessageTemplate.TYPE),
							CustomMessageTemplate.create(
									eParam.getAttributeValue(CustomMessageTemplate.TYPE),
									eParam.getAttributeValue(CustomMessageTemplate.TEMPLATE),
									Boolean.parseBoolean(eParam.getAttributeValue(CustomMessageTemplate.ENABLED))
									)
							);
				}
			}
		}

        if(e.getChild("content") != null) {
            setHasCustomContent(true);
            Element eContent = e.getChild("content");

            this.content.setEnabled(true);

            if (eContent.getAttribute("iconUrl") != null){
                this.content.setIconUrl(eContent.getAttributeValue("iconUrl"));
            }
            if (eContent.getAttribute("botName") != null){
                this.content.setBotName(eContent.getAttributeValue("botName"));
            }
            if (eContent.getAttribute("showBuildAgent") != null){
                this.content.setShowBuildAgent(Boolean.parseBoolean(eContent.getAttributeValue("showBuildAgent")));
            }
            if (eContent.getAttribute("showElapsedBuildTime") != null){
                this.content.setShowElapsedBuildTime(Boolean.parseBoolean(eContent.getAttributeValue("showElapsedBuildTime")));
            }
            if (eContent.getAttribute("showCommits") != null){
                this.content.setShowCommits(Boolean.parseBoolean(eContent.getAttributeValue("showCommits")));
            }
            if (eContent.getAttribute("showCommitters") != null){
                this.content.setShowCommitters(Boolean.parseBoolean(eContent.getAttributeValue("showCommitters")));
            }
            if (eContent.getAttribute("maxCommitsToDisplay") != null){
                this.content.setMaxCommitsToDisplay(Integer.parseInt(eContent.getAttributeValue("maxCommitsToDisplay")));
            }
            if (eContent.getAttribute("showFailureReason") != null){
                this.content.setShowFailureReason(Boolean.parseBoolean(eContent.getAttributeValue("showFailureReason")));
            }
        }

		
	}

    /**
     * SlackNotificationsConfig constructor. Unchecked version. Use with caution!!
     * This constructor does not check if the payloadFormat is valid.
     * It will still allow you to add the format, but the slacknotifications might not
     * fire at runtime if the payloadFormat configured is not available.
     *
     * @param channel
     * @param enabled
     * @param states
     */
    public SlackNotificationConfig(String token,
								   String channel,
								   String teamName,
								   Boolean enabled,
								   BuildState states,
								   boolean buildTypeAllEnabled,
								   boolean buildTypeSubProjects,
								   Set<String> enabledBuildTypes,
								   boolean mentionChannelEnabled,
								   boolean mentionSlackUserEnabled) {
        this.content = new SlackNotificationContentConfig();
        int Min = 1000000, Max = 1000000000;
        Integer Rand = Min + (int) (Math.random() * ((Max - Min) + 1));
        this.uniqueKey = Rand.toString();
        this.templates = new TreeMap<String, CustomMessageTemplate>();
        this.setToken(token);
        this.setChannel(channel);
        this.setTeamName(teamName);
        this.setEnabled(enabled);
        this.setBuildStates(states);
        this.subProjectsEnabled = buildTypeSubProjects;
        this.allBuildTypesEnabled = buildTypeAllEnabled;
        this.setMentionChannelEnabled(mentionChannelEnabled);
		this.setMentionSlackUserEnabled(mentionSlackUserEnabled);

        if (!this.allBuildTypesEnabled) {
            this.enabledBuildTypesSet = enabledBuildTypes;
        }
    }
	
	public Element getAsElement(){
		Element el = new Element("slackNotification");
		el.setAttribute("channel", this.getChannel());

        if(StringUtil.isNotEmpty(this.getTeamName())) {
            el.setAttribute("teamName", this.getTeamName());
        }
		el.setAttribute("enabled", String.valueOf(this.enabled));
        el.setAttribute("mention-channel-enabled", String.valueOf(this.getMentionChannelEnabled()));
		el.setAttribute("mention-slack-user-enabled", String.valueOf(this.getMentionSlackUserEnabled()));

		Element statesEl = new Element("states");
		for (BuildStateEnum state : states.getStateSet()){
			Element e = new Element("state");
			e.setAttribute("type", state.getShortName());
			e.setAttribute("enabled", Boolean.toString(states.enabled(state)));
			statesEl.addContent(e);
		}
		el.addContent(statesEl);
		
		Element buildsEl = new Element("build-types");
		buildsEl.setAttribute("enabled-for-all", Boolean.toString(isEnabledForAllBuildsInProject()));
		buildsEl.setAttribute("enabled-for-subprojects", Boolean.toString(isEnabledForSubProjects()));
		
		if (!this.enabledBuildTypesSet.isEmpty()){
			for (String i : enabledBuildTypesSet){
				Element e = new Element("build-type");
				e.setAttribute("id", i);
				buildsEl.addContent(e);
			}
		}
		el.addContent(buildsEl);
		
		if (this.templates.size() > 0){
			Element templatesEl = new Element("custom-templates");
			for (CustomMessageTemplate t : this.templates.values()){
				templatesEl.addContent(t.getAsElement());
			}
			el.addContent(templatesEl);
		}

        if(this.hasCustomContent()){
            Element customContentEl = new Element("content");
            customContentEl.setAttribute("iconUrl", this.content.getIconUrl());
            customContentEl.setAttribute("botName", this.content.getBotName());
            customContentEl.setAttribute("maxCommitsToDisplay", Integer.toString(this.content.getMaxCommitsToDisplay()));
            customContentEl.setAttribute("showBuildAgent", this.content.getShowBuildAgent().toString());
            customContentEl.setAttribute("showElapsedBuildTime", this.content.getShowElapsedBuildTime().toString());
            customContentEl.setAttribute("showCommits", this.content.getShowCommits().toString());
            customContentEl.setAttribute("showCommitters", this.content.getShowCommitters().toString());
            customContentEl.setAttribute("showFailureReason", this.content.getShowFailureReason().toString());
            el.addContent(customContentEl);
        }
		
		return el;
	}
	
	// Getters and Setters..

	public boolean isEnabledForBuildType(SBuildType sBuildType){
		// If allBuildTypes enabled, return true, otherwise  return whether the build is in the list of enabled buildTypes. 
		return isEnabledForAllBuildsInProject() ? true : enabledBuildTypesSet.contains(TeamCityIdResolver.getInternalBuildId(sBuildType));
	}
	
	public boolean isSpecificBuildTypeEnabled(SBuildType sBuildType){
		// Just check if this build type is only enabled for a specific build. 
		return enabledBuildTypesSet.contains(TeamCityIdResolver.getInternalBuildId(sBuildType));
	}
	
	public String getBuildTypeCountAsFriendlyString(){
		if (this.allBuildTypesEnabled  && !this.subProjectsEnabled){
			return "All builds";
		} else if (this.allBuildTypesEnabled  && this.subProjectsEnabled){
				return "All builds & Sub-Projects";
		} else {
			String subProjectsString = "";
			if (this.subProjectsEnabled){
				subProjectsString = " & All Sub-Project builds";
			}
			int enabledBuildTypeCount = this.enabledBuildTypesSet.size();
			if (enabledBuildTypeCount == 1){
				return enabledBuildTypeCount + " build" + subProjectsString;
			}
			return enabledBuildTypeCount + " builds" + subProjectsString; 
		}
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public BuildState getBuildStates() {
		return states;
	}

	public void setBuildStates(BuildState states) {
		this.states = states;
	}

	public String getToken() {
		return apiToken;
	}

	public void setToken(String apiToken) {
		this.apiToken = apiToken;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
	
	public String getEnabledListAsString(){
		if (!this.enabled){
			return "Disabled";
		} else if (states.allEnabled()){
			return "All Build Events";
		} else if (states.noneEnabled()) {
			return "None";
		} else {
			String enabledStates = "";
			if (states.enabled(BuildStateEnum.BUILD_STARTED)){
				enabledStates += ", Build Started";
			}
//			if (BuildState.enabled(BuildState.BUILD_FINISHED,this.statemask)){
//				enabledStates += ", Build Completed";
//			}
//			if (BuildState.enabled(BuildState.BUILD_CHANGED_STATUS,this.statemask)){
//				enabledStates += ", Build Changed Status";
//			}
			if (states.enabled(BuildStateEnum.BUILD_INTERRUPTED)){
				enabledStates += ", Build Interrupted";
			}
			if (states.enabled(BuildStateEnum.BEFORE_BUILD_FINISHED)){
				enabledStates += ", Build Almost Completed";
			}
			if (states.enabled(BuildStateEnum.RESPONSIBILITY_CHANGED)){
				enabledStates += ", Build Responsibility Changed";
			}
			if (states.enabled(BuildStateEnum.BUILD_FAILED)){
				if (states.enabled(BuildStateEnum.BUILD_BROKEN)){
					enabledStates += ", Build Broken";
				} else {
					enabledStates += ", Build Failed";
				}
			}
			if (states.enabled(BuildStateEnum.BUILD_SUCCESSFUL)){
				if (states.enabled(BuildStateEnum.BUILD_FIXED)){
					enabledStates += ", Build Fixed";
				} else {
					enabledStates += ", Build Successful";
				}
			}
			if (enabledStates.length() > 0){
				return enabledStates.substring(1);
			} else {
				return "None";
			}
		}
	}
	
	public String getSlackNotificationEnabledAsChecked() {
		if (this.enabled){
			return "checked ";
		}
		return ""; 
	}
	
	public String getStateAllAsChecked() {
		if (states.allEnabled()){
			return "checked ";
		}		
		return ""; 
	}
	
	public String getStateBuildStartedAsChecked() {
		if (states.enabled(BUILD_STARTED)){
			return "checked ";
		}
		return ""; 
	}
	
	public String getStateBuildFinishedAsChecked() {
		if (states.enabled(BUILD_FINISHED)){
			return "checked ";
		}
		return ""; 
	}

	public String getStateBeforeFinishedAsChecked() {
		if (states.enabled(BEFORE_BUILD_FINISHED)){
			return "checked ";
		}
		return ""; 
	}

	public String getStateResponsibilityChangedAsChecked() {
		if (states.enabled(RESPONSIBILITY_CHANGED)){
			return "checked ";
		}
		return ""; 
	}

	public String getStateBuildInterruptedAsChecked() {
		if (states.enabled(BUILD_INTERRUPTED)){
			return "checked ";
		}
		return ""; 
	}
	
	public String getStateBuildSuccessfulAsChecked() {
		if (states.enabled(BUILD_SUCCESSFUL)){
			return "checked ";
		}
		return ""; 
	}
	
	public String getStateBuildFixedAsChecked() {
		if (states.enabled(BUILD_FIXED)){
			return "checked ";
		}
		return ""; 
	}
	
	public String getStateBuildFailedAsChecked() {
		if (states.enabled(BUILD_FAILED)){
			return "checked ";
		}
		return ""; 
	}

	public String getStateBuildBrokenAsChecked() {
		if (states.enabled(BUILD_BROKEN)){
			return "checked ";
		}
		return ""; 
	}

	public Boolean isEnabledForAllBuildsInProject() {
		return allBuildTypesEnabled;
	}

	public void enableForAllBuildsInProject(Boolean allBuildTypesEnabled) {
		this.allBuildTypesEnabled = allBuildTypesEnabled;
	}
	
	public Boolean isEnabledForSubProjects() {
		return subProjectsEnabled;
	}
	
	public void enableForSubProjects(Boolean subProjectsEnabled) {
		this.subProjectsEnabled = subProjectsEnabled;
	}
	
	public void clearAllEnabledBuildsInProject(){
		this.enabledBuildTypesSet.clear();
	}
	
	public void enableBuildInProject(String buildTypeId) {
		this.enabledBuildTypesSet.add(buildTypeId);
	}

    public void setMentionChannelEnabled(boolean mentionChannelEnabled) {
        this.mentionChannelEnabled = mentionChannelEnabled;
    }

    public boolean getMentionChannelEnabled() {
        return mentionChannelEnabled;
    }

	public void setMentionSlackUserEnabled(boolean mentionSlackUserEnabled) {
		this.mentionSlackUserEnabled = mentionSlackUserEnabled;
	}

	public boolean getMentionSlackUserEnabled() {
		return mentionSlackUserEnabled;
	}

    public boolean hasCustomContent() {
        return customContent;
    }

    public void setHasCustomContent(boolean customContent) {
        this.customContent = customContent;
    }

    public SlackNotificationContentConfig getContent() {
        return content;
    }

    public void setContent(SlackNotificationContentConfig content) {
        this.content = content;
    }
}
