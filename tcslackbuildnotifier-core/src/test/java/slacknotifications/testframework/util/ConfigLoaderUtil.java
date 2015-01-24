package slacknotifications.testframework.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import slacknotifications.teamcity.settings.SlackNotificationConfig;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ConfigLoaderUtil {
	
	public static Element getFullConfigElement(File file) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);
		Document doc = builder.build(file);
		return doc.getRootElement();
	}

	public static SlackNotificationConfig getFirstSlackNotificationInConfig(File f) throws JDOMException, IOException{
		Element fileAsElement = ConfigLoaderUtil.getFullConfigElement(f);
		assertTrue("One and only one slackNotifications expected when loading test config from file : " + f.getName(), fileAsElement.getChild("slackNotifications").getChildren("slackNotification").size() == 1);
		return new SlackNotificationConfig((Element) fileAsElement.getChild("slackNotifications").getChildren("slackNotification").get(0));
	}
	
}
