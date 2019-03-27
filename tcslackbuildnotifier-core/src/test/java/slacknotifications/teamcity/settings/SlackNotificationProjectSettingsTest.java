package slacknotifications.teamcity.settings;


import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import slacknotifications.teamcity.BuildState;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SlackNotificationProjectSettingsTest {
	ProjectSettingsManager psm = mock(ProjectSettingsManager.class);
	
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void TestFactory(){
		SlackNotificationProjectSettingsFactory psf = new SlackNotificationProjectSettingsFactory(psm);
		psf.createProjectSettings("project1");
	}

	@Test
	public void filterBranchName_CanBeSet(){
		SlackNotificationProjectSettingsFactory psf = new SlackNotificationProjectSettingsFactory(psm);
		SlackNotificationProjectSettings settings = psf.createProjectSettings("project1");
		BuildState state = new BuildState().setAllEnabled();
		settings.addNewSlackNotification("1", "1", "#general", "Steve",
				"master", false, state, false, false,
				new HashSet<String>(), false, false);
		List<SlackNotificationConfig> listOfConfigs = settings.getSlackNotificationsConfigs();
		for (SlackNotificationConfig config : listOfConfigs) {
			assertTrue(config.getFilterBranchName() == "master");
		}
	}

}
