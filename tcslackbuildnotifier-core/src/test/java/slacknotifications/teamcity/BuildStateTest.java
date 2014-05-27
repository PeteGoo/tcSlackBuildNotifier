package slacknotifications.teamcity;

import static org.junit.Assert.*;
import static slacknotifications.teamcity.BuildStateEnum.*;

import org.junit.Test;

public class BuildStateTest {

	@Test
	public void testBuildState() {
		BuildState state = new BuildState();
		assertFalse(state.enabled(BuildStateEnum.BEFORE_BUILD_FINISHED));
	}

	@Test
	public void testEnabled() {
		BuildState state = new BuildState();
		state.enable(BuildStateEnum.BUILD_STARTED);
		assertFalse(state.enabled(BuildStateEnum.BEFORE_BUILD_FINISHED));
		assertTrue(state.enabled(BuildStateEnum.BUILD_STARTED));
	}

	@Test
	public void testGetShortName() {
		BuildState state = new BuildState();
		String shortname1 = BuildStateEnum.BUILD_SUCCESSFUL.getShortName();
		String shortname2 = state.getShortName(BuildStateEnum.BUILD_SUCCESSFUL);
		assertTrue(shortname1.equals(shortname2));
	}

	@Test
	public void testGetDescriptionSuffix() {
		BuildState state = new BuildState();
		String suffix1 = BuildStateEnum.BUILD_SUCCESSFUL.getDescriptionSuffix();
		String suffix2 = state.getDescriptionSuffix(BuildStateEnum.BUILD_SUCCESSFUL);
		assertTrue(suffix1.equals(suffix2));
	}

	@Test
	public void testAllEanbled_01(){
		BuildState state = new BuildState();
		assertFalse(state.allEnabled());
	}
	
	@Test
	public void testAllEnabled_02(){
		BuildState state = new BuildState();
		state.enable(BUILD_STARTED);
		state.enable(BUILD_INTERRUPTED);
		state.enable(RESPONSIBILITY_CHANGED);
		state.enable(BEFORE_BUILD_FINISHED);
		state.enable(BUILD_FINISHED);
		state.enable(BUILD_FAILED);
		state.enable(BUILD_SUCCESSFUL);
		assertTrue(state.allEnabled());
	}

	@Test
	public void testAllEanbled_03(){
		BuildState state = new BuildState();
		state.enable(BUILD_STARTED);
		state.enable(BUILD_INTERRUPTED);
		state.enable(BEFORE_BUILD_FINISHED);
		state.enable(BUILD_FINISHED);
		state.enable(BUILD_SUCCESSFUL);
		assertFalse(state.allEnabled());
	}
	
	@Test
	public void testAllEanbled_04(){
		BuildState state = new BuildState();
		state.enable(BUILD_STARTED);
		state.enable(BUILD_INTERRUPTED);
		state.enable(RESPONSIBILITY_CHANGED);
		state.enable(BEFORE_BUILD_FINISHED);
		state.enable(BUILD_FINISHED);
		state.enable(BUILD_FAILED);
		state.enable(BUILD_SUCCESSFUL);
		state.enable(BUILD_BROKEN);
		assertFalse(state.allEnabled());
	}
	
	@Test
	public void testAllEanbled_05(){
		BuildState state = new BuildState();
		state.enable(BUILD_STARTED);
		state.enable(BUILD_INTERRUPTED);
		state.enable(RESPONSIBILITY_CHANGED);
		state.enable(BEFORE_BUILD_FINISHED);
		state.enable(BUILD_FINISHED);
		state.enable(BUILD_FAILED);
		state.enable(BUILD_SUCCESSFUL);
		state.enable(BUILD_FIXED);
		assertFalse(state.allEnabled());
	}
	
	@Test
	public void testAllEanbled_06(){
		BuildState state = new BuildState();
		state.enable(BUILD_STARTED);
		state.enable(BUILD_INTERRUPTED);
		state.enable(RESPONSIBILITY_CHANGED);
		state.enable(BEFORE_BUILD_FINISHED);
		state.enable(BUILD_FINISHED);
		state.enable(BUILD_FAILED);
		state.enable(BUILD_SUCCESSFUL);
		state.enable(BUILD_FIXED);
		state.enable(BUILD_BROKEN);
		assertFalse(state.allEnabled());
	}
	
	@Test
	public void testNoneEnabled_01(){
		BuildState state = new BuildState();
		state.enable(BUILD_STARTED);
		state.enable(BUILD_INTERRUPTED);
		state.enable(RESPONSIBILITY_CHANGED);
		state.enable(BEFORE_BUILD_FINISHED);
		state.enable(BUILD_FINISHED);
		state.enable(BUILD_FAILED);
		state.enable(BUILD_SUCCESSFUL);
		state.enable(BUILD_FIXED);
		state.enable(BUILD_BROKEN);
		assertFalse(state.noneEnabled());
	}

	@Test
	public void testNoneEnabled_02(){
		BuildState state = new BuildState();
		state.disable(BUILD_STARTED);
		state.disable(BUILD_INTERRUPTED);
		state.disable(RESPONSIBILITY_CHANGED);
		state.disable(BEFORE_BUILD_FINISHED);
		state.disable(BUILD_FINISHED);
		state.enable(BUILD_FAILED);
		state.enable(BUILD_SUCCESSFUL);
		state.enable(BUILD_FIXED);
		state.enable(BUILD_BROKEN);
		assertTrue(state.noneEnabled());
	}
	
	@Test
	public void testNoneEnabled_03(){
		BuildState state = new BuildState();
		state.disable(BUILD_STARTED);
		state.disable(BUILD_INTERRUPTED);
		state.disable(RESPONSIBILITY_CHANGED);
		state.disable(BEFORE_BUILD_FINISHED);
		state.disable(BUILD_FINISHED);
		state.disable(BUILD_FAILED);
		state.disable(BUILD_SUCCESSFUL);
		state.disable(BUILD_FIXED);
		state.disable(BUILD_BROKEN);
		assertTrue(state.noneEnabled());
	}
	
	@Test
	public void testNoneEnabled_04(){
		BuildState state = new BuildState();
		state.disable(BUILD_STARTED);
		state.disable(BUILD_INTERRUPTED);
		state.disable(RESPONSIBILITY_CHANGED);
		state.disable(BEFORE_BUILD_FINISHED);
		state.enable(BUILD_FINISHED);
		state.disable(BUILD_FAILED);
		state.disable(BUILD_SUCCESSFUL);
		state.disable(BUILD_FIXED);
		state.disable(BUILD_BROKEN);
		assertTrue(state.noneEnabled());
	}
	
	@Test
	public void testNoneEnabled_05(){
		BuildState state = new BuildState();
		state.disable(BUILD_STARTED);
		state.disable(BUILD_INTERRUPTED);
		state.disable(RESPONSIBILITY_CHANGED);
		state.disable(BEFORE_BUILD_FINISHED);
		state.enable(BUILD_FINISHED);
		state.enable(BUILD_FAILED);
		state.disable(BUILD_SUCCESSFUL);
		state.disable(BUILD_FIXED);
		state.disable(BUILD_BROKEN);
		assertFalse(state.noneEnabled());
	}

	@Test
	public void testSetAllEnabled(){
		BuildState state = new BuildState();
		assertTrue(state.noneEnabled());
		state.setAllEnabled();
		assertTrue(state.allEnabled());
	}
	
}
