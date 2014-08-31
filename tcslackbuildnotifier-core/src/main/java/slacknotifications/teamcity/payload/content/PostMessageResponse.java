package slacknotifications.teamcity.payload.content;

import com.google.gson.Gson;

/**
 * Created by Peter on 31/08/2014.
 */
public class PostMessageResponse {
    private boolean ok;
    private String channel;
    private String ts;
    private String error;

    public boolean getOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static PostMessageResponse fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, PostMessageResponse.class);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
