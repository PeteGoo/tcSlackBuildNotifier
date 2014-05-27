package slacknotifications.testframework.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import slacknotifications.teamcity.settings.SlackNotificationConfig;

public class ConfigLoaderUtil {
	
	public static Element getFullConfigElement(File file) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);
		Document doc = builder.build(file);
		return doc.getRootElement();
	}

	public static SlackNotificationConfig getFirstSlackNotificationInConfig(File f) throws JDOMException, IOException{
		Element fileAsElement = ConfigLoaderUtil.getFullConfigElement(f);
		assertTrue("One and only one slacknotifications expected when loading test config from file : " + f.getName(), fileAsElement.getChild("slacknotifications").getChildren("slacknotification").size() == 1);
		return new SlackNotificationConfig((Element) fileAsElement.getChild("slacknotifications").getChildren("slacknotification").get(0));
	}
	
}
