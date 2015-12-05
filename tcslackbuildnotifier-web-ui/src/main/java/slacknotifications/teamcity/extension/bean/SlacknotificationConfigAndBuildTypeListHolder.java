package slacknotifications.teamcity.extension.bean;

import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationContentConfig;
import slacknotifications.teamcity.settings.SlackNotificationMainConfig;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;

import java.util.ArrayList;
import java.util.List;

public class SlacknotificationConfigAndBuildTypeListHolder {
    public String token;
	public String channel;
	public String uniqueKey; 
	public boolean enabled;
	public String payloadFormatForWeb = "Unknown";
	public List<StateBean> states = new ArrayList<StateBean>();
	public boolean allBuildTypesEnabled;
	public boolean subProjectsEnabled;
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
        iconUrl = valueOrFallback(config.getContent().getIconUrl(), SlackNotificationMainConfig.DEFAULT_ICONURL);
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
