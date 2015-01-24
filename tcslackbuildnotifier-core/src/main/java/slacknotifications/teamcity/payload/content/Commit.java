package slacknotifications.teamcity.payload.content;

import jetbrains.buildServer.util.StringUtil;

/**
 * Created by Peter on 4/06/2014.
 */
public class Commit {

    public Commit(String revision, String description, String userName, String slackUserName) {
        this.description = description;
        this.userName = userName;
        this.revision = revision;

        if(slackUserName != null && slackUserName.startsWith("@")){
            slackUserName = slackUserName.substring(1);
        }
        this.slackUserName = slackUserName;
    }

    private String description;
    private String userName;
    private String revision;
    private String slackUserName;

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSlackUserName() {
        return slackUserName;
    }

    public void setSlackUserName(String slackUserName) {
        if(slackUserName != null && slackUserName.startsWith("@")){
            slackUserName = slackUserName.substring(1);
        }
        this.slackUserName = slackUserName;
    }

    public boolean hasSlackUsername(){
        return slackUserName != null && StringUtil.isNotEmpty(slackUserName);
    }
}
