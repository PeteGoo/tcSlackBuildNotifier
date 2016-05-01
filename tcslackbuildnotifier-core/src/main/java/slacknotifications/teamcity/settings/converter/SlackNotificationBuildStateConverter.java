package slacknotifications.teamcity.settings.converter;

import slacknotifications.teamcity.BuildState;
import slacknotifications.teamcity.BuildStateEnum;

public class SlackNotificationBuildStateConverter {

	private SlackNotificationBuildStateConverter(){}
	
	public static BuildState convert(Integer oldState){
		BuildState newStates = new BuildState();
		
		newStates.setEnabled(BuildStateEnum.BUILD_STARTED, OldStyleBuildState.enabled(OldStyleBuildState.BUILD_STARTED, oldState));
		newStates.setEnabled(BuildStateEnum.BEFORE_BUILD_FINISHED, OldStyleBuildState.enabled(OldStyleBuildState.BEFORE_BUILD_FINISHED, oldState));
		newStates.setEnabled(BuildStateEnum.BUILD_INTERRUPTED, OldStyleBuildState.enabled(OldStyleBuildState.BUILD_INTERRUPTED, oldState));
		newStates.setEnabled(BuildStateEnum.RESPONSIBILITY_CHANGED, OldStyleBuildState.enabled(OldStyleBuildState.RESPONSIBILITY_CHANGED, oldState));

		// We don't support CHANGED_STATUS any more. It was too confusing.
		// newStates.setEnabled(BuildStateEnum.BUILD_CHANGED_STATUS, OldStyleBuildState.enabled(OldStyleBuildState.BUILD_CHANGED_STATUS, oldState));

		// BUILD_FINISHED has now been made more specific, so enable them all to start with, and users can turn them off.
		newStates.setEnabled(BuildStateEnum.BUILD_FINISHED, OldStyleBuildState.enabled(OldStyleBuildState.BUILD_FINISHED, oldState));
		newStates.setEnabled(BuildStateEnum.BUILD_SUCCESSFUL, OldStyleBuildState.enabled(OldStyleBuildState.BUILD_FINISHED, oldState));
		newStates.setEnabled(BuildStateEnum.BUILD_FAILED, OldStyleBuildState.enabled(OldStyleBuildState.BUILD_FINISHED, oldState));

		// If BUILD_CHANGED_STATUS was set, the user probably wanted to know iof the build went from broken to fixed.
		// Therefore, translate those into the right new settings.
		//newStates.setEnabled(BuildStateEnum.BUILD_BROKEN, 	OldStyleBuildState.enabled(OldStyleBuildState.BUILD_CHANGED_STATUS, oldState));
		//newStates.setEnabled(BuildStateEnum.BUILD_FIXED, 	OldStyleBuildState.enabled(OldStyleBuildState.BUILD_CHANGED_STATUS, oldState));
		
		return newStates;
	}
}
