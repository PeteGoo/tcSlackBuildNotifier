package slacknotifications.teamcity.extension;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import org.jetbrains.annotations.NotNull;
import slacknotifications.teamcity.Loggers;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Peter on 24/01/2015.
 */
public class SlackNotifierAdminPage extends AdminPage {


    private static final String AFTER_PAGE_ID = "jabber";
    private static final String BEFORE_PAGE_ID = "clouds";
    private static final String PAGE = "SlackNotification/slackAdminSettings.jsp";
    private static final String PLUGIN_NAME = "slackNotifications";
    private static final String TAB_TITLE = "Slack Notifications";
    private SBuildServer sBuildServer;
    private SlackNotificationMainSettings slackMainSettings;

    protected SlackNotifierAdminPage(@NotNull PagePlaces pagePlaces,
                                     @NotNull PluginDescriptor descriptor,
                                     @NotNull SBuildServer sBuildServer,
                                     @NotNull SlackNotificationMainSettings slackMainSettings
                                     ) {
        super(pagePlaces);
        this.sBuildServer = sBuildServer;
        this.slackMainSettings = slackMainSettings;
        setPluginName(PLUGIN_NAME);
        setIncludeUrl(descriptor.getPluginResourcesPath(PAGE));
        setTabTitle(TAB_TITLE);
        ArrayList<String> after = new ArrayList<String>();
        after.add(AFTER_PAGE_ID);
        ArrayList<String> before = new ArrayList<String>();
        before.add(BEFORE_PAGE_ID);
        setPosition(PositionConstraint.between(after, before));
        register();
        Loggers.SERVER.info("Slack global configuration page registered");
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request){
        super.fillModel(model, request);

    }

    @NotNull
    @Override
    public String getGroup() {
        return SERVER_RELATED_GROUP;
    }

    @Override
    public boolean isAvailable(@NotNull HttpServletRequest request) {
        return super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
    }
}
