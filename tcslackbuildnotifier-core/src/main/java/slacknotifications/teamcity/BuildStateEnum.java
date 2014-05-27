package slacknotifications.teamcity;

public enum BuildStateEnum {
    BUILD_STARTED 			("buildStarted",			"started"),                
    BUILD_FINISHED 			("buildFinished", 			"finished"),
    //BUILD_CHANGED_STATUS	("statusChanged", 			"changed status"),
    BEFORE_BUILD_FINISHED	("beforeBuildFinish", 		"nearly finished"),
	RESPONSIBILITY_CHANGED	("responsibilityChanged",	"changed responsibility"),
	BUILD_INTERRUPTED		("buildInterrupted", 		"been interrupted"),
	BUILD_SUCCESSFUL		("buildSuccessful", 		"completed successfully"),
	BUILD_FAILED			("buildFailed", 			"failed"),
	BUILD_FIXED				("buildFixed", 				"been fixed"),
	BUILD_BROKEN			("buildBroken", 			"broken");
    
    private final String shortName;
    private final String descriptionSuffix;
    
    private BuildStateEnum(String shortname, String descriptionSuffix){
    	this.shortName = shortname;
    	this.descriptionSuffix = descriptionSuffix;
    }
    
    /**
     * 
     * @return A short name for the Enum. This is used to uniquely identify the BuildStateEnum
     * as a text string.
     * eg, "buildFixed"
     */
    public String getShortName(){
    	return this.shortName;
    }

    /**
     * @return a string that fits into the sentence "build blahblah has..."
     * eg, "been fixed"
     */
	public String getDescriptionSuffix() {
		return this.descriptionSuffix;
	}
	
	/**
	 * Takes a string and tries to find a BuildStateEnum that that matches it.
	 * @param stateString
	 * @return BuildStateEnum or null.
	 */
	public static BuildStateEnum findBuildState(String stateString){
		for (BuildStateEnum b : BuildStateEnum.values()) {
			if (b.shortName.equalsIgnoreCase(stateString)){
				return b;
			}
		}
		return null;
	}
}
