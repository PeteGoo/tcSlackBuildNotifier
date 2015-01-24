package slacknotifications.teamcity;

import jetbrains.buildServer.Build;
import jetbrains.buildServer.BuildType;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.serverSide.problems.BuildProblemInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.NotificatorPropertyKey;
import jetbrains.buildServer.users.PropertyKey;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.vcs.VcsModification;
import jetbrains.buildServer.vcs.VcsRoot;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slacknotifications.SlackNotification;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.settings.SlackNotificationMainSettings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SlackNotificator implements Notificator {

    private final SlackNotificationMainSettings mainConfig;
    private final SlackNotificationFactory notificationFactory;
    private final SlackNotificationPayloadManager payloadManager;
    private final SBuildServer buildServer;
    private ArrayList<UserPropertyInfo> userProps;

    private static final String SLACK_USERNAME_KEY = "tcSlackNotifier.userName";
    private static final String TYPE = "tcSlackBuildNotifier";

    private static final PropertyKey USERNAME_KEY = new NotificatorPropertyKey(TYPE, SLACK_USERNAME_KEY);

    public SlackNotificator(NotificatorRegistry notificatorRegistry,
                            SBuildServer sBuildServer,
                            SlackNotificationMainSettings configSettings,
                            SlackNotificationFactory factory,
                            SlackNotificationPayloadManager manager){
        Loggers.ACTIVITIES.debug("Registering SlackNotificator...");

        userProps = new ArrayList<UserPropertyInfo>();
        userProps.add(new UserPropertyInfo(SLACK_USERNAME_KEY, "Slack Username"));
        notificatorRegistry.register(this, userProps);
        mainConfig = configSettings;
        notificationFactory = factory;
        payloadManager = manager;
        buildServer = sBuildServer;
    }

    public void register(){

    }

    @Override
    public void notifyBuildStarted(SRunningBuild sRunningBuild, Set<SUser> set) {
        for(SUser sUser : set){
            if(!userHasSlackNameConfigured(sUser)){
                continue;
            }
            SlackNotification slackNotification = createNotification(sUser);
            slackNotification.setPayload(payloadManager.buildStarted(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
            doNotification(slackNotification);
        }
    }

    @Override
    public void notifyBuildSuccessful(SRunningBuild sRunningBuild, Set<SUser> set) {
        for(SUser sUser : set){
            if(!userHasSlackNameConfigured(sUser)){
                continue;
            }
            SlackNotification slackNotification = createNotification(sUser);
            slackNotification.setPayload(payloadManager.buildFinished(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
            doNotification(slackNotification);
        }
    }

    @Override
    public void notifyBuildFailed(SRunningBuild sRunningBuild, Set<SUser> set) {
        for(SUser sUser : set){
            if(!userHasSlackNameConfigured(sUser)){
                continue;
            }
            SlackNotification slackNotification = createNotification(sUser);
            slackNotification.setPayload(payloadManager.buildFinished(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
            doNotification(slackNotification);
        }
    }

    @Override
    public void notifyBuildFailedToStart(SRunningBuild sRunningBuild, Set<SUser> set) {
    }

    @Override
    public void notifyLabelingFailed(Build build, VcsRoot vcsRoot, Throwable throwable, Set<SUser> set) {

    }

    @Override
    public void notifyBuildFailing(SRunningBuild sRunningBuild, Set<SUser> set) {
        for(SUser sUser : set){
            if(!userHasSlackNameConfigured(sUser)){
                continue;
            }
            SlackNotification slackNotification = createNotification(sUser);
            slackNotification.setPayload(payloadManager.beforeBuildFinish(sRunningBuild, getPreviousNonPersonalBuild(sRunningBuild)));
            doNotification(slackNotification);
        }
    }

    @Override
    public void notifyBuildProbablyHanging(SRunningBuild sRunningBuild, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleChanged(SBuildType sBuildType, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleAssigned(SBuildType sBuildType, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleChanged(TestNameResponsibilityEntry testNameResponsibilityEntry, TestNameResponsibilityEntry testNameResponsibilityEntry1, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleAssigned(TestNameResponsibilityEntry testNameResponsibilityEntry, TestNameResponsibilityEntry testNameResponsibilityEntry1, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleChanged(Collection<TestName> collection, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyResponsibleAssigned(Collection<TestName> collection, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemResponsibleAssigned(Collection<BuildProblemInfo> collection, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemResponsibleChanged(Collection<BuildProblemInfo> collection, ResponsibilityEntry responsibilityEntry, SProject sProject, Set<SUser> set) {

    }

    @Override
    public void notifyTestsMuted(Collection<STest> collection, MuteInfo muteInfo, Set<SUser> set) {

    }

    @Override
    public void notifyTestsUnmuted(Collection<STest> collection, MuteInfo muteInfo, SUser sUser, Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemsMuted(Collection<BuildProblemInfo> collection, MuteInfo muteInfo, Set<SUser> set) {

    }

    @Override
    public void notifyBuildProblemsUnmuted(Collection<BuildProblemInfo> collection, MuteInfo muteInfo, SUser sUser, Set<SUser> set) {

    }

    @NotNull
    @Override
    public String getNotificatorType() {
        return TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Slack Notifier";
    }


    private boolean userHasSlackNameConfigured(SUser sUser){
        String userName = sUser.getPropertyValue(USERNAME_KEY);

        return userName != null && StringUtil.isNotEmpty(userName);
    }

    private SlackNotification createNotification(SUser sUser){
        SlackNotification notification = notificationFactory.getSlackNotification();
        String userName = sUser.getPropertyValue(USERNAME_KEY);
        if(userName.substring(0,1) == "@"){
            userName = userName.substring(1);
        }
        notification.setChannel("@" + userName);

        notification.setTeamName(mainConfig.getTeamName());
        notification.setToken(mainConfig.getToken());
        notification.setIconUrl(mainConfig.getIconUrl());
        notification.setBotName(mainConfig.getBotName());
        notification.setEnabled(true);
        //notification.setBuildStates(BuildState);
        //notification.setProxy(mainConfig.getProxyConfigForUrl(slackNotificationConfig.getChannel()));
        notification.setShowBuildAgent(mainConfig.getShowBuildAgent());
        notification.setShowElapsedBuildTime(mainConfig.getShowElapsedBuildTime());
        notification.setShowCommits(mainConfig.getShowCommits());
        notification.setMaxCommitsToDisplay(mainConfig.getMaxCommitsToDisplay());
        notification.setMentionChannelEnabled(false);


        return notification;

    }

    @Nullable
    private SFinishedBuild getPreviousNonPersonalBuild(SRunningBuild paramSRunningBuild)
    {
        List<SFinishedBuild> localList = buildServer.getHistory().getEntriesBefore(paramSRunningBuild, false);

        for (SFinishedBuild localSFinishedBuild : localList)
            if (!(localSFinishedBuild.isPersonal())) return localSFinishedBuild;
        return null;
    }

    private void doNotification(SlackNotification notification) {
            try {
                if (notification.isEnabled()){
                    notification.post();
                    if (notification.getResponse() != null && !notification.getResponse().getOk()) {
                        Loggers.SERVER.error(this.getClass().getSimpleName() + " :: SlackNotification failed : "
                                + notification.getChannel()
                                + " returned error " + notification.getResponse().getError()
                                + " " + notification.getErrorReason());
                    }
                    else {
                        Loggers.SERVER.info(this.getClass().getSimpleName() + " :: SlackNotification delivered : "
                                + notification.getChannel()
                                + " returned " + notification.getStatus()
                                + " " + notification.getErrorReason());
                    }
                    Loggers.SERVER.debug(this.getClass().getSimpleName() + ":doPost :: content dump: " + notification.getPayload());
                    if (notification.isErrored()){
                        Loggers.SERVER.error(notification.getErrorReason());
                    }
                    if ((notification.getStatus() == null || notification.getStatus() > HttpStatus.SC_OK))
                        Loggers.ACTIVITIES.warn("SlackNotificationListener :: " + notification.getParam("projectId") + " SlackNotification (url: " + notification.getChannel() + " proxy: " + notification.getProxyHost() + ":" + notification.getProxyPort()+") returned HTTP status " + notification.getStatus().toString());

                } else {
                    Loggers.SERVER.debug("SlackNotification NOT triggered: "
                            + notification.getParam("buildStatus") + " " + notification.getChannel());
                }
            } catch (FileNotFoundException e) {
                Loggers.SERVER.warn(this.getClass().getName() + ":doPost :: "
                        + "A FileNotFoundException occurred while attempting to execute SlackNotification (" + notification.getChannel() + "). See the following stacktrace");
                Loggers.SERVER.warn(e);
            } catch (IOException e) {
                Loggers.SERVER.warn(this.getClass().getName() + ":doPost :: "
                        + "An IOException occurred while attempting to execute SlackNotification (" + notification.getChannel() + "). See the following stacktrace");
                Loggers.SERVER.warn(e);
            }

    }
}
