package slacknotifications.teamcity.payload.content;

import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.SVcsModification;
import slacknotifications.teamcity.Loggers;
import slacknotifications.teamcity.SlackNotificator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PayloadContentCommits {
    private List<Commit> commits;

    public PayloadContentCommits() {
        commits = new ArrayList<Commit>();
    }

    public void populateCommits(SRunningBuild sRunningBuild) {
        List<SVcsModification> changes = sRunningBuild.getContainingChanges();
        if (changes == null) {
            return;
        }

        for (SVcsModification change : changes) {
            Collection<SUser> committers = change.getCommitters();
            String slackUserName = null;
            if (committers != null && !committers.isEmpty()) {
                SUser committer = committers.iterator().next();
                slackUserName = committer.getPropertyValue(SlackNotificator.USERNAME_KEY);
                Loggers.ACTIVITIES.debug("Resolved committer " + change.getUserName() + " to Slack User " + slackUserName);
            }
            commits.add(new Commit(change.getVersion(), change.getDescription(), change.getUserName(), slackUserName));
        }
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }
}
