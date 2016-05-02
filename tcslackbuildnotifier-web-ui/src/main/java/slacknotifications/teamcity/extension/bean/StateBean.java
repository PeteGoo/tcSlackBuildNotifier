package slacknotifications.teamcity.extension.bean;
public class StateBean{
	private String buildStateName;
	private boolean enabled;
	
	public StateBean(String name1, boolean enabled1) {
		this.buildStateName = name1;
		this.enabled = enabled1;
	}

	public String getBuildStateName() {
		return buildStateName;
	}

	public void setBuildStateName(String buildStateName) {
		this.buildStateName = buildStateName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}