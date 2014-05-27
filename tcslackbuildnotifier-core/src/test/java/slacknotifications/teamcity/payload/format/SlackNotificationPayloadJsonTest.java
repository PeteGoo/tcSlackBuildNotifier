package slacknotifications.teamcity.payload.format;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import jetbrains.buildServer.serverSide.SBuildServer;

import org.junit.Test;

import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;

public class SlackNotificationPayloadJsonTest {

	@Test
	public void testRegister() {
		SBuildServer mockServer = mock(SBuildServer.class);
		when(mockServer.getRootUrl()).thenReturn("http://test.url");
		SlackNotificationPayloadManager wpm = new SlackNotificationPayloadManager(mockServer);
		SlackNotificationPayloadJson whp = new SlackNotificationPayloadJson(wpm);
		whp.register();
		assertEquals(whp, wpm.getFormat(whp.getFormatShortName()));
	}

	@Test
	public void testGetContentType() {
		SlackNotificationPayloadJson whp = new SlackNotificationPayloadJson(null);
		assertEquals(whp.getContentType().toString(), "application/json");

	}

	@Test
	public void testGetRank() {
		SlackNotificationPayloadJson whp = new SlackNotificationPayloadJson(null);
		assertTrue(whp.getRank() == 100);
	}

	@Test
	public void testSetRank() {
		SlackNotificationPayloadJson whp = new SlackNotificationPayloadJson(null);
		whp.setRank(10);
		assertTrue(whp.getRank() == 10);
	}

	@Test
	public void testGetCharset() {
		SlackNotificationPayloadJson whp = new SlackNotificationPayloadJson(null);
		assertTrue(whp.getCharset().equals("UTF-8".toString()));
	}

	@Test
	public void testGetFormatDescription() {
		SlackNotificationPayloadJson whp = new SlackNotificationPayloadJson(null);
		assertTrue(whp.getFormatDescription().equals("JSON".toString()));
	}

	@Test
	public void testGetFormatShortName() {
		SlackNotificationPayloadJson whp = new SlackNotificationPayloadJson(null);
		assertTrue(whp.getFormatShortName().equals("json".toString()));
	}

	@Test
	public void testGetFormatToolTipText() {
		SlackNotificationPayloadJson whp = new SlackNotificationPayloadJson(null);
		assertTrue(whp.getFormatToolTipText().equals("Send the payload formatted in JSON".toString()));
	}
}
