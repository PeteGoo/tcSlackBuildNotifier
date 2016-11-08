package slacknotifications.teamcity;

public class SimpleBuildState implements BuildStateInterface {

	BuildStateEnum state;
	boolean enabled;

	SimpleBuildState(BuildStateEnum state, boolean enabled) {
		this.state = state;
		this.enabled = enabled;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void enable() {
		this.enabled = true;
		
	}

	@Override
	public void disable() {
		this.enabled = false;
	}

}
