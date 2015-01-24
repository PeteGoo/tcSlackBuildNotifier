package slacknotifications.teamcity.settings;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import slacknotifications.testframework.util.ConfigLoaderUtil;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SlackNotificationConfigTest {
	
	private static final String EMPTY_STRING = "";
	private static final String CHECKED = "checked ";
	SlackNotificationConfig slacknotificationAllEnabled;
	SlackNotificationConfig slacknotificationAllDisabled;
	SlackNotificationConfig slacknotificationDisabled;
	SlackNotificationConfig slacknotificationMostEnabled;
	
	
	@Before
	public void setup() throws JDOMException, IOException{
		
		slacknotificationAllEnabled  = ConfigLoaderUtil.getFirstSlackNotificationInConfig(new File("src/test/resources/project-settings-test-all-states-enabled.xml"));
		slacknotificationAllDisabled = ConfigLoaderUtil.getFirstSlackNotificationInConfig(new File("src/test/resources/project-settings-test-all-states-disabled.xml"));
		slacknotificationDisabled    = ConfigLoaderUtil.getFirstSlackNotificationInConfig(new File("src/test/resources/project-settings-test-slacknotifications-disabled.xml"));
		slacknotificationMostEnabled = ConfigLoaderUtil.getFirstSlackNotificationInConfig(new File("src/test/resources/project-settings-test-all-but-respchange-states-enabled.xml"));
	}
	
//	private SlackNotificationConfig getFirstSlackNotificationInConfig(File f) throws JDOMException, IOException{
//		Element fileAsElement = ConfigLoaderUtil.getFullConfigElement(f);
//		assertTrue("One and only one slacknotifications expected when loading test config from file : " + f.getName(), fileAsElement.getChild("slackNotifications").getChildren("slacknotifications").size() == 1);
//		return new SlackNotificationConfig((Element) fileAsElement.getChild("slackNotifications").getChildren("slacknotifications").get(0));
//	}

	@Test
	public void testGetEnabled() {
		assertTrue(slacknotificationAllEnabled.getEnabled());
		assertTrue(slacknotificationAllDisabled.getEnabled());
		assertFalse(slacknotificationDisabled.getEnabled());
	}

	@Test
	public void testSetEnabled() {
		assertTrue(slacknotificationAllEnabled.getEnabled());
		slacknotificationAllEnabled.setEnabled(false);
		assertFalse(slacknotificationAllEnabled.getEnabled());
	}

	@Test
	public void testGetBuildStates() {
		assertTrue(slacknotificationAllEnabled.getBuildStates().allEnabled());
		assertFalse(slacknotificationAllDisabled.getBuildStates().allEnabled());
		assertFalse(slacknotificationDisabled.getBuildStates().allEnabled());
	}

	@Test
	public void testGetUrl() {
		assertTrue(slacknotificationAllEnabled.getChannel().equals("http://localhost/test"));
	}

	@Test
	public void testSetUrl() {
		assertTrue(slacknotificationAllEnabled.getChannel().equals("http://localhost/test"));
		slacknotificationAllEnabled.setChannel("a new url");
		assertFalse(slacknotificationAllEnabled.getChannel().equals("http://localhost/test"));
		assertTrue(slacknotificationAllEnabled.getChannel().equals("a new url"));
		
	}

	@Test
	public void testGetUniqueKey() {
		assertFalse(slacknotificationAllEnabled.getUniqueKey().equals(EMPTY_STRING));
	}

	@Test
	public void testSetUniqueKey() {
		String s = slacknotificationAllEnabled.getUniqueKey();
		slacknotificationAllEnabled.setUniqueKey("SomethingElse");
		assertFalse(slacknotificationAllEnabled.getUniqueKey().equals(s));
		assertTrue(slacknotificationAllEnabled.getUniqueKey().equals("SomethingElse"));
	}

	@Test
	public void testGetEnabledListAsString() {
		assertTrue(slacknotificationAllEnabled.getEnabledListAsString().equals("All Build Events"));
		assertTrue(slacknotificationAllDisabled.getEnabledListAsString().equals("None"));
		assertTrue(slacknotificationMostEnabled.getEnabledListAsString().equals(" Build Started, Build Interrupted, Build Almost Completed, Build Failed, Build Successful"));
	}

	@Test
	public void testGetSlackNotificationEnabledAsChecked() {
		assertTrue(slacknotificationAllEnabled.getSlackNotificationEnabledAsChecked().equals(CHECKED));
		assertTrue(slacknotificationAllDisabled.getSlackNotificationEnabledAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateAllAsChecked() {
		assertTrue(slacknotificationAllEnabled.getStateAllAsChecked().equals(CHECKED));
		assertFalse(slacknotificationAllDisabled.getStateAllAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildStartedAsChecked() {
		assertTrue(slacknotificationAllEnabled.getStateBuildStartedAsChecked().equals(CHECKED));
		assertFalse(slacknotificationAllDisabled.getStateBuildStartedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildFinishedAsChecked() {
		assertTrue(slacknotificationAllEnabled.getStateBeforeFinishedAsChecked().equals(CHECKED));
		assertFalse(slacknotificationAllDisabled.getStateBeforeFinishedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBeforeFinishedAsChecked() {
		assertTrue(slacknotificationAllEnabled.getStateBeforeFinishedAsChecked().equals(CHECKED));
		assertFalse(slacknotificationAllDisabled.getStateBeforeFinishedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateResponsibilityChangedAsChecked() {
		assertTrue(slacknotificationAllEnabled.getStateResponsibilityChangedAsChecked().equals(CHECKED));
		assertFalse(slacknotificationAllDisabled.getStateResponsibilityChangedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildInterruptedAsChecked() {
		assertTrue(slacknotificationAllEnabled.getStateBuildInterruptedAsChecked().equals(CHECKED));
		assertFalse(slacknotificationAllDisabled.getStateBuildInterruptedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildSuccessfulAsChecked() {
		assertTrue(slacknotificationAllEnabled.getStateBuildInterruptedAsChecked().equals(CHECKED));
		assertFalse(slacknotificationAllDisabled.getStateBuildInterruptedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildFixedAsChecked() {
		assertFalse(slacknotificationAllEnabled.getStateBuildFixedAsChecked().equals(CHECKED));
		assertFalse(slacknotificationAllDisabled.getStateBuildFixedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildFailedAsChecked() {
		assertTrue(slacknotificationAllEnabled.getStateBuildFailedAsChecked().equals(CHECKED));
		assertFalse(slacknotificationAllDisabled.getStateBuildFailedAsChecked().equals(CHECKED));
	}

	@Test
	public void testGetStateBuildBrokenAsChecked() {
		assertFalse(slacknotificationAllEnabled.getStateBuildBrokenAsChecked().equals(CHECKED));
		assertFalse(slacknotificationAllDisabled.getStateBuildBrokenAsChecked().equals(CHECKED));
	}


}
