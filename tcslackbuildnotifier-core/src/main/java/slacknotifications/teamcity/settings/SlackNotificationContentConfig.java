package slacknotifications.teamcity.settings;

/**
 * Created by petegoo on 24/02/15.
 */
public class SlackNotificationContentConfig {
    public static final int DEFAULT_MAX_COMMITS = 5;
    public static final boolean DEFAULT_SHOW_BUILD_AGENT = true;
    public static final boolean DEFAULT_SHOW_ELAPSED_BUILD_TIME = true;
    public static final boolean DEFAULT_SHOW_COMMITS = true;
    public static final boolean DEFAULT_SHOW_COMMITTERS = true;
    public static final boolean DEFAULT_SHOW_TRIGGERED_BY = true;
    public static final boolean DEFAULT_SHOW_FAILURE_REASON = false;
    public static final String DEFAULT_FILTER_BRANCH_NAME = "";
    private String iconUrl = SlackNotificationMainConfig.DEFAULT_ICONURL;
    private String botName = SlackNotificationMainConfig.DEFAULT_BOTNAME;
    private Boolean showBuildAgent;
    private Boolean showElapsedBuildTime;
    private Boolean showCommits = true;
    private Boolean showCommitters = true;
    private Boolean showTriggeredBy = true;
    private int maxCommitsToDisplay = 5;
    private boolean enabled;
    private Boolean showFailureReason;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
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

    public Boolean getShowFailureReason(){
        return showFailureReason;
    }

    public void setShowFailureReason(Boolean showFailureReason){
        this.showFailureReason = showFailureReason;
    }

    public Boolean getShowCommits() {
        return showCommits;
    }

    public void setShowCommits(Boolean showCommits) {
        this.showCommits = showCommits;
    }

    public Boolean getShowCommitters() {
        return showCommitters;
    }

    public void setShowCommitters(Boolean showCommitters) {
        this.showCommitters = showCommitters;
    }

    public Boolean getShowTriggeredBy() {
        return showTriggeredBy;
    }

    public void setShowTriggeredBy(Boolean showTriggeredBy) {
        this.showTriggeredBy = showTriggeredBy;
    }

    public int getMaxCommitsToDisplay() {
        return maxCommitsToDisplay;
    }

    public void setMaxCommitsToDisplay(int maxCommitsToDisplay) {
        this.maxCommitsToDisplay = maxCommitsToDisplay;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
