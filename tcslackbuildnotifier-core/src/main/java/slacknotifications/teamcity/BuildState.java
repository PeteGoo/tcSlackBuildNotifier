package slacknotifications.teamcity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static slacknotifications.teamcity.BuildStateEnum.*;

public class BuildState {

	Map<BuildStateEnum, BuildStateInterface> states = new HashMap<BuildStateEnum, BuildStateInterface>();
	
	public BuildState() {
		states.clear();
		states.put(BuildStateEnum.BUILD_STARTED, 			new SimpleBuildState(BuildStateEnum.BUILD_STARTED, 			false));
		//states.put(BuildStateEnum.BUILD_CHANGED_STATUS, 	new SimpleBuildState(BuildStateEnum.BUILD_CHANGED_STATUS, 	false)); 	
		states.put(BuildStateEnum.BEFORE_BUILD_FINISHED, 	new SimpleBuildState(BuildStateEnum.BEFORE_BUILD_FINISHED, 	false)); 
		states.put(BuildStateEnum.RESPONSIBILITY_CHANGED, 	new SimpleBuildState(BuildStateEnum.RESPONSIBILITY_CHANGED,	false));
		states.put(BuildStateEnum.BUILD_INTERRUPTED, 		new SimpleBuildState(BuildStateEnum.BUILD_INTERRUPTED, 		false));
		states.put(BuildStateEnum.BUILD_SUCCESSFUL, 		new SimpleBuildState(BuildStateEnum.BUILD_SUCCESSFUL, 		false));
		states.put(BuildStateEnum.BUILD_FAILED, 			new SimpleBuildState(BuildStateEnum.BUILD_FAILED, 			false));

		states.put(BuildStateEnum.BUILD_BROKEN, 			new SimpleBuildState(BuildStateEnum.BUILD_BROKEN, 			false));
		states.put(BuildStateEnum.BUILD_FIXED, 				new SimpleBuildState(BuildStateEnum.BUILD_FIXED, 			false));
		
		states.put(BuildStateEnum.BUILD_FINISHED, 			new SimpleBuildState(BuildStateEnum.BUILD_FINISHED, 		false)); 		
	}
	
	public Set<BuildStateEnum> getStateSet(){
		return states.keySet();
	}
	
    /**
     * Takes the currentBuildState, for which the SlackNotification is being triggered
     * and compares it against the build states for which this SlackNotification is configured
     * to notify.
     * 
     * @param currentBuildState
     * @param buildStatesToNotify
     * @return Whether or not the slacknotifications should trigger for the current build state.
     */
    public boolean enabled(BuildStateEnum currentBuildState) {
    	return states.get(currentBuildState).isEnabled();
	}
    
    public boolean enabled(BuildStateEnum currentBuildState, boolean success, boolean changed){
    	if (currentBuildState != BuildStateEnum.BUILD_FINISHED){
    		return enabled(currentBuildState);
    	} else {
    		if (enabled(BUILD_SUCCESSFUL) &&  enabled(BUILD_FIXED) && changed && success){
    			return true;
    		}
    		if (enabled(BUILD_SUCCESSFUL) && !enabled(BUILD_FIXED) && success){
    			return true;
    		}
    		if (enabled(BUILD_FAILED) && enabled(BUILD_BROKEN) && changed && !success){
    			return true;
    		}
    		if (enabled(BUILD_FAILED) && !enabled(BUILD_BROKEN) && !success){
    			return true;
    		}
    	}
    	return false;
    }
    
    public void setEnabled(BuildStateEnum currentBuildState, boolean enabled){
    	if (enabled)
    		enable(currentBuildState);
    	else
    		disable(currentBuildState);
    }
    
    /**
     * Enable all build events for notification
     * Note: BROKEN and FIXED restrict builds, so don't set those.
     */
    public BuildState setAllEnabled(){
    	for (BuildStateEnum state : states.keySet()){
    		switch (state){
    		case BUILD_BROKEN:
    			disable(state);
    			break;
    		case BUILD_FIXED:
    			disable(state);
    			break;
    		default:
    			enable(state);
    			break;
    		}
    	}
    	return this;
    }
    
    public void enable(BuildStateEnum currentBuildState){
    	states.get(currentBuildState).enable();
    }

    public void disable(BuildStateEnum currentBuildState){
    	states.get(currentBuildState).disable();
    }

    /**
     * Convert build state Integer into short string 
     * 
     * @param  Build state as an Integer constant.
     * @return A string representing the shortname of the state. Is used in messages.
     */
	public String getShortName(BuildStateEnum state) {
			return state.getShortName();
	}
	
	/**
	 * Convert build state Integer into descriptive string 
	 * 
	 * @param  Build state as an Integer constant.
	 * @return A string that fits into the sentence "The build has...<state>"
	 */
	public String getDescriptionSuffix(BuildStateEnum state) {
		return state.getDescriptionSuffix();
	}

	public boolean allEnabled() {
		boolean areAllEnbled = true;
		for (BuildStateEnum state : states.keySet()){
			if ((state.equals(BUILD_BROKEN) && states.get(BUILD_BROKEN).isEnabled()) || (state.equals(BUILD_FIXED) && states.get(BUILD_FIXED).isEnabled())){
				return false;
			}
			if (state.equals(BUILD_BROKEN) || state.equals(BUILD_FIXED)) {
				continue;
			}
			areAllEnbled = areAllEnbled && states.get(state).isEnabled();  
		}
		return areAllEnbled;
	}

	public boolean noneEnabled() {
		int enabled = 0;
		for (BuildStateEnum state : states.keySet()){
			if (state.equals(BUILD_BROKEN)){
				continue;
			}
			if (state.equals(BUILD_FIXED)){
				continue;
			}
			if (state.equals(BUILD_SUCCESSFUL)){
				continue;
			}
			if (state.equals(BUILD_FAILED)){
				continue;
			}
			
			if (state.equals(BUILD_FINISHED)){
				if (finishEnabled()){
					enabled++;
				}
				continue;
			}
			if (states.get(state).isEnabled())  
				enabled++;
		}
		return enabled == 0;
	}
	
	
	private boolean finishEnabled(){
		// If finished is disabled, who cares what the other finish states are set to.
		if (! states.get(BUILD_FINISHED).isEnabled()){
			return false;
		}
		
		// If it's enabled, check its sub-settings.
		return (states.get(BUILD_FAILED).isEnabled() || states.get(BUILD_SUCCESSFUL).isEnabled());
	}
}
