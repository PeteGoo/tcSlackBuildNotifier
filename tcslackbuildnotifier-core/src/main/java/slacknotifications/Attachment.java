package slacknotifications;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 3/06/2014.
 */
public class Attachment {
    private String text;
    private String pretext;
    private String color;
    private String fallback;
    private List<Field> fields;

    public Attachment(String fallback, String text, String pretext, String color) {
        this.fallback = fallback;
        this.text = text;
        this.pretext = pretext;
        this.color = color;
        this.fields = new ArrayList<Field>();
    }

    public void addField(String title, String value, boolean isShort) {
        this.fields.add(new Field(title, value, isShort));
    }

    public String getFallback() {
        return fallback;
    }

   

    public String getText() {
        return text;
    }

    public String getPretext() {
        return pretext;
    }

    public String getColor() {
        return color;
    }

    public List<Field> getFields() {
        return fields;
    }
}
