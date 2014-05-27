package slacknotifications.teamcity;

public interface BuildStateInterface {
	public boolean isEnabled();
	public void enable();
	public void disable();
	public String getShortName();
	public String getDescriptionSuffix();
}
