package slacknotifications;

import com.google.gson.Gson;
import jetbrains.buildServer.util.StringUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpHost;
import slacknotifications.teamcity.BuildState;
import slacknotifications.teamcity.Loggers;
import slacknotifications.teamcity.payload.content.Commit;
import slacknotifications.teamcity.payload.content.PostMessageResponse;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;



public class SlackNotificationImpl implements SlackNotification {
	
	private static final String UTF8 = "UTF-8";

    private String proxyHost;
    private Integer proxyPort = 0;
    private String proxyUsername;
    private String proxyPassword;
    private String channel;
    private String teamName;
    private String token;
    private String iconUrl;
    private String content;
    private SlackNotificationPayloadContent payload;
    private Integer resultCode;
    private HttpClient client;
    private String filename = "";
    private Boolean enabled = false;
    private Boolean errored = false;
    private String errorReason = "";
    private List<NameValuePair> params = new ArrayList<NameValuePair>();
    private BuildState states;
    private String botName;
    private final static String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private PostMessageResponse response;
    private Boolean showBuildAgent;
    private Boolean showElapsedBuildTime;
    private boolean showCommits;
    private boolean showCommitters;
    private boolean showTriggeredBy;
    private int maxCommitsToDisplay;
    private boolean mentionChannelEnabled;
    private boolean mentionSlackUserEnabled;
    private boolean mentionHereEnabled;
    private boolean showFailureReason;
	
/*	This is a bit mask of states that should trigger a SlackNotification.
 *  All ones (11111111) means that all states will trigger the slacknotifications
 *  We'll set that as the default, and then override if we get a more specific bit mask. */
    //private Integer EventListBitMask = BuildState.ALL_ENABLED;
    //private Integer EventListBitMask = Integer.parseInt("0",2);


    public SlackNotificationImpl() {
        this.client = HttpClients.createDefault();
        this.params = new ArrayList<NameValuePair>();
    }

    public SlackNotificationImpl(String channel) {
        this.channel = channel;
        this.client = HttpClients.createDefault();
        this.params = new ArrayList<NameValuePair>();
    }

    public SlackNotificationImpl(String channel, String proxyHost, String proxyPort) {
        this.channel = channel;
        this.client = HttpClients.createDefault();
        this.params = new ArrayList<NameValuePair>();
        if (proxyPort.length() != 0) {
            try {
                this.proxyPort = Integer.parseInt(proxyPort);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
        this.setProxy(proxyHost, this.proxyPort, null);
    }

    public SlackNotificationImpl(String channel, String proxyHost, Integer proxyPort) {
        this.channel = channel;
        this.client = HttpClients.createDefault();
        this.params = new ArrayList<NameValuePair>();
        this.setProxy(proxyHost, proxyPort, null);
    }

    public SlackNotificationImpl(String channel, SlackNotificationProxyConfig proxyConfig) {
        this.channel = channel;
        this.client = HttpClients.createDefault();
        this.params = new ArrayList<NameValuePair>();
        setProxy(proxyConfig);
    }

    public SlackNotificationImpl(HttpClient httpClient, String channel) {
        this.channel = channel;
        this.client = httpClient;
    }

    public void setProxy(SlackNotificationProxyConfig proxyConfig) {
        if ((proxyConfig != null) && (proxyConfig.getProxyHost() != null) && (proxyConfig.getProxyPort() != null)) {
            this.setProxy(proxyConfig.getProxyHost(), proxyConfig.getProxyPort(), proxyConfig.getCreds());
        }
    }

    public void setProxy(String proxyHost, Integer proxyPort, Credentials credentials) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        
		if (this.proxyHost.length() > 0 && !this.proxyPort.equals(0)) {
            HttpClientBuilder clientBuilder = HttpClients.custom()
                .useSystemProperties()
                .setProxy(new HttpHost(proxyHost, proxyPort, "http"));
                
            if (credentials != null) {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), credentials);
                clientBuilder.setDefaultCredentialsProvider(credsProvider);
                Loggers.SERVER.debug("SlackNotification ::using proxy credentials " + credentials.getUserPrincipal().getName());
            }
            
            this.client = clientBuilder.build();
		}
    }

    public void post() throws IOException {
        if(getIsApiToken()){
            postViaApi();
        }
        else{
            postViaWebHook();
        }

    }

    private void postViaApi() throws IOException {
        if ((this.enabled) && (!this.errored)) {
            if (this.teamName == null) {
                this.teamName = "";
            }
            String url = String.format("https://slack.com/api/chat.postMessage?token=%s&link_names=1&username=%s&icon_url=%s&channel=%s&text=%s&pretty=1",
                    this.token,
                    this.botName == null ? "" : URLEncoder.encode(this.botName, UTF8),
                    this.iconUrl == null ? "" : URLEncoder.encode(this.iconUrl, UTF8),
                    this.channel == null ? "" : URLEncoder.encode(this.channel, UTF8),
                    this.payload == null ? "" : URLEncoder.encode(payload.getBuildDescriptionWithLinkSyntax(), UTF8),
                    "");

            HttpPost httppost = new HttpPost(url);

            Loggers.SERVER.info("SlackNotificationListener :: Preparing message for URL " + url + " using proxy " + this.proxyHost + ":" + this.proxyPort);
            if (this.filename.length() > 0) {
                File file = new File(this.filename);
                throw new NotImplementedException();
            }
            if (this.payload != null) {

                List<Attachment> attachments = getAttachments();

                String attachmentsParam = String.format("attachments=%s", URLEncoder.encode(convertAttachmentsToJson(attachments), UTF8));

                Loggers.SERVER.info("SlackNotificationListener :: Body message will be " + attachmentsParam);

                httppost.setEntity(new StringEntity(attachmentsParam));
                httppost.setHeader("Content-Type", CONTENT_TYPE);
            }
            try {
                HttpResponse response = client.execute(httppost);
                this.resultCode = response.getStatusLine().getStatusCode();
                if (this.resultCode == HttpStatus.SC_OK) {
                    this.response = PostMessageResponse.fromJson(EntityUtils.toString(response.getEntity()));
                }
                if (response.getEntity().getContentLength() > 0) {
                    this.content = EntityUtils.toString(response.getEntity());
                }
            } finally {
                httppost.releaseConnection();
            }
        }
    }

    private void postViaWebHook() throws IOException {
        if ((this.enabled) && (!this.errored)) {
            if (this.teamName == null) {
                this.teamName = "";
            }

            String url = "";
            if(this.token != null && this.token.startsWith("http")){
                url = this.token;
            }
             else {
                url = String.format("https://%s.slack.com/services/hooks/incoming-webhook?token=%s",
                        this.teamName.toLowerCase(),
                        this.token);
            }

            Loggers.SERVER.info("SlackNotificationListener :: Preparing message for URL " + url);

            WebHookPayload requestBody = new WebHookPayload();
            requestBody.setChannel(this.getChannel());
            requestBody.setUsername(this.getBotName());
            requestBody.setIcon_url(this.getIconUrl());

            HttpPost httppost = new HttpPost(url);

            if (this.payload != null) {
                requestBody.setText(payload.getBuildDescriptionWithLinkSyntax());
                requestBody.setAttachments(getAttachments());
            }

            String bodyParam = String.format("payload=%s", URLEncoder.encode(requestBody.toJson(), UTF8));

            Loggers.SERVER.info("SlackNotificationListener :: Body message will be " + bodyParam);

            httppost.setEntity(new StringEntity(bodyParam));
            httppost.setHeader("Content-Type", CONTENT_TYPE);

            try {
                HttpResponse response = client.execute(httppost);
                this.resultCode = response.getStatusLine().getStatusCode();

                PostMessageResponse resp = new PostMessageResponse();

                if (this.resultCode != HttpStatus.SC_OK) {
                    String error = EntityUtils.toString(response.getEntity());
                    resp.setOk(error == "ok");
                    resp.setError(error);
                }
                else{
                    resp.setOk(true);
                    this.response = resp;
                }

                this.content = EntityUtils.toString(response.getEntity());

            } finally {
                httppost.releaseConnection();
            }
        }
    }

    private List<Attachment> getAttachments() {
        List<Attachment> attachments = new ArrayList<Attachment>();
        Attachment attachment = new Attachment(this.payload.getBuildName(), null, null, this.payload.getColor());

        List<String> firstDetailLines = new ArrayList<String>();
        if(showBuildAgent == null || showBuildAgent){
            firstDetailLines.add("Agent: " + this.payload.getAgentName());
        }
        if(this.payload.getIsComplete() && (showElapsedBuildTime == null || showElapsedBuildTime)){
            firstDetailLines.add("Elapsed: " + formatTime(this.payload.getElapsedTime()));
        }

        attachment.addField(this.payload.getBuildName(), StringUtil.join(firstDetailLines, "\n"), false);

        if(showFailureReason && this.payload.getBuildResult() == SlackNotificationPayloadContent.BUILD_STATUS_FAILURE){
            if(this.payload.getFailedBuildMessages().size() > 0) {
                attachment.addField("Reason", StringUtil.join(", ", payload.getFailedBuildMessages()), false);
            }
            if(this.payload.getFailedTestNames().size() > 0){
                ArrayList<String> failedTestNames = payload.getFailedTestNames();
                String truncated = "";
                if(failedTestNames.size() > 10){
                    failedTestNames = new ArrayList<String>( failedTestNames.subList(0, 9));
                    truncated = " (+ " + Integer.toString(payload.getFailedBuildMessages().size() - 10) + " more)";
                }
                payload.getFailedTestNames().size();
                attachment.addField("Failed Tests", StringUtil.join(", ", failedTestNames) + truncated, false);
            }
        }

        StringBuilder sbCommits = new StringBuilder();

        List<Commit> commits = this.payload.getCommits();

        List<Commit> commitsToDisplay = new ArrayList<Commit>(commits);

        if(showCommits) {
            boolean truncated = false;
            int totalCommits = commitsToDisplay.size();
            if (commitsToDisplay.size() > maxCommitsToDisplay) {
                commitsToDisplay = commitsToDisplay.subList(0, maxCommitsToDisplay > commitsToDisplay.size() ? commitsToDisplay.size() : 5);
                truncated = true;
            }

            for (Commit commit : commitsToDisplay) {
                String revision = commit.getRevision();
                revision = revision == null ? "" : revision;
                sbCommits.append(String.format("%s :: %s :: %s\n", revision.substring(0, Math.min(revision.length(), 10)), commit.getUserName(), commit.getDescription()));
            }

            if (truncated) {
                sbCommits.append(String.format("(+ %d more)\n", totalCommits - 5));
            }

            if (!commitsToDisplay.isEmpty()) {
                attachment.addField("Commits", sbCommits.toString(), false);
            }
        }

        List<String> slackUsers = new ArrayList<String>();


        for(Commit commit : commits){
            if(commit.hasSlackUsername()){
                slackUsers.add("<@" + commit.getSlackUserName() + ">");
            }
        }
        HashSet<String> tempHash = new HashSet<String>(slackUsers);
        slackUsers = new ArrayList<String>(tempHash);

        if(showCommitters) {
            Set<String> committers = new HashSet<String>();
            for (Commit commit : commits) {
                committers.add(commit.getUserName());
            }

            String committersString = StringUtil.join(", ", committers);

            if (!commits.isEmpty()) {
                attachment.addField("Changes By", committersString, false);
            }
        }
        
        if (showTriggeredBy){
            attachment.addField("Triggered By", this.payload.getTriggeredBy(), false);
        }

        // Mention the channel and/or the Slack Username of any committers if known
        if(payload.getIsFirstFailedBuild()
                && (mentionChannelEnabled
                    || mentionHereEnabled
                    ||(mentionSlackUserEnabled
                        && !slackUsers.isEmpty()))){
            String mentionContent = ":arrow_up: \"" + this.payload.getBuildName() + "\" Failed ";
            if(mentionChannelEnabled){
                mentionContent += "<!channel> ";
            }
            if(mentionSlackUserEnabled && !slackUsers.isEmpty() && !this.payload.isMergeBranch()) {
                mentionContent += StringUtil.join(" ", slackUsers);
            }
            if (mentionHereEnabled) {
                mentionContent += "<!here>";
            }
            attachment.addField("", mentionContent, true);
        }

        attachments.add(attachment);
        return attachments;
    }

    private class WebHookPayload {
        private String channel;
        private String username;
        private String text;
        private String icon_url;
        private List<Attachment> attachments;

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }

        public List<Attachment> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<Attachment> attachments) {
            this.attachments = attachments;
        }

        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

    public static String convertAttachmentsToJson(List<Attachment> attachments) {
        Gson gson = new Gson();
        return gson.toJson(attachments);
//        XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
//        xstream.setMode(XStream.NO_REFERENCES);
//        xstream.alias("build", Attachment.class);
//        /* For some reason, the items are coming back as "@name" and "@value"
//         * so strip those out with a regex.
//         */
//        return xstream.toXML(attachments).replaceAll("\"@(fallback|text|pretext|color|fields|title|value|short)\": \"(.*)\"", "\"$1\": \"$2\"");
    }

    public Integer getStatus() {
        return this.resultCode;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getBotName() {
        return this.botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getParameterisedUrl() {
        //TODO: Implement different url logic
        return this.channel + this.parametersAsQueryString();
    }

    public String parametersAsQueryString() {
        String s = "";
        for (Iterator<NameValuePair> i = this.params.iterator(); i.hasNext(); ) {
            NameValuePair nv = i.next();
            s += "&" + nv.getName() + "=" + nv.getValue();
        }
        if (s.length() > 0) {
            return "?" + s.substring(1);
        }
        return s;
    }

    public void addParam(String key, String value) {
        this.params.add(new BasicNameValuePair(key, value));
    }

    public void addParams(List<NameValuePair> paramsList) {
        for (Iterator<NameValuePair> i = paramsList.iterator(); i.hasNext(); ) {
            this.params.add(i.next());
        }
    }

    public String getParam(String key) {
        for (Iterator<NameValuePair> i = this.params.iterator(); i.hasNext(); ) {
            NameValuePair nv = i.next();
            if (nv.getName().equals(key)) {
                return nv.getValue();
            }
        }
        return "";
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setEnabled(String enabled) {
        if ("true".equals(enabled.toLowerCase())) {
            this.enabled = true;
        } else {
            this.enabled = false;
        }
    }

    public Boolean isErrored() {
        return errored;
    }

    public void setErrored(Boolean errored) {
        this.errored = errored;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

//	public Integer getEventListBitMask() {
//		return EventListBitMask;
//	}
//
//	public void setTriggerStateBitMask(Integer triggerStateBitMask) {
//		EventListBitMask = triggerStateBitMask;
//	}

    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public SlackNotificationPayloadContent getPayload() {
        return payload;
    }

    public void setPayload(SlackNotificationPayloadContent payloadContent) {
        this.payload = payloadContent;
    }

    @Override
    public BuildState getBuildStates() {
        return states;
    }

    @Override
    public void setBuildStates(BuildState states) {
        this.states = states;
    }

    public PostMessageResponse getResponse() {
        return response;
    }

    @Override
    public void setShowBuildAgent(Boolean showBuildAgent) {
        this.showBuildAgent = showBuildAgent;
    }

    @Override
    public void setShowElapsedBuildTime(Boolean showElapsedBuildTime) {
        this.showElapsedBuildTime = showElapsedBuildTime;
    }

    @Override
    public void setShowCommits(boolean showCommits) {
        this.showCommits = showCommits;
    }
	
    @Override
    public void setShowCommitters(boolean showCommitters) {
        this.showCommitters = showCommitters;
    }

    @Override
    public void setShowTriggeredBy(boolean showTriggeredBy) {
        this.showTriggeredBy = showTriggeredBy;
    }

    @Override
    public void setMaxCommitsToDisplay(int maxCommitsToDisplay) {
        this.maxCommitsToDisplay = maxCommitsToDisplay;
    }

    @Override
    public void setMentionChannelEnabled(boolean mentionChannelEnabled) {
        this.mentionChannelEnabled = mentionChannelEnabled;
    }

    @Override
    public void setMentionSlackUserEnabled(boolean mentionSlackUserEnabled) {
        this.mentionSlackUserEnabled = mentionSlackUserEnabled;
    }

    @Override
    public void setMentionHereEnabled(boolean mentionHereEnabled) {
        this.mentionHereEnabled = mentionHereEnabled;
    }

    @Override
    public void setShowFailureReason(boolean showFailureReason) {
        this.showFailureReason = showFailureReason;
    }

    public boolean getIsApiToken() {
        if(this.token != null && this.token.startsWith("http")){
            // We now accept a webhook url.
            return false;
        }
        return this.token == null || this.token.split("-").length > 1;
    }

    private String formatTime(long seconds){
        if(seconds < 60){
            return seconds + "s";
        }
        return String.format("%dm:%ds",
                TimeUnit.SECONDS.toMinutes(seconds),
                TimeUnit.SECONDS.toSeconds(seconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
        );
    }
}
