package slacknotifications.teamcity.payload.format;

import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;
import slacknotifications.teamcity.payload.convertor.ExtraParametersMapToXmlConvertor;

import com.thoughtworks.xstream.XStream;

public class SlackNotificationPayloadXml extends SlackNotificationPayloadGeneric {

	private Integer rank = 100; 

	public SlackNotificationPayloadXml(SlackNotificationPayloadManager wpm) {
		super(wpm);
	}

	public void register(){
		myManager.registerPayloadFormat(this);
	}

	public String getCharset() {
		return "UTF-8";
	}

	public String getContentType() {
		return "text/xml";
	}

	public String getFormatDescription() {
		return "XML";
	}

	public String getFormatShortName() {
		return "xml";
	}

	public String getFormatToolTipText() {
		return "Send the payload formatted in XML";
	}

	
	public Integer getRank() {
		return this.rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Override
	protected String getStatusAsString(SlackNotificationPayloadContent content) {
		XStream xstream = new XStream();
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.registerConverter(new ExtraParametersMapToXmlConvertor());
        xstream.alias("build", SlackNotificationPayloadContent.class);
		return xstream.toXML(content);
	}

}
