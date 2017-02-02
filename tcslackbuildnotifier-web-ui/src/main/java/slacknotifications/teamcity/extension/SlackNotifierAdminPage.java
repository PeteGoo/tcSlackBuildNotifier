package slacknotifications.teamcity.extension;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import org.jetbrains.annotations.NotNull;
import slacknotifications.SlackNotificationProxyConfig;
import slacknotifications.teamcity.Loggers;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Peter on 24/01/2015.
 */
public class SlackNotifierAdminPage extends AdminPage {


    private static final String AFTER_PAGE_ID = "jabber";
    private static final String BEFORE_PAGE_ID = "clouds";
    private static final String PAGE = "SlackNotification/slackAdminSettings.jsp";
    private static final String PLUGIN_NAME = "slackNotifications";
    private static final String TAB_TITLE = "Slack Notifications";
    private final String jspHome;
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
        jspHome = descriptor.getPluginResourcesPath();
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
        slackMainSettings.refresh();
        model.put("teamName", this.slackMainSettings.getTeamName());
        model.put("token", this.slackMainSettings.getToken());
        model.put("botName", this.slackMainSettings.getBotName());
        model.put("iconUrl", this.slackMainSettings.getIconUrl());
        model.put("defaultChannel", this.slackMainSettings.getDefaultChannel());
        model.put("branchName", this.slackMainSettings.getBranchName());
        model.put("maxCommitsToDisplay", this.slackMainSettings.getMaxCommitsToDisplay());
        model.put("showBuildAgent", this.slackMainSettings.getShowBuildAgent());
        model.put("showCommits", this.slackMainSettings.getShowCommits());
        model.put("showCommitters", this.slackMainSettings.getShowCommitters());
        model.put("showTriggeredBy", this.slackMainSettings.getShowTriggeredBy());
        model.put("showElapsedBuildTime", this.slackMainSettings.getShowElapsedBuildTime());
        model.put("showFailureReason", this.slackMainSettings.getShowFailureReason());

        SlackNotificationProxyConfig proxyConfig = this.slackMainSettings.getProxyConfig();
        model.put("proxyHost", proxyConfig.getProxyHost());
        model.put("proxyPort", proxyConfig.getProxyPort());
        model.put("proxyUser", proxyConfig.getCreds() == null ? null : proxyConfig.getCreds().getUserPrincipal().getName());
        model.put("proxyPassword", proxyConfig.getCreds() == null ? null : proxyConfig.getCreds().getPassword());
        model.put("encryptedProxyPassword", proxyConfig.getCreds() == null || proxyConfig.getCreds().getPassword() == null ? null : RSACipher.encryptDataForWeb(proxyConfig.getCreds().getPassword()));
        model.put("hexEncodedPublicKey", RSACipher.getHexEncodedPublicKey());

        try {
            model.put("pluginVersion", this.slackMainSettings.getPluginVersion());
        } catch (IOException e) {
            Loggers.ACTIVITIES.error("Could not retrieve slack plugin version", e);
        }

        model.put("disabled", !this.slackMainSettings.getEnabled());
        model.put("jspHome", this.jspHome);
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
