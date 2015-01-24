package slacknotifications.teamcity.extension;

import org.junit.Before;
import org.junit.Test;
import slacknotifications.teamcity.BuildState;
import slacknotifications.teamcity.BuildStateEnum;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static slacknotifications.teamcity.extension.SlackNotificationAjaxEditPageController.*;

public class SlackNotificationAjaxEditPageControllerTest {
	
	HttpServletRequest requestSuccessOnAndFailureOn;
	HttpServletRequest requestSuccessOnAndFailureOff;
	HttpServletRequest requestSuccessOffAndFailureOn;
	HttpServletRequest requestSuccessOffAndFailureOff;
	BuildState states;
	
	@Before
	public void setup(){
		states = new BuildState();

		requestSuccessOnAndFailureOff = mock(HttpServletRequest.class);
		when(requestSuccessOnAndFailureOff.getParameter(BUILD_SUCCESSFUL)).thenReturn("on");
		
		requestSuccessOffAndFailureOn = mock(HttpServletRequest.class);
		when(requestSuccessOffAndFailureOn.getParameter(BUILD_FAILED)).thenReturn("on");
		
		requestSuccessOnAndFailureOn = mock(HttpServletRequest.class);
		when(requestSuccessOnAndFailureOn.getParameter(BUILD_SUCCESSFUL)).thenReturn("on");
		when(requestSuccessOnAndFailureOn.getParameter(BUILD_FAILED)).thenReturn("on");
		
		requestSuccessOffAndFailureOff = mock(HttpServletRequest.class);
	}

	@Test
	
	/** 
	 * The problem with the logic is that enabling the BUILD_SUCCESSFUL and BUILD_FAILED settings 
	 * also enable BUILD_FINISHED triggering. (See the logic in SlackNotificationAjexEditPageController#doHandle)
	 * 
	 * However, the last one wins, so we should really do an OR on it. (See next four tests)
	 */
	public void testCheckAndAddBuildState() {
		assertFalse(states.enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		checkAndAddBuildState(requestSuccessOnAndFailureOff, states, BuildStateEnum.BUILD_SUCCESSFUL, BUILD_SUCCESSFUL);
		assertTrue(states.enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_FINISHED));
		checkAndAddBuildState(requestSuccessOnAndFailureOff, states, BuildStateEnum.BUILD_FINISHED, BUILD_SUCCESSFUL);
		assertTrue(states.enabled(BuildStateEnum.BUILD_FINISHED));
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_FAILED));
		checkAndAddBuildState(requestSuccessOnAndFailureOff, states, BuildStateEnum.BUILD_FINISHED, BUILD_FAILED);
		assertFalse(states.enabled(BuildStateEnum.BUILD_FAILED));
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_FINISHED));
	}
	
	

	@Test
	public void testCheckAndAddBuildStateIfEitherSet01() {
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		checkAndAddBuildState(requestSuccessOnAndFailureOff, states, BuildStateEnum.BUILD_SUCCESSFUL, BUILD_SUCCESSFUL);
		assertTrue(states.enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_FINISHED));
		checkAndAddBuildState(requestSuccessOnAndFailureOff, states, BuildStateEnum.BUILD_FINISHED, BUILD_SUCCESSFUL);
		assertTrue(states.enabled(BuildStateEnum.BUILD_FINISHED));
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_FAILED));
		checkAndAddBuildState(requestSuccessOnAndFailureOff, states, BuildStateEnum.BUILD_FINISHED, BUILD_FAILED);
		assertFalse(states.enabled(BuildStateEnum.BUILD_FAILED));

		/* Use checkAndAddBuildStateIfEitherSet so that either one or the other need to be set, not the last one */
		
		checkAndAddBuildStateIfEitherSet(requestSuccessOnAndFailureOff, states, BuildStateEnum.BUILD_FINISHED, BUILD_SUCCESSFUL, BUILD_FAILED);
		assertTrue(states.enabled(BuildStateEnum.BUILD_FINISHED));
	}
	
	@Test
	public void testCheckAndAddBuildStateIfEitherSet02() {
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		checkAndAddBuildState(requestSuccessOffAndFailureOn, states, BuildStateEnum.BUILD_SUCCESSFUL, BUILD_SUCCESSFUL);
		assertFalse(states.enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_FAILED));
		checkAndAddBuildState(requestSuccessOffAndFailureOn, states, BuildStateEnum.BUILD_FAILED, BUILD_FAILED);
		assertTrue(states.enabled(BuildStateEnum.BUILD_FAILED));
		
		/* Use checkAndAddBuildStateIfEitherSet so that either one or the other need to be set, not the last one */
		
		checkAndAddBuildStateIfEitherSet(requestSuccessOffAndFailureOn, states, BuildStateEnum.BUILD_FINISHED, BUILD_SUCCESSFUL, BUILD_FAILED);
		assertTrue(states.enabled(BuildStateEnum.BUILD_FINISHED));
	}

	@Test
	public void testCheckAndAddBuildStateIfEitherSet03() {
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		checkAndAddBuildState(requestSuccessOffAndFailureOff, states, BuildStateEnum.BUILD_SUCCESSFUL, BUILD_SUCCESSFUL);
		assertFalse(states.enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_FAILED));
		checkAndAddBuildState(requestSuccessOffAndFailureOff, states, BuildStateEnum.BUILD_FAILED, BUILD_FAILED);
		assertFalse(states.enabled(BuildStateEnum.BUILD_FAILED));
		
		/* Use checkAndAddBuildStateIfEitherSet so that either one or the other need to be set, not the last one */
		
		checkAndAddBuildStateIfEitherSet(requestSuccessOffAndFailureOff, states, BuildStateEnum.BUILD_FINISHED, BUILD_SUCCESSFUL, BUILD_FAILED);
		assertFalse(states.enabled(BuildStateEnum.BUILD_FINISHED));
	}
	
	@Test
	public void testCheckAndAddBuildStateIfEitherSet04() {
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		checkAndAddBuildState(requestSuccessOnAndFailureOn, states, BuildStateEnum.BUILD_SUCCESSFUL, BUILD_SUCCESSFUL);
		assertTrue(states.enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		
		assertFalse(states.enabled(BuildStateEnum.BUILD_FAILED));
		checkAndAddBuildState(requestSuccessOnAndFailureOn, states, BuildStateEnum.BUILD_FAILED, BUILD_FAILED);
		assertTrue(states.enabled(BuildStateEnum.BUILD_FAILED));
		
		/* Use checkAndAddBuildStateIfEitherSet so that either one or the other need to be set, not the last one */
		
		checkAndAddBuildStateIfEitherSet(requestSuccessOnAndFailureOn, states, BuildStateEnum.BUILD_FINISHED, BUILD_SUCCESSFUL, BUILD_FAILED);
		assertTrue(states.enabled(BuildStateEnum.BUILD_FINISHED));
	}
	
}
