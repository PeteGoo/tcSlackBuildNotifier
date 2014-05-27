package slacknotifications.teamcity.settings.converter;

import static org.junit.Assert.*;

import org.junit.Test;

import slacknotifications.teamcity.BuildState;
import slacknotifications.teamcity.BuildStateEnum;

public class SlackNotificationBuildStateConverterTest {

	@Test
	public void testConvert_01() {
		// ALL_ENABLED should not be everything anymore. We are deliberately not including
		// Broken/Fixed as that is not technically every build.
		BuildState state = SlackNotificationBuildStateConverter.convert(OldStyleBuildState.ALL_ENABLED);
		assertTrue(state.allEnabled());
	}
	
	@Test
	public void testConvert_02() {
		// Test all 1's except CHANGED_STATUS, since that would  need broken or fixed set
		BuildState state = SlackNotificationBuildStateConverter.convert(Integer.parseInt("11111011",2));
		assertTrue(state.allEnabled());
	}
	
	
	/*
	 *     
		public static final Integer BUILD_STARTED  			= Integer.parseInt("00000001",2);
	    public static final Integer BUILD_FINISHED 			= Integer.parseInt("00000010",2);
	    public static final Integer BUILD_CHANGED_STATUS 	= Integer.parseInt("00000100",2);
	    public static final Integer BEFORE_BUILD_FINISHED 	= Integer.parseInt("00001000",2);
	    public static final Integer RESPONSIBILITY_CHANGED 	= Integer.parseInt("00010000",2);
	    public static final Integer BUILD_INTERRUPTED 		= Integer.parseInt("00100000",2);
	    
	    public static final Integer ALL_ENABLED				= Integer.parseInt("11111111",2);
	 *
	 */
	
	@Test
	public void testConvert_03() {
		assertTrue(SlackNotificationBuildStateConverter.convert(OldStyleBuildState.BUILD_STARTED).enabled(BuildStateEnum.BUILD_STARTED));
		assertTrue(SlackNotificationBuildStateConverter.convert(OldStyleBuildState.BUILD_FINISHED).enabled(BuildStateEnum.BUILD_FINISHED));
		assertTrue(SlackNotificationBuildStateConverter.convert(OldStyleBuildState.BUILD_FINISHED).enabled(BuildStateEnum.BUILD_SUCCESSFUL));
		assertTrue(SlackNotificationBuildStateConverter.convert(OldStyleBuildState.BUILD_FINISHED).enabled(BuildStateEnum.BUILD_FAILED));
		assertTrue(SlackNotificationBuildStateConverter.convert(OldStyleBuildState.BEFORE_BUILD_FINISHED).enabled(BuildStateEnum.BEFORE_BUILD_FINISHED));
		assertTrue(SlackNotificationBuildStateConverter.convert(OldStyleBuildState.RESPONSIBILITY_CHANGED).enabled(BuildStateEnum.RESPONSIBILITY_CHANGED));
		assertTrue(SlackNotificationBuildStateConverter.convert(OldStyleBuildState.BUILD_INTERRUPTED).enabled(BuildStateEnum.BUILD_INTERRUPTED));
	}

}
