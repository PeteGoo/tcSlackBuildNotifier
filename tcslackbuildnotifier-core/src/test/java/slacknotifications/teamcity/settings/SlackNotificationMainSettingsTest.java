package slacknotifications.teamcity.settings;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import jetbrains.buildServer.serverSide.SBuildServer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import slacknotifications.SlackNotificationProxyConfig;

public class SlackNotificationMainSettingsTest {
	SBuildServer server = mock(SBuildServer.class);
	Integer proxyPort = 8080;
	String proxyHost = "myproxy.mycompany.com";
    String defaultChannel = "#my-channel";
    String teamName =  "myteam";
    String token = "thisismytoken";
	
	@Test
	public void TestFullConfig(){
		SlackNotificationMainSettings whms = new SlackNotificationMainSettings(server);
		whms.register();
		whms.readFrom(getFullConfigElement());
		String proxy = whms.getProxyForUrl("http://something.somecompany.com");
		SlackNotificationProxyConfig whpc = whms.getProxyConfigForUrl("http://something.somecompany.com");
		assertTrue(proxy.equals(this.proxyHost));
		assertTrue(whpc.getProxyHost().equals(this.proxyHost ));
		assertTrue(whpc.getProxyPort().equals(this.proxyPort));
        assertTrue(whms.getDefaultChannel().equals(this.defaultChannel));
        assertTrue(whms.getTeamName().equals(this.teamName));
        assertTrue(whms.getToken().equals(this.token));
	}
	
	private Element getFullConfigElement(){
		SAXBuilder builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);
		try {
			Document doc = builder.build("src/test/resources/main-config-full.xml");
			return doc.getRootElement();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
