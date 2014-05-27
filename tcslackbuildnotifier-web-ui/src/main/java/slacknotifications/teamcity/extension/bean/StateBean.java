package slacknotifications.teamcity.extension.bean;
public class StateBean{
	public String buildStateName;
	public boolean enabled;
	
	public StateBean(String name1, boolean enabled1) {
		this.buildStateName = name1;
		this.enabled = enabled1;
	}
}