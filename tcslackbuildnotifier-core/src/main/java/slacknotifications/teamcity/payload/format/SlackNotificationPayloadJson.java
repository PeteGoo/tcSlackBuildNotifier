/**
 * 
 */
package slacknotifications.teamcity.payload.format;

import slacknotifications.teamcity.payload.SlackNotificationPayload;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;
import slacknotifications.teamcity.payload.convertor.ExtraParametersMapToJsonConvertor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class SlackNotificationPayloadJson extends SlackNotificationPayloadGeneric implements SlackNotificationPayload {
	
	Integer rank = 100;
	String charset = "UTF-8";
	
	public SlackNotificationPayloadJson(SlackNotificationPayloadManager manager){
		super(manager);
	}

	public void register(){
		myManager.registerPayloadFormat(this);
	}
	
	public String getFormatDescription() {
		return "JSON";
	}

	public String getFormatShortName() {
		return "json";
	}

	public String getFormatToolTipText() {
		return "Send the payload formatted in JSON";
	}
	
	protected String getStatusAsString(SlackNotificationPayloadContent content){

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.registerConverter(new ExtraParametersMapToJsonConvertor());
        xstream.alias("build", SlackNotificationPayloadContent.class);
        /* For some reason, the items are coming back as "@name" and "@value"
         * so strip those out with a regex.
         */
		return xstream.toXML(content).replaceAll("\"@(name|value)\": \"(.*)\"", "\"$1\": \"$2\"");

	}

	public String getContentType() {
		return "application/json";
	}

	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getCharset() {
		return this.charset;
	}

}
