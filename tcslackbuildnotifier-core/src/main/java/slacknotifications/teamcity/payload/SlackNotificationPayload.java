package slacknotifications.teamcity.payload;

import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.ResponsibilityInfo;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.tests.TestName;

import org.jetbrains.annotations.NotNull;

public interface SlackNotificationPayload {
	
	public final static String BUILD_STATUS_FAILURE   = "failure";
	public final static String BUILD_STATUS_SUCCESS   = "success";
	public final static String BUILD_STATUS_RUNNING   = "running";
	public final static String BUILD_STATUS_NO_CHANGE = "unchanged";
	public final static String BUILD_STATUS_FIXED     = "fixed";
	public final static String BUILD_STATUS_BROKEN    = "broken";
	public final static String BUILD_STATUS_UNKNOWN   = "unknown";
	
	/** 
	 * Sets the PayloadManger so that register() can register this payload with that manager.
	 * 
	 * @param slackNotificationPayloadManager
	 */
	void setPayloadManager (SlackNotificationPayloadManager slackNotificationPayloadManager);
	
	/**
	 * Registers with the PayloadManager
	 * 
	 */
	void register();
	
	/**
	 * Returns a Description of the format type. This is used for display on the config page
	 * when selecting the SlackNotification format. The output will be used with <c:out in the JSP so
	 * any HTML tags will get escaped into &amp;lt; tag &amp;gt; etc.
	 *  
	 * @return	Text for display on the SlackNotification config page.
	 */
	String getFormatDescription();

	/**
	 * Returns a sightly longer Description of the format type. This is used in the "title" 
	 * tags for providing a "tool tip" when a user hovers over the format description. 
	 * The output will be used with <c:out in the JSP so 
	 * any HTML tags will get escaped into &amp;lt; tag &amp;gt; etc.
	 *  
	 * @return	Tool Tip Text for display on the SlackNotification config page.
	 */
	String getFormatToolTipText();
	
	/**
	 * Returns a short name for the format type. This string is used in the HTML form when 
	 * referring to the format, and used in the plugin-settings.xml file for specifying the
	 * format of the payload.
	 * 
	 * @return	Text for referring to the format type. eg, JSON.
	 */
	String getFormatShortName();

	
	
	/**
	 * Extracts the required information from the sRunningBuild and extraParameters configured in the slacknotifications
	 * or build parameters and returns a String of the SlackNotification payload.
	 *  
	 * @param sRunningBuild
	 * @param extraParameters
	 * @return Formatted payload for the SlackNotification to send for the buildStarted event.
	 */
    String buildStarted(SRunningBuild sRunningBuild, SFinishedBuild previousBuild, SortedMap<String,String> extraParameters, Map<String, String> templates);

    /**
	 * Extracts the required information from the sRunningBuild and extraParameters configured in the slacknotifications
	 * or build parameters and returns a String of the SlackNotification payload.
	 *  
	 * @param sRunningBuild
	 * @param extraParameters
	 * @return Formatted payload for the SlackNotification to send for the buildFinished event.
	 */
    String buildFinished(SRunningBuild sRunningBuild, SFinishedBuild previousBuild, SortedMap<String,String> extraParameters, Map<String, String> templates);

    /**
	 * Extracts the required information from the sRunningBuild and extraParameters configured in the slacknotifications
	 * or build parameters and returns a String of the SlackNotification payload.
	 *  
	 * @param sRunningBuild
	 * @param extraParameters
	 * @return Formatted payload for the SlackNotification to send for the buildInterrupted event.
	 */
    String buildInterrupted(SRunningBuild sRunningBuild, SFinishedBuild previousBuild, SortedMap<String,String> extraParameters, Map<String, String> templates);

    /**
	 * Extracts the required information from the sRunningBuild and extraParameters configured in the slacknotifications
	 * or build parameters and returns a String of the SlackNotification payload.
	 *  
	 * @param sRunningBuild
	 * @param extraParameters
	 * @return Formatted payload for the SlackNotification to send for the beforeBuildFinish event.
	 */
    String beforeBuildFinish(SRunningBuild sRunningBuild, SFinishedBuild previousBuild, SortedMap<String,String> extraParameters, Map<String, String> templates);
    
	/**
	 * buildChangedStatus has been deprecated because it alluded to build history status, which was incorrect.
	 * It will no longer be called by the SlackNotificationListener
	 */
	@Deprecated
    String buildChangedStatus(SRunningBuild sRunningBuild, SFinishedBuild previousBuild, 
    		Status oldStatus, 
    		Status newStatus, 
    		SortedMap<String,String> extraParameters, Map<String, String> templates);
	

    /**
     * Used by TC 6.5 and below
     * @param sBuildType
     * @param responsibilityInfoOld
     * @param responsibilityInfoNew
     * @param isUserAction
     * @param extraParameters
     * @return Formatted payload for the SlackNotification to send for the responsibleChanged event.
     */
    String responsibleChanged(@NotNull SBuildType sBuildType, 
    		@NotNull ResponsibilityInfo responsibilityInfoOld, 
    		@NotNull ResponsibilityInfo responsibilityInfoNew, 
    		boolean isUserAction, SortedMap<String,String> extraParameters, Map<String, String> templates);

    /**
     * Used by TC 7.x and above
     * @param sBuildType
     * @param responsibilityEntryOld
     * @param responsibilityEntryNew
     * @param params
     * @return Formatted payload for the SlackNotification to send for the responsibleChanged event.
     */
	String responsibleChanged(SBuildType sBuildType,
			ResponsibilityEntry responsibilityEntryOld,
			ResponsibilityEntry responsibilityEntryNew,
			SortedMap<String,String> extraParameters, Map<String, String> templates); 
    
	/**
	 * Gets the content type of the format.
	 * Should return a string like "application/json"
	 * 
	 * @return contentType;
	 */
    String getContentType();
    
    /**
     * Gets in Integer for order. The Higher the number, the more likely 
     * it is to appear higher in the list of options.
     * The highest number will be the default when showing the list of SlackNotifications
     * in the web UI.
     *  
     * Suggestion : When registering your plugin with Spring, you could set with a bean property
     * in the spring XML file. That way it can be edited by the end user if required.
     * 
     * @return rank (higher numbers are ranked first)
     */
    Integer getRank();
    
    /**
     * Set in Integer for order. The Higher the number, the more likely 
     * it is to appear higher in the list of options.
     * The highest number will be the default when showing the list of SlackNotifications
     * in the web UI.
     *  
     * Suggestion : When registering your plugin with Spring, you could set with a bean property
     * in the spring XML file. That way it can be edited by the end user if required.
     * 
     * @param rank (higher numbers are ranked first)
     */
    void setRank(Integer rank);

    /**
     * Get the character set that the payload is in. This is probably UTF-8, but is up to the 
     * implementation. 
     * @return charset (string like "UTF-8")
     */
	String getCharset();

	
	String responsibleChanged(SProject project,
			TestNameResponsibilityEntry oldTestNameResponsibilityEntry,
			TestNameResponsibilityEntry newTestNameResponsibilityEntry,
			boolean isUserAction, SortedMap<String,String> extraParameters, Map<String, String> templates);

	String responsibleChanged(SProject project, Collection<TestName> testNames,
			ResponsibilityEntry entry, boolean isUserAction,
			SortedMap<String,String> extraParameters, Map<String, String> templates);

}
