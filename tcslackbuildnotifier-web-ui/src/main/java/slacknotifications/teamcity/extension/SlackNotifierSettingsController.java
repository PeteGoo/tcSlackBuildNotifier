package slacknotifications.teamcity.extension;


import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.web.openapi.WebControllerManager;
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
import java.util.List;


public class SlackNotifierSettingsController extends BaseController {

    private static final String CONTROLLER_PATH = "/slackNotifier/adminSettings.html";
    public static final String EDIT_PARAMETER = "edit";
    public static final String TEST_PARAMETER = "test";


    private SBuildServer server;
    private ServerPaths serverPaths;
    private WebControllerManager manager;
    private SlackNotificationMainConfig config;
    private SlackNotificationPayloadManager payloadManager;

    public SlackNotifierSettingsController(@NotNull SBuildServer server,
                                           @NotNull ServerPaths serverPaths,
                                           @NotNull WebControllerManager manager,
                                           @NotNull SlackNotificationMainConfig config,
                                           SlackNotificationPayloadManager payloadManager){

        this.server = server;
        this.serverPaths = serverPaths;
        this.manager = manager;
        this.config = config;
        this.payloadManager = payloadManager;

        manager.registerController(CONTROLLER_PATH, this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {

        if(request.getParameter(EDIT_PARAMETER) != null){
            logger.debug("Updating configuration");
            this.handleConfigurationChange(request);
        }
        else if(request.getParameter(TEST_PARAMETER) != null){
            logger.debug("Sending test notification");
            this.handleTestNotification(request);
        }
        return new ModelAndView();
    }

    private void handleTestNotification(HttpServletRequest request) throws IOException {
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

        if(teamName == null || StringUtil.isEmpty(teamName)
                || token == null || StringUtil.isEmpty(token)
                || botName == null || StringUtil.isEmpty(botName)
                || iconUrl == null || StringUtil.isEmpty(iconUrl)
                || defaultChannel == null || StringUtil.isEmpty(defaultChannel)
                || (showBuildAgent.toLowerCase() == "false" && (maxCommitsToDisplay == null || StringUtil.isEmpty(maxCommitsToDisplay)))
                || tryParse(maxCommitsToDisplay) == null
                ){

            SlackNotification notification = createMockNotification(teamName, defaultChannel, botName,
                    token, iconUrl, Integer.parseInt(maxCommitsToDisplay),
                    Boolean.parseBoolean(showElapsedBuildTime), Boolean.parseBoolean(showBuildAgent),
                    Boolean.parseBoolean(showCommits), Boolean.parseBoolean(showCommitters));



            notification.post();

            return;
        }
    }
    public SlackNotification createMockNotification(String teamName, String defaultChannel, String botName,
                                                    String token, String iconUrl, Integer maxCommitsToDisplay,
                                                    Boolean showElapsedBuildTime, Boolean showBuildAgent, Boolean showCommits,
                                                    Boolean showCommitters) {
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

        return notification;
    }

    public Integer tryParse(String str) {
        Integer retVal;
        try {
            retVal = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            retVal = null; // or null if that is your preference
        }
        return retVal;
    }

    private void handleConfigurationChange(HttpServletRequest request) throws IOException {
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

        this.config.setTeamName(teamName);
        this.config.setToken(token);
        this.config.setBotName(botName);
        this.config.setIconUrl(iconUrl);
        this.config.setDefaultChannel(defaultChannel);
        this.config.setMaxCommitsToDisplay(Integer.parseInt(maxCommitsToDisplay));
        this.config.setShowBuildAgent(Boolean.parseBoolean(showBuildAgent));
        this.config.setShowCommits(Boolean.parseBoolean(showCommits));
        this.config.setShowCommitters(Boolean.parseBoolean(showCommitters));
        this.config.setShowElapsedBuildTime((Boolean.parseBoolean(showElapsedBuildTime)));

        this.config.save();
    }
}
