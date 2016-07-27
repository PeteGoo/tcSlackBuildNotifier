package slacknotifications.teamcity.extension.bean;

import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationContentConfig;
import slacknotifications.teamcity.settings.SlackNotificationMainConfig;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;

import java.util.ArrayList;
import java.util.List;

public class SlacknotificationConfigAndBuildTypeListHolder {
   

	private String token;
	private String channel;
	private String uniqueKey; 
	private boolean enabled;
	private String payloadFormatForWeb = "Unknown";
	private List<StateBean> states = new ArrayList<StateBean>();
	private boolean allBuildTypesEnabled;
	private boolean subProjectsEnabled;
	
	
	
	
	private List<SlacknotificationBuildTypeEnabledStatusBean> builds = new ArrayList<SlacknotificationBuildTypeEnabledStatusBean>();
	private String enabledEventsListForWeb;
	private String enabledBuildsListForWeb;
	private boolean mentionChannelEnabled;
	private boolean mentionSlackUserEnabled;
    private boolean customContentEnabled;
    private boolean showBuildAgent;
    private boolean showElapsedBuildTime;
    private boolean showCommits;
    private boolean showCommitters;
    private int maxCommitsToDisplay;
    private boolean showFailureReason;
    private String botName;
	private String templateTitle;
	private String templateBody;
    private String iconUrl;

	public SlacknotificationConfigAndBuildTypeListHolder(SlackNotificationConfig config, SlackNotificationMainSettings mainSettings) {
		token = config.getToken();
		channel = config.getChannel();
		uniqueKey = config.getUniqueKey();
		enabled = config.getEnabled();
		setEnabledEventsListForWeb(config.getEnabledListAsString());
		setEnabledBuildsListForWeb(config.getBuildTypeCountAsFriendlyString());
		allBuildTypesEnabled = config.isEnabledForAllBuildsInProject();
		subProjectsEnabled = config.isEnabledForSubProjects();
		for (BuildStateEnum state : config.getBuildStates().getStateSet()){
			states.add(new StateBean(state.getShortName(), config.getBuildStates().enabled(state)));
		}
		mentionChannelEnabled = config.getMentionChannelEnabled();
		mentionSlackUserEnabled = config.getMentionSlackUserEnabled();
        maxCommitsToDisplay = config.getContent().getMaxCommitsToDisplay();
        customContentEnabled = config.getContent().isEnabled();
        showBuildAgent = valueOrFallback(config.getContent().getShowBuildAgent(), valueOrFallback(mainSettings.getShowBuildAgent(), SlackNotificationContentConfig.DEFAULT_SHOW_BUILD_AGENT));
        showElapsedBuildTime = valueOrFallback(config.getContent().getShowElapsedBuildTime(), valueOrFallback(mainSettings.getShowElapsedBuildTime(), SlackNotificationContentConfig.DEFAULT_SHOW_ELAPSED_BUILD_TIME));
        showCommits = valueOrFallback(config.getContent().getShowCommits(), valueOrFallback(mainSettings.getShowCommits(), SlackNotificationContentConfig.DEFAULT_SHOW_COMMITS));
        showCommitters = valueOrFallback(config.getContent().getShowCommitters(), valueOrFallback(mainSettings.getShowCommitters(), SlackNotificationContentConfig.DEFAULT_SHOW_COMMITTERS));
        showFailureReason = valueOrFallback(config.getContent().getShowFailureReason(), valueOrFallback(mainSettings.getShowFailureReason(), SlackNotificationContentConfig.DEFAULT_SHOW_FAILURE_REASON));
        botName = valueOrFallback(config.getContent().getBotName(), SlackNotificationMainConfig.DEFAULT_BOTNAME);
        templateTitle = config.getContent().getTemplateTitle();
        templateBody = config.getContent().getTemplateBody();
        iconUrl = valueOrFallback(config.getContent().getIconUrl(), SlackNotificationMainConfig.DEFAULT_ICONURL);
	}

	
	 public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getChannel() {
			return channel;
		}

		public void setChannel(String channel) {
			this.channel = channel;
		}

		public String getUniqueKey() {
			return uniqueKey;
		}

		public void setUniqueKey(String uniqueKey) {
			this.uniqueKey = uniqueKey;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getPayloadFormatForWeb() {
			return payloadFormatForWeb;
		}

		public void setPayloadFormatForWeb(String payloadFormatForWeb) {
			this.payloadFormatForWeb = payloadFormatForWeb;
		}

		public List<StateBean> getStates() {
			return states;
		}

		public void setStates(List<StateBean> states) {
			this.states = states;
		}

		public boolean isAllBuildTypesEnabled() {
			return allBuildTypesEnabled;
		}

		public void setAllBuildTypesEnabled(boolean allBuildTypesEnabled) {
			this.allBuildTypesEnabled = allBuildTypesEnabled;
		}

		public boolean isSubProjectsEnabled() {
			return subProjectsEnabled;
		}

		public void setSubProjectsEnabled(boolean subProjectsEnabled) {
			this.subProjectsEnabled = subProjectsEnabled;
		}
	
	public List<SlacknotificationBuildTypeEnabledStatusBean> getBuilds() {
		return builds;
	}
	
	public String getEnabledBuildTypes(){
		StringBuilder types = new StringBuilder();
		for (SlacknotificationBuildTypeEnabledStatusBean build : getBuilds()){
			if (build.enabled){
				types.append(build.buildTypeId).append(",");
			}
		}
		return types.toString();
		
	}

    private boolean valueOrFallback(Boolean value, boolean fallback){
        return value == null ? fallback : value.booleanValue();
    }

    private String valueOrFallback(String value, String fallback){
        return value == null ? fallback : value;
    }

	public void setBuilds(List<SlacknotificationBuildTypeEnabledStatusBean> builds) {
		this.builds = builds;
	}
	
	
	public void addSlackNotificationBuildType(SlacknotificationBuildTypeEnabledStatusBean status){
		this.builds.add(status);
	}

	public String getEnabledEventsListForWeb() {
		return enabledEventsListForWeb;
	}

	public void setEnabledEventsListForWeb(String enabledEventsListForWeb) {
		this.enabledEventsListForWeb = enabledEventsListForWeb;
	}

	public String getEnabledBuildsListForWeb() {
		return enabledBuildsListForWeb;
	}

	public void setEnabledBuildsListForWeb(String enabledBuildsListForWeb) {
		this.enabledBuildsListForWeb = enabledBuildsListForWeb;
	}
	
}
