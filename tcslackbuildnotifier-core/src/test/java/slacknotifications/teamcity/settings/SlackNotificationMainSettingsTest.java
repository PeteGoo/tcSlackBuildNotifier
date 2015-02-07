package slacknotifications.teamcity.settings;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import jetbrains.buildServer.serverSide.SBuildServer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;

import slacknotifications.SlackNotificationProxyConfig;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

public class SlackNotificationMainSettingsTest {
	SBuildServer server = mock(SBuildServer.class);
	Integer proxyPort = 8080;
	String proxyHost = "myproxy.mycompany.com";
    String defaultChannel = "#my-channel";
    String teamName =  "myteam";
    String token = "thisismytoken";
	String iconUrl = "http://www.myicon.com/icon.gif";
    String botName = "Team City";

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
        assertTrue(whms.getIconUrl().equals(this.iconUrl));
        assertTrue(whms.getBotName().equals(this.botName));
        assertTrue(whms.getShowBuildAgent());
        assertTrue(whms.getShowElapsedBuildTime());
        assertFalse(whms.getShowCommits());
        assertEquals(15, whms.getMaxCommitsToDisplay());
        
        Credentials credentials = whpc.getCreds();
        
		assertEquals("some-username", credentials.getUserPrincipal().getName());
		assertEquals("some-password", credentials.getPassword());
	}

    @Test
    public void TestEmptyDefaultsConfig(){
        SlackNotificationMainSettings whms = new SlackNotificationMainSettings(server);
        whms.register();
        whms.readFrom(getEmptyDefaultsConfigElement());
        String proxy = whms.getProxyForUrl("http://something.somecompany.com");
        SlackNotificationProxyConfig whpc = whms.getProxyConfigForUrl("http://something.somecompany.com");
        assertTrue(proxy.equals(this.proxyHost));
        assertTrue(whpc.getProxyHost().equals(this.proxyHost ));
        assertTrue(whpc.getProxyPort().equals(this.proxyPort));
        assertTrue(whms.getDefaultChannel().equals(this.defaultChannel));
        assertTrue(whms.getTeamName().equals(this.teamName));
        assertTrue(whms.getToken().equals(this.token));
        assertTrue(whms.getIconUrl().equals(this.iconUrl));
        assertTrue(whms.getBotName().equals(this.botName));
        assertNull(whms.getShowBuildAgent());
        assertNull(whms.getShowElapsedBuildTime());
        assertTrue(whms.getShowCommits());
        assertEquals(5, whms.getMaxCommitsToDisplay());
    }

    private Element getFullConfigElement(){
        return getElement("src/test/resources/main-config-full.xml");
    }

    private Element getEmptyDefaultsConfigElement(){
        return getElement("src/test/resources/main-config-empty-defaults.xml");
    }

    private Element getElement(String filePath){
        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);
        try {
            Document doc = builder.build(filePath);
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
