package slacknotifications.teamcity.extension;


import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationImpl;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.payload.content.Commit;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;
import slacknotifications.teamcity.settings.SlackNotificationMainConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SlackNotifierSettingsController extends BaseController {

    private static final String CONTROLLER_PATH = "/slackNotifier/adminSettings.html";
    public static final String EDIT_PARAMETER = "edit";
    public static final String TEST_PARAMETER = "test";
    private static final Object ACTION_ENABLE = "enable";
    private static final String ACTION_PARAMETER = "action";


    private SBuildServer server;
    private ServerPaths serverPaths;
    private WebControllerManager manager;
    private SlackNotificationMainConfig config;
    private SlackNotificationPayloadManager payloadManager;
    private PluginDescriptor descriptor;

    public SlackNotifierSettingsController(@NotNull SBuildServer server,
                                           @NotNull ServerPaths serverPaths,
                                           @NotNull WebControllerManager manager,
                                           @NotNull SlackNotificationMainConfig config,
                                           SlackNotificationPayloadManager payloadManager,
                                           PluginDescriptor descriptor){

        this.server = server;
        this.serverPaths = serverPaths;
        this.manager = manager;
        this.config = config;
        this.payloadManager = payloadManager;
        this.descriptor = descriptor;

        manager.registerController(CONTROLLER_PATH, this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();

        if(request.getParameter(EDIT_PARAMETER) != null){
            logger.debug("Updating configuration");
            params = this.handleConfigurationChange(request);
        }
        else if(request.getParameter(TEST_PARAMETER) != null){
            logger.debug("Sending test notification");
            params = this.handleTestNotification(request);
        } else if (request.getParameter(ACTION_PARAMETER) != null) {
            logger.debug("Changing plugin status");
            this.handlePluginStatusChange(request);
        }
        return new ModelAndView(descriptor.getPluginResourcesPath() + "SlackNotification/ajaxEdit.jsp", params);
    }

    private void handlePluginStatusChange(HttpServletRequest request) {
        logger.debug("Changing status");
        Boolean disabled = !request.getParameter(ACTION_PARAMETER).equals(ACTION_ENABLE);
        logger.debug(String.format("Disabled status: %s", disabled));
        this.config.setEnabled(!disabled);
        this.config.save();
    }

    private HashMap<String, Object> handleTestNotification(HttpServletRequest request) throws IOException, SlackConfigValidationException {
        String teamName = request.getParameter("teamName");
        String token = request.getParameter("token");
        String botName = request.getParameter("botName");
        String iconUrl = request.getParameter("iconUrl");
        String defaultChannel = request.getParameter("defaultChannel");
        String maxCommitsToDisplay = request.getParameter("maxCommitsToDisplay");
        String showBuildAgent = request.getParameter("showBuildAgent");
        String showCommits = request.getParameter("showCommits");
        String showCommitters = request.getParameter("showCommitters");
        String showElapsedBuildTime = request.getParameter("showElapsedBuildTime");
        String showFailureReason = request.getParameter("showFailureReason");
        String proxyHost = request.getParameter("proxyHost");
        String proxyPort = request.getParameter("proxyPort");
        String proxyUser = request.getParameter("proxyUser");
        String proxyPassword = request.getParameter("proxyPassword");

        HashMap<String, Object> params = new HashMap<String, Object>();

        Validate(teamName, token, botName, iconUrl, defaultChannel, maxCommitsToDisplay, showBuildAgent, proxyHost, proxyPort, proxyUser, proxyPassword);

        SlackNotification notification = createMockNotification(teamName, defaultChannel, botName,
                token, iconUrl, Integer.parseInt(maxCommitsToDisplay),
                Boolean.parseBoolean(showElapsedBuildTime), Boolean.parseBoolean(showBuildAgent),
                Boolean.parseBoolean(showCommits), Boolean.parseBoolean(showCommitters), Boolean.parseBoolean(showFailureReason),
                proxyHost, proxyPort, proxyUser, proxyPassword);



        notification.post();

        this.getOrCreateMessages(request).addMessage("notificationSent", "The notification has been sent");

        params.put("messages", "Sent");


        return params;
    }

    private void Validate(String teamName, String token, String botName, String iconUrl, String defaultChannel
            , String maxCommitsToDisplay, String showBuildAgent, String proxyHost, String proxyPort, String proxyUser, String proxyPassword) throws SlackConfigValidationException {
        if(teamName == null || StringUtil.isEmpty(teamName)
                || token == null || StringUtil.isEmpty(token)
                || botName == null || StringUtil.isEmpty(botName)
                || iconUrl == null || StringUtil.isEmpty(iconUrl)
                || defaultChannel == null || StringUtil.isEmpty(defaultChannel)
                || (showBuildAgent.toLowerCase() == "false" && (maxCommitsToDisplay == null || StringUtil.isEmpty(maxCommitsToDisplay)))
                || tryParseInt(maxCommitsToDisplay) == null
                || (!isNullOrEmpty(proxyHost) && isNullOrEmpty(proxyPort))
                || (!isNullOrEmpty(proxyUser) && isNullOrEmpty(proxyPassword))
                || (!isNullOrEmpty(proxyPort) && tryParseInt(proxyPort) == null)
                ){

            throw new SlackConfigValidationException("Could not validate parameters. Please recheck the request.");
        }
    }

    private boolean isNullOrEmpty(String str){
        return str == null || StringUtil.isEmpty(str);
    }

    public SlackNotification createMockNotification(String teamName, String defaultChannel, String botName,
                                                    String token, String iconUrl, Integer maxCommitsToDisplay,
                                                    Boolean showElapsedBuildTime, Boolean showBuildAgent, Boolean showCommits,
                                                    Boolean showCommitters, Boolean showFailureReason, String proxyHost,
                                                    String proxyPort, String proxyUser, String proxyPassword) {
        SlackNotification notification = new SlackNotificationImpl(defaultChannel);
        notification.setTeamName(teamName);
        notification.setBotName(botName);
        notification.setToken(token);
        notification.setIconUrl(iconUrl);
        notification.setMaxCommitsToDisplay(maxCommitsToDisplay);
        notification.setShowElapsedBuildTime(showElapsedBuildTime);
        notification.setShowBuildAgent(showBuildAgent);
        notification.setShowCommits(showCommits);
        notification.setShowCommitters(showCommitters);
        notification.setShowFailureReason(showFailureReason);

        if(proxyHost != null && !StringUtil.isEmpty(proxyHost)){
            Credentials creds = null;
            if(proxyUser != null && !StringUtil.isEmpty(proxyUser)){
                creds = new UsernamePasswordCredentials(proxyUser, proxyPassword);
            }
            notification.setProxy(proxyHost, Integer.parseInt(proxyPort), creds);
        }

        SlackNotificationPayloadContent payload = new SlackNotificationPayloadContent();
        payload.setAgentName("Build Agent 1");

        payload.setBranchDisplayName("master");
        payload.setBranchIsDefault(true);
        payload.setBuildDescriptionWithLinkSyntax(String.format("<http://buildserver/builds/|Failed - My Awesome Build #5>"));
        payload.setBuildFullName("The Awesome Build");
        payload.setBuildId("b123");
        payload.setBuildName("My Awesome Build");
        payload.setBuildResult("Failed");
        payload.setBuildStatusUrl("http://buildserver/builds");
        payload.setBuildTypeId("b123");
        payload.setColor("danger");
        List<Commit> commits = new ArrayList<Commit>();
        commits.add(new Commit("a5bdc78", "Bug fix for that awful thing", "jbloggs", "jbloggs"));
        commits.add(new Commit("cc4500d", "New feature that rocks", "bbrave", "bbrave"));
        commits.add(new Commit("abb23b4", "Merge of branch xyz", "ddruff", "ddruff"));
        payload.setCommits(commits);
        payload.setElapsedTime(13452);
        payload.setFirstFailedBuild(true);
        payload.setIsComplete(true);
        payload.setText("My Awesome build");
        notification.setPayload(payload);
        notification.setEnabled(true);

        return notification;
    }

    public Integer tryParseInt(String str) {
        Integer retVal;
        try {
            retVal = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            retVal = null; // or null if that is your preference
        }
        return retVal;
    }

    private HashMap<String, Object> handleConfigurationChange(HttpServletRequest request) throws IOException, SlackConfigValidationException {
        String teamName = request.getParameter("teamName");
        String token = request.getParameter("token");
        String botName = request.getParameter("botName");
        String iconUrl = request.getParameter("iconUrl");
        String defaultChannel = request.getParameter("defaultChannel");
        String maxCommitsToDisplay = request.getParameter("maxCommitsToDisplay");
        String showBuildAgent = request.getParameter("showBuildAgent");
        String showCommits = request.getParameter("showCommits");
        String showCommitters = request.getParameter("showCommitters");
        String showElapsedBuildTime = request.getParameter("showElapsedBuildTime");
        String showFailureReason = request.getParameter("showFailureReason");
        String proxyHost = request.getParameter("proxyHost");
        String proxyPort = request.getParameter("proxyPort");
        String proxyUser = request.getParameter("proxyUser");
        String proxyPassword = request.getParameter("proxyPassword");

        if(!isNullOrEmpty(proxyPassword)){
            proxyPassword = RSACipher.decryptWebRequestData(proxyPassword);
        }

        Validate(teamName, token, botName, iconUrl, defaultChannel, maxCommitsToDisplay, showBuildAgent, proxyHost, proxyPort, proxyUser, proxyPassword);

        this.config.setTeamName(teamName);
        this.config.setToken(token);
        this.config.getContent().setBotName(botName);
        this.config.getContent().setIconUrl(iconUrl);
        this.config.setDefaultChannel(defaultChannel);
        this.config.getContent().setMaxCommitsToDisplay(Integer.parseInt(maxCommitsToDisplay));
        this.config.getContent().setShowBuildAgent(Boolean.parseBoolean(showBuildAgent));
        this.config.getContent().setShowCommits(Boolean.parseBoolean(showCommits));
        this.config.getContent().setShowCommitters(Boolean.parseBoolean(showCommitters));
        this.config.getContent().setShowElapsedBuildTime((Boolean.parseBoolean(showElapsedBuildTime)));
        this.config.getContent().setShowFailureReason((Boolean.parseBoolean(showFailureReason)));


        this.config.setProxyHost(proxyHost);
        this.config.setProxyPort(isNullOrEmpty(proxyPort) ? null : Integer.parseInt(proxyPort));
        this.config.setProxyUsername(proxyUser);
        this.config.setProxyPassword(proxyPassword);


        this.config.save();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("message", "Saved");
        return params;
    }

    public class SlackConfigValidationException extends Exception {
        public SlackConfigValidationException(String message) {
            super(message);
        }
    }
}
