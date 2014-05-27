package slacknotifications.teamcity.settings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import jetbrains.buildServer.serverSide.SBuildType;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import slacknotifications.teamcity.MockSBuildType;
import slacknotifications.testframework.util.ConfigLoaderUtil;

public class SlackNotificationConfigTestForBuildTypeSpecifics {

	SlackNotificationConfig slacknotificationAllBuilds;
	SlackNotificationConfig slacknotificationSpecificBuilds;
	SBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
	SBuildType sBuildType02 = new MockSBuildType("Test Build", "A Test Build", "bt2");
	SBuildType sBuildType03 = new MockSBuildType("Test Build", "A Test Build", "bt3");
	
	
	@Before
	public void setup() throws JDOMException, IOException{
		slacknotificationSpecificBuilds  = ConfigLoaderUtil.getFirstSlackNotificationInConfig(new File("src/test/resources/project-settings-test-all-states-enabled-with-specific-builds.xml"));
		slacknotificationAllBuilds  = ConfigLoaderUtil.getFirstSlackNotificationInConfig(new File("src/test/resources/project-settings-test-all-states-enabled.xml"));
	}

	@Test
	public void testGetBuildTypeEnabled() {
		assertTrue(slacknotificationSpecificBuilds.isEnabledForBuildType(sBuildType));
		assertTrue(slacknotificationSpecificBuilds.isEnabledForBuildType(sBuildType02));
		assertFalse(slacknotificationSpecificBuilds.isEnabledForBuildType(sBuildType03));
		
		assertTrue(slacknotificationAllBuilds.isEnabledForBuildType(sBuildType));
		assertTrue(slacknotificationAllBuilds.isEnabledForBuildType(sBuildType02));
		assertTrue(slacknotificationAllBuilds.isEnabledForBuildType(sBuildType03));
	}
	
	@Test
	public void testGetAsElementSpecific() {
		Element e = slacknotificationSpecificBuilds.getAsElement();
		SlackNotificationConfig whc = new SlackNotificationConfig(e);
		assertTrue(whc.isEnabledForBuildType(sBuildType));
		assertTrue(whc.isEnabledForBuildType(sBuildType02));
		assertFalse(whc.isEnabledForBuildType(sBuildType03));
	}

	@Test
	public void testGetAsElementAll() {

		Element e = slacknotificationAllBuilds.getAsElement();
		SlackNotificationConfig whc = new SlackNotificationConfig(e);
		assertTrue(whc.isEnabledForBuildType(sBuildType));
		assertTrue(whc.isEnabledForBuildType(sBuildType02));
		assertTrue(whc.isEnabledForBuildType(sBuildType03));
	}
}
