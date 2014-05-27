package slacknotifications.teamcity.extension.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.payload.SlackNotificationPayload;
import slacknotifications.teamcity.settings.SlackNotificationConfig;

public class SlacknotificationConfigAndBuildTypeListHolder {
	public String url;
	public String uniqueKey; 
	public boolean enabled;
	public String payloadFormat;
	public String payloadFormatForWeb = "Unknown";
	public List<StateBean> states = new ArrayList<StateBean>();
	public boolean allBuildTypesEnabled;
	public boolean subProjectsEnabled;
	private List<SlacknotificationBuildTypeEnabledStatusBean> builds = new ArrayList<SlacknotificationBuildTypeEnabledStatusBean>();
	private String enabledEventsListForWeb;
	private String enabledBuildsListForWeb;
	
	public SlacknotificationConfigAndBuildTypeListHolder(SlackNotificationConfig config, Collection<SlackNotificationPayload> registeredPayloads) {
		url = config.getUrl();
		uniqueKey = config.getUniqueKey();
		enabled = config.getEnabled();
		payloadFormat = config.getPayloadFormat();
		setEnabledEventsListForWeb(config.getEnabledListAsString());
		setEnabledBuildsListForWeb(config.getBuildTypeCountAsFriendlyString());
		allBuildTypesEnabled = config.isEnabledForAllBuildsInProject();
		subProjectsEnabled = config.isEnabledForSubProjects();
		for (BuildStateEnum state : config.getBuildStates().getStateSet()){
			states.add(new StateBean(state.getShortName(), config.getBuildStates().enabled(state)));
		}
		for (SlackNotificationPayload payload : registeredPayloads){
			if (payload.getFormatShortName().equals(payloadFormat)){
				this.payloadFormatForWeb = payload.getFormatDescription();
			}
		}
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
