package slacknotifications.teamcity.settings.converter;

import java.util.HashMap;
import java.util.Map;

public final class OldStyleBuildState {
    public static final Integer BUILD_STARTED  			= Integer.parseInt("00000001",2);
    public static final Integer BUILD_FINISHED 			= Integer.parseInt("00000010",2);
    public static final Integer BUILD_CHANGED_STATUS 	= Integer.parseInt("00000100",2);
    public static final Integer BEFORE_BUILD_FINISHED 	= Integer.parseInt("00001000",2);
    public static final Integer RESPONSIBILITY_CHANGED 	= Integer.parseInt("00010000",2);
    public static final Integer BUILD_INTERRUPTED 		= Integer.parseInt("00100000",2);
    
    public static final Integer ALL_ENABLED				= Integer.parseInt("11111111",2);
	private static final Map<Integer, String>  shortNameMessages;
	private static final Map<Integer, String>  descriptionMessages;

	static {
		shortNameMessages = new HashMap<Integer, String>();
		shortNameMessages.put(BUILD_STARTED, "buildStarted");
		shortNameMessages.put(BUILD_FINISHED, "buildFinished");
		shortNameMessages.put(BUILD_CHANGED_STATUS, "statusChanged");
		shortNameMessages.put(BEFORE_BUILD_FINISHED, "beforeBuildFinish");
		shortNameMessages.put(RESPONSIBILITY_CHANGED, "responsibilityChanged");
		shortNameMessages.put(BUILD_INTERRUPTED, "buildInterrupted");

		descriptionMessages = new HashMap<Integer, String>();
		descriptionMessages.put(BUILD_STARTED, "started");
		descriptionMessages.put(BUILD_FINISHED, "finished");
		descriptionMessages.put(BUILD_CHANGED_STATUS, "changed status");
		descriptionMessages.put(BEFORE_BUILD_FINISHED, "nearly finished");
		descriptionMessages.put(RESPONSIBILITY_CHANGED, "changed responsibility");
		descriptionMessages.put(BUILD_INTERRUPTED, "been interrupted");
	}
    
    private OldStyleBuildState(){}
    /**
     * Takes the currentBuildState, for which the SlackNotification is being triggered
     * and compares it against the build states for which this SlackNotification is configured
     * to notify.
     * 
     * @param currentBuildState
     * @param buildStatesToNotify
     * @return Whether or not the slacknotifications should trigger for the current build state.
     */
    public static boolean enabled(Integer currentBuildState, Integer buildStatesToNotify) {
		 
		return ((currentBuildState & buildStatesToNotify) > 0);
	}

    /**
     * Convert build state Integer into short string 
     * 
     * @param  Build state as an Integer constant.
     * @return A string representing the shortname of the state. Is used in messages.
     */
	public static String getShortName(Integer stateInt) {
		return shortNameMessages.get(stateInt);
	}
	
	/**
	 * Convert build state Integer into descriptive string 
	 * 
	 * @param  Build state as an Integer constant.
	 * @return A string that fits into the sentence "The build has...<state>"
	 */
	public static String getDescriptionSuffix(Integer stateInt) {
		return  descriptionMessages.get(stateInt);
	}
    
}
