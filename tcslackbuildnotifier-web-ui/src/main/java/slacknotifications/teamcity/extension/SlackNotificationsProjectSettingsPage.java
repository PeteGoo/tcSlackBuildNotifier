package slacknotifications.teamcity.extension;

import jetbrains.buildServer.web.openapi.*;
import org.jetbrains.annotations.NotNull;
import slacknotifications.teamcity.Loggers;

import java.util.ArrayList;

public class SlackNotificationsProjectSettingsPage extends SimpleCustomTab {

    private static final String PAGE = "SlackNotification/slackProjectSettings.jsp";
    private static final String PLUGIN_NAME = "slackNotifications";
    private static final String TAB_TITLE = "Slack Settings";
    private final String jspHome;

    public SlackNotificationsProjectSettingsPage(PagePlaces pagePlaces,
                                                 @NotNull PluginDescriptor descriptor) {
        super(pagePlaces);

        setPlaceId(PlaceId.EDIT_PROJECT_PAGE_TAB);
        setPluginName(PLUGIN_NAME);
        setIncludeUrl(descriptor.getPluginResourcesPath(PAGE));
        jspHome = descriptor.getPluginResourcesPath();
        setTabTitle(TAB_TITLE);
        register();
        Loggers.SERVER.info("Slack project configuration page registered");
    }



}
