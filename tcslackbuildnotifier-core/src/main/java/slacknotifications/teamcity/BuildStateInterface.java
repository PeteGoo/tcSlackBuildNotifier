package slacknotifications.teamcity;

public interface BuildStateInterface {
    boolean isEnabled();

    void enable();

    void disable();
}
