package slacknotifications.teamcity.extension.bean;

import jetbrains.buildServer.serverSide.SBuildType;
import slacknotifications.teamcity.TeamCityIdResolver;
import slacknotifications.teamcity.settings.SlackNotificationConfig;

import java.util.List;

public class BuildSlacknotificationsBean{
	
	private SBuildType sBuildType;
	private List<SlackNotificationConfig> buildConfigs;
	
	public BuildSlacknotificationsBean(SBuildType b, List<SlackNotificationConfig> c) {
		this.setsBuildType(b);
		this.setBuildConfigs(c);
	}

	public SBuildType getsBuildType() {
		return sBuildType;
	}

	public void setsBuildType(SBuildType sBuildType) {
		this.sBuildType = sBuildType;
	}

	public List<SlackNotificationConfig> getBuildSlackNotificationList() {
		return buildConfigs;
	}

	public void setBuildConfigs(List<SlackNotificationConfig> buildConfigs) {
		this.buildConfigs = buildConfigs;
	}
	
	public boolean hasBuilds(){
		return !this.buildConfigs.isEmpty();
	}
	
	public boolean hasNoBuildSlackNotifications(){
		return this.buildConfigs.isEmpty();
	}
	
	public boolean hasBuildSlackNotifications(){
		return !this.buildConfigs.isEmpty();
	}
	
	public int getBuildCount(){
		return this.buildConfigs.size();
	}
	
	public String getBuildExternalId(){
		return TeamCityIdResolver.getExternalBuildId(sBuildType);
	}
	public String getBuildName(){
		return sBuildType.getName();
	}
	
}