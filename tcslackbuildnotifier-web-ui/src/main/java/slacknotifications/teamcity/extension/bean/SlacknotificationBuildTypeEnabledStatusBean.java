package slacknotifications.teamcity.extension.bean;

public class SlacknotificationBuildTypeEnabledStatusBean {
	
	boolean enabled;
	String buildTypeId;
	String buildTypeName;
	
	public SlacknotificationBuildTypeEnabledStatusBean(String buildTypeId, String buildTypeName, boolean enabled) {
		this.buildTypeId = buildTypeId;
		this.buildTypeName = buildTypeName;
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public String getEnabledAsChecked(){
		return enabled ? "checked" : "";
	}

	public String getBuildTypeId() {
		return buildTypeId;
	}

	public String getBuildTypeName() {
		return buildTypeName;
	}
	
	

}
