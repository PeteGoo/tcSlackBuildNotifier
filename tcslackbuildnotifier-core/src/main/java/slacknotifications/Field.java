package slacknotifications;

public class Field {

    public Field(String title, String value, Boolean isShort) {
        this.title = title;
        this.value = value;
        this.isShort = isShort;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getIsShort() {
        return isShort;
    }

    public void setIsShort(Boolean isShort) {
        this.isShort = isShort;
    }

    private String title;
    private String value;
    private Boolean isShort;
}
