package slacknotifications.teamcity.settings.converter;

public final class OldStyleBuildState {
    public static final Integer BUILD_STARTED  			= Integer.parseInt("00000001",2);
    public static final Integer BUILD_FINISHED 			= Integer.parseInt("00000010",2);
    public static final Integer BUILD_CHANGED_STATUS 	= Integer.parseInt("00000100",2);
    public static final Integer BEFORE_BUILD_FINISHED 	= Integer.parseInt("00001000",2);
    public static final Integer RESPONSIBILITY_CHANGED 	= Integer.parseInt("00010000",2);
    public static final Integer BUILD_INTERRUPTED 		= Integer.parseInt("00100000",2);
    
    public static final Integer ALL_ENABLED				= Integer.parseInt("11111111",2);
    
    
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
		
		if (stateInt.equals(BUILD_STARTED)) 		{	return "buildStarted"; }
		if (stateInt.equals(BUILD_FINISHED))		{	return "buildFinished"; }
		if (stateInt.equals(BUILD_CHANGED_STATUS)) 	{ 	return "statusChanged"; }
		if (stateInt.equals(BEFORE_BUILD_FINISHED)) {	return "beforeBuildFinish"; }
		if (stateInt.equals(RESPONSIBILITY_CHANGED)){ 	return "responsibilityChanged"; }
		if (stateInt.equals(BUILD_INTERRUPTED))		{ 	return "buildInterrupted"; }
		return null;
	}
	
	/**
	 * Convert build state Integer into descriptive string 
	 * 
	 * @param  Build state as an Integer constant.
	 * @return A string that fits into the sentence "The build has...<state>"
	 */
	public static String getDescriptionSuffix(Integer stateInt) {

		if (stateInt.equals(BUILD_STARTED)) 		{	 return "started"; }
		if (stateInt.equals(BUILD_FINISHED)) 		{	 return "finished"; }
		if (stateInt.equals(BUILD_CHANGED_STATUS)) 	{	 return "changed status"; }
		if (stateInt.equals(BEFORE_BUILD_FINISHED)) {	 return "nearly finished"; }
		if (stateInt.equals(RESPONSIBILITY_CHANGED)){	 return "changed responsibility"; }
		if (stateInt.equals(BUILD_INTERRUPTED)) 	{	 return "been interrupted"; }
		return null;
	}
    
}
