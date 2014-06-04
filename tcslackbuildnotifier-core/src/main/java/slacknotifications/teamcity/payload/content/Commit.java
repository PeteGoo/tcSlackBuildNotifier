package slacknotifications.teamcity.payload.content;

/**
 * Created by Peter on 4/06/2014.
 */
public class Commit {

    public Commit(String revision, String description, String userName) {
        this.description = description;
        this.userName = userName;
        this.revision = revision;
    }

    private String description;
    private String userName;
    private String revision;

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


}
