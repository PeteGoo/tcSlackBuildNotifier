package slacknotifications.teamcity.extension;


import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.junit.Ignore;
import org.junit.Test;
import slacknotifications.SlackNotification;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.settings.SlackNotificationMainConfig;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SlackNotificationSettingsControllerTest {
    SBuildServer sBuildServer = mock(SBuildServer.class);
    WebControllerManager webControllerManager = mock(WebControllerManager.class);

    @Test
    @Ignore
    public void createMockNotification_constructsValidNotification(){
        String expectedConfigDirectory = ".";
        ServerPaths serverPaths = mock(ServerPaths.class);
        when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);



        SlackNotificationMainConfig config = new SlackNotificationMainConfig(serverPaths);

        SlackNotificationPayloadManager payloadManager = new SlackNotificationPayloadManager(sBuildServer);
        SlackNotifierSettingsController controller = new SlackNotifierSettingsController(
                sBuildServer, serverPaths, webControllerManager,
                config, payloadManager);

        SlackNotification notification = controller.createMockNotification("the team", "#general", "The Bot", "tokenthingy",
                SlackNotificationMainConfig.DEFAULT_ICONURL, 5, true, true, true, true);
    }
}
