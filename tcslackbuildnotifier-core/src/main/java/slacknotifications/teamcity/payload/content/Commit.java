package slacknotifications.teamcity.payload.content;

import jetbrains.buildServer.util.StringUtil;

/**
 * Created by Peter on 4/06/2014.
 */
public class Commit {

    public Commit(String revision, String description, String userName, String slackUserId) {
        this.description = description;
        this.userName = userName;
        this.revision = revision;

        if(slackUserId != null && slackUserId.startsWith("@")){
            slackUserId = slackUserId.substring(1);
        }
        this.slackUserId = slackUserId;
    }

    private String description;
    private String userName;
    private String revision;
    private String slackUserId;

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

    public String getSlackUserId() {
        return slackUserId;
    }

    public void setSlackUserId(String slackUserId) {
        if(slackUserId != null && slackUserId.startsWith("@")){
            slackUserId = slackUserId.substring(1);
        }
        this.slackUserId = slackUserId;
    }

    public boolean hasSlackUserId(){
        return StringUtil.isNotEmpty(slackUserId);
    }
}
