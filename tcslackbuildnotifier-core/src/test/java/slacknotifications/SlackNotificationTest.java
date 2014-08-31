package slacknotifications;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Ignore;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import slacknotifications.teamcity.*;
import slacknotifications.teamcity.payload.content.Commit;
import slacknotifications.teamcity.payload.content.PostMessageResponse;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;


public class SlackNotificationTest {
	public String proxy = "127.0.0.1";
	public Integer proxyPort = 58002;
	String proxyPortString = "58002";
	public Integer webserverPort = 58001;
	public Integer proxyserverPort = 58002;
	public String webserverHost = "127.0.0.1";
	String url = "http://127.0.0.1:58001";
	
	public String proxyUsername = "foo";
	public String proxyPassword = "bar";
	
	SlackNotificationFactory factory = new SlackNotificationFactoryImpl();
	
	@Test
	public void test_BuildStates(){
		assertTrue(BuildStateEnum.BUILD_STARTED.getShortName().equals("buildStarted"));
		assertTrue(BuildStateEnum.BUILD_FINISHED.getShortName().equals("buildFinished"));
		assertTrue(BuildStateEnum.BEFORE_BUILD_FINISHED.getShortName().equals("beforeBuildFinish"));
		assertTrue(BuildStateEnum.RESPONSIBILITY_CHANGED.getShortName().equals("responsibilityChanged"));
		assertTrue(BuildStateEnum.BUILD_INTERRUPTED.getShortName().equals("buildInterrupted"));

		
	}
	
	
	@Test
	public void test_ProxyPort() {
		SlackNotification W = factory.getSlackNotification(url, proxy, proxyPort);
		assertTrue(W.getProxyPort() == proxyPort);
	}

	@Test
	public void test_ProxyHost() {
		SlackNotification W = factory.getSlackNotification(url, proxy, proxyPort);
		assertTrue(W.getProxyHost() == proxy);
	}
	
	@Test
	public void test_URL() {
		SlackNotification W = factory.getSlackNotification(url, proxy, proxyPort);
		assertTrue(W.getChannel() == url);
	}

    @Ignore // Not a valid test anymore?
	@Test(expected=java.io.FileNotFoundException.class)
	public void test_FileNotFoundExeption() throws FileNotFoundException, IOException{
		System.out.print("Testing for FileNotFound exception");
		SlackNotification w = factory.getSlackNotification(url, proxy, proxyPort);
		w.setFilename("SlackNotifications/src/test/resources/fileWithDoesNotExist.txt");
		w.setEnabled(true);
		w.post();
		System.out.print(".. done");
	}

    @Ignore
	@Test(expected=java.net.ConnectException.class)
	public void test_ConnectionRefused() throws ConnectException, IOException{
		SlackNotification w = factory.getSlackNotification(url);
		w.setEnabled(true);
		w.post();		
	}

    @Ignore // Is this still relevant?
	@Test(expected=java.io.IOException.class)
	public void test_IOExeption() throws IOException{
		System.out.println("Testing for IO exception");
		SlackNotification w = factory.getSlackNotification(url, "localhost", proxyPort);
		w.setEnabled(true);
		w.post();
		System.out.print(".. done");
	}

    @Test
    public void post_whenResponseIsOk_doesNotThrow() throws IOException {
        ArgumentCaptor<HttpPost> requestCaptor = ArgumentCaptor.forClass(HttpPost.class);
        HttpClient httpClient = mock(HttpClient.class);
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 200, ""));
        PostMessageResponse successfulResponse = new PostMessageResponse();
        successfulResponse.setOk(true);
        successfulResponse.setError("channel_not_found");
        response.setEntity(new StringEntity(successfulResponse.toJson()));

        when(httpClient.execute(requestCaptor.capture())).thenReturn(response);

        SlackNotification w = factory.getSlackNotification(httpClient, "#test-channel");

        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent();
        content.setBuildDescriptionWithLinkSyntax("http://foo");
        content.setCommits(new ArrayList<Commit>());

        w.setPayload(content);
        w.setEnabled(true);
        w.post();

        List<HttpPost> capturedRequests = requestCaptor.getAllValues();
        HttpPost request = capturedRequests.get(0);

        assertNotNull(w.getResponse());
        assertTrue(w.getResponse().getOk());
    }

    @Test
    public void post_whenResponseIsFailure_logsException() throws IOException {
        ArgumentCaptor<HttpPost> requestCaptor = ArgumentCaptor.forClass(HttpPost.class);
        HttpClient httpClient = mock(HttpClient.class);
        BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("http", 1, 1), 200, ""));
        PostMessageResponse failedResponse = new PostMessageResponse();
        failedResponse.setOk(false);
        failedResponse.setError("channel_not_found");
        response.setEntity(new StringEntity(failedResponse.toJson()));

        when(httpClient.execute(requestCaptor.capture())).thenReturn(response);

        SlackNotification w = factory.getSlackNotification(httpClient, "#test-channel");

        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent();
        content.setBuildDescriptionWithLinkSyntax("http://foo");
        content.setCommits(new ArrayList<Commit>());

        w.setPayload(content);
        w.setEnabled(true);
        w.post();

        assertNotNull(w.getResponse());
        assertFalse(w.getResponse().getOk());
    }

    @Test
    public void actualTest() throws IOException {
        SlackNotificationImpl impl = new SlackNotificationImpl("#sdfdfgdfg");
        impl.setToken("xoxp-sdfgdfg-2602518dfghdgfh312-dfghd-f93dc2");
        impl.setBotName("foo");
        impl.setTeamName("mmbottest");
        impl.setEnabled(true);
        SlackNotificationPayloadContent content = new SlackNotificationPayloadContent();
        content.setBuildResult("sdfd");
        content.setColor("#ff0000");
        content.setAgentName("fsdf");
        content.setCommits(new ArrayList<Commit>());
        content.setBuildDescriptionWithLinkSyntax("asdf");
        impl.setPayload(content);

        impl.post();

        assertNotNull(impl.getResponse());
        assertFalse(impl.getResponse().getOk());
        assertEquals(impl.getResponse().getError(), "invalid_auth");
    }

/*
    @Test
	public void test_200() throws FileNotFoundException, IOException, Exception {
        String expectedBody = "";
        HttpClient httpClient = mock(HttpClient.class) ;
        HttpResponse response = null;
        when(httpClient.executeMethod(any(HttpUriRequest.class)))
                .thenReturn(response);
        when(httpClient.execute(any(PostMethod.class))).thenReturn(200);
        when(response.getStatusCode()).thenReturn(200);
        when(response.getBody()).thenReturn("{id:1, name:\"Diego\", surname:\"Castorina\"}"):
		SlackNotification w = factory.getSlackNotification(httpClient, "#test-channel");
		w.setEnabled(true);
		w.post();
		System.out.println(w.getContent());
		assertTrue(w.getStatus() == HttpStatus.SC_OK);
	}

    @Ignore
	@Test
	public void test_NotEnabled() throws FileNotFoundException, IOException, InterruptedException {
		SlackNotificationTestServer s = startWebServer();
		SlackNotification w = factory.getSlackNotification(url + "/200", proxy, proxyPort);
		w.post();
		stopWebServer(s);
		assertTrue(w.getStatus() == null);
	}

    @Ignore
	@Test
	public void test_200WithProxy() throws FileNotFoundException, IOException, InterruptedException {
		SlackNotificationTestServer s = startWebServer();
		SlackNotificationTestProxyServer p = startProxyServer();
		SlackNotification w = factory.getSlackNotification(url + "/200", proxy, proxyPort);
		w.setEnabled(true);
		w.post();
		stopWebServer(s);
		stopProxyServer(p);
		assertTrue(w.getStatus() == HttpStatus.SC_OK);
	}

    @Ignore
	@Test
	public void test_200WithProxyFailAuth() throws FileNotFoundException, IOException, InterruptedException {
		SlackNotificationTestServer s = startWebServer();
		SlackNotificationTestProxyServer p = startProxyServerAuth(proxyUsername, proxyPassword);
		SlackNotification w = factory.getSlackNotification(url + "/200", proxy, proxyPort);
		w.setEnabled(true);
		w.post();
		stopWebServer(s);
		stopProxyServer(p);
		assertTrue(w.getStatus() == HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
	}

    @Ignore
	@Test
	public void test_200WithProxyAuth() throws FileNotFoundException, IOException, InterruptedException {
		SlackNotificationTestServer s = startWebServer();
		SlackNotificationTestProxyServer p = startProxyServerAuth(proxyUsername, proxyPassword);
		SlackNotification w = factory.getSlackNotification(url + "/200", proxy, proxyPort);
		w.setEnabled(true);
		w.setProxyUserAndPass(proxyUsername, proxyPassword);
		w.post();
		stopWebServer(s);
		stopProxyServer(p);
		assertTrue(w.getStatus() == HttpStatus.SC_OK);
	}

    @Ignore
	@Test
	public void test_200WithFilename() throws FileNotFoundException, IOException, Exception {
		SlackNotificationTestServer s = startWebServer();
		SlackNotification w = factory.getSlackNotification(url + "/200");
		w.setFilename("src/test/resources/FileThatDoesExist.txt");
		w.setEnabled(true);
		w.post();
		stopWebServer(s);
		assertTrue(w.getStatus() == HttpStatus.SC_OK);
	}

    @Ignore
	@Test
	public void test_200WithFilenameWithProxy() throws FileNotFoundException, IOException, Exception {
		SlackNotificationTestServer s = startWebServer();
		SlackNotificationTestProxyServer p = startProxyServer();
		SlackNotification w = factory.getSlackNotification(url + "/200", proxy, proxyPort);
		w.setFilename("src/test/resources/FileThatDoesExist.txt");
		w.setEnabled(true);
		w.post();
		stopWebServer(s);
		stopProxyServer(p);
		assertTrue(w.getStatus() == HttpStatus.SC_OK);

	}

    @Ignore
	@Test
	public void test_302() throws FileNotFoundException, IOException, Exception {
		SlackNotificationTestServer s = startWebServer();
		SlackNotification w = factory.getSlackNotification(url + "/302");
		w.setEnabled(true);
		w.post();
		stopWebServer(s);
		assertTrue(w.getStatus() == HttpStatus.SC_MOVED_TEMPORARILY);

	}

    @Ignore
	@Test
	public void test_302WithProxy() throws FileNotFoundException, IOException, InterruptedException {
		SlackNotificationTestServer s = startWebServer();
		SlackNotificationTestProxyServer p = startProxyServer();
		SlackNotification w = factory.getSlackNotification(url + "/302", proxy, proxyPort);
		w.setEnabled(true);
		w.post();
		stopWebServer(s);
		stopProxyServer(p);
		assertTrue(w.getStatus() == HttpStatus.SC_MOVED_TEMPORARILY);

	}

    @Ignore
	@Test
	public void test_404WithProxyStringPort() throws FileNotFoundException, IOException, InterruptedException {
		SlackNotificationTestServer s = startWebServer();
		SlackNotificationTestProxyServer p = startProxyServer();
		SlackNotification w = factory.getSlackNotification(url + "/404", proxy, proxyPortString);
		w.setEnabled(true);
		w.post();
		stopWebServer(s);		
		stopProxyServer(p);
		assertTrue(w.getStatus() == HttpStatus.SC_NOT_FOUND);

	}

    @Ignore
	@Test
	public void test_404WithProxyConfig() throws FileNotFoundException, IOException, InterruptedException {
		SlackNotificationTestServer s = startWebServer();
		SlackNotificationTestProxyServer p = startProxyServer();
		SlackNotificationProxyConfig pc = new SlackNotificationProxyConfig(proxy, Integer.parseInt(proxyPortString));
		SlackNotification w = factory.getSlackNotification(url + "/404", pc);
		w.setEnabled(true);
		w.post();
		stopWebServer(s);
		stopProxyServer(p);
		assertTrue(w.getStatus() == HttpStatus.SC_NOT_FOUND);
	}

    @Ignore
	@Test
	public void test_302WithFilename() throws FileNotFoundException, IOException, Exception {
		SlackNotificationTestServer s = startWebServer();
		SlackNotification w = factory.getSlackNotification(url + "/302");
		w.setFilename("src/test/resources/FileThatDoesExist.txt");
		w.setEnabled(true);
		w.post();
		stopWebServer(s);
		assertTrue(w.getStatus() == HttpStatus.SC_MOVED_TEMPORARILY);

	}

    @Ignore
	@Test
	public void test_302WithFilenameWithProxy() throws FileNotFoundException, IOException, Exception {
		SlackNotificationTestServer s = startWebServer();
		SlackNotificationTestProxyServer p = startProxyServer();
		SlackNotification w = factory.getSlackNotification(url + "/302", proxy, proxyPort);
		w.setFilename("src/test/resources/FileThatDoesExist.txt");
		w.setEnabled(true);
		w.post();
		stopWebServer(s);
		stopProxyServer(p);
		assertTrue(w.getStatus() == HttpStatus.SC_MOVED_TEMPORARILY);

	}

	@Ignore
	public void test_SlackNotificationCollection() throws SlackNotificationParameterReferenceException {
		Map <String, String> params = new HashMap<String, String>();
		params.put("system.slacknotifications.1.url", url);
		params.put("system.slacknotifications.1.enabled", "true");
		params.put("system.slacknotifications.1.parameter.1.name","fod");
		params.put("system.slacknotifications.1.parameter.1.value","baa");
		params.put("system.slacknotifications.1.parameter.2.name","slash");
		params.put("system.slacknotifications.1.parameter.2.value","dot");
		params.put("system.slacknotifications.2.url", url + "/something");
		params.put("system.slacknotifications.2.enabled", "false");
		params.put("system.slacknotifications.2.parameter.1.name","foo");
		params.put("system.slacknotifications.2.parameter.1.value","bar");
		SlackNotificationCollection whc = new SlackNotificationCollection(params);
		System.out.println("Test 1" + whc.getSlackNotifications().get(1).getParameterisedUrl());
		System.out.println("Test 2" + whc.getSlackNotifications().get(2).getParameterisedUrl());
		assertTrue(whc.getSlackNotifications().get(1).getChannel().equals(url));
		assertTrue((whc.getSlackNotifications().get(1).getParameterisedUrl().equals(url + "?fod=baa&slash=dot"))
				|| (whc.getSlackNotifications().get(1).getParameterisedUrl().equals(url + "?slash=dot&fod=baa")));
		assertTrue(whc.getSlackNotifications().get(2).getParameterisedUrl().equals(url + "/something?foo=bar"));
		assertFalse(whc.getSlackNotifications().get(1).isErrored());
	}
	
	@Ignore
	public void test_SlackNotificationCollectionWithRecursiveParameterReference() throws SlackNotificationParameterReferenceException {
		Map <String, String> params = new HashMap<String, String>();
		params.put("system.test.recursive1", "%system.test.recursive2%");
		params.put("system.test.recursive2", "blahblah");
		params.put("system.slacknotifications.1.url", url);
		params.put("system.slacknotifications.1.enabled", "true");
		params.put("system.slacknotifications.1.parameter.1.name","foo");
		params.put("system.slacknotifications.1.parameter.1.value","bar");
		params.put("system.slacknotifications.1.parameter.2.name","slash");
		params.put("system.slacknotifications.1.parameter.2.value","%system.test.recursive1%");
		SlackNotificationCollection whc = new SlackNotificationCollection(params);
		System.out.println("Test 1" + whc.getSlackNotifications().get(1).getParameterisedUrl());
		assertTrue(whc.getSlackNotifications().get(1).getChannel().equals(url));
		assertTrue((whc.getSlackNotifications().get(1).getParameterisedUrl().equals(url + "?foo=bar&slash=blahblah"))
				|| (whc.getSlackNotifications().get(1).getParameterisedUrl().equals(url + "?slash=blahblah&foo=bar")));
		assertFalse(whc.getSlackNotifications().get(1).isErrored());
	}

	@Ignore
	public void test_SlackNotificationCollectionWithNonExistantRecursiveParameterReference(){
		Map <String, String> params = new HashMap<String, String>();
		params.put("system.test.recursive1", "%system.test.recursive3%");
		params.put("system.test.recursive2", "blahblah");
		params.put("system.slacknotifications.1.url", url);
		params.put("system.slacknotifications.1.enabled", "true");
		params.put("system.slacknotifications.1.parameter.1.name","foo");
		params.put("system.slacknotifications.1.parameter.1.value","bar");
		params.put("system.slacknotifications.1.parameter.2.name","slash");
		params.put("system.slacknotifications.1.parameter.2.value","%system.test.recursive1%");
		SlackNotificationCollection whc = new SlackNotificationCollection(params);
		System.out.println("Test 1" + whc.getSlackNotifications().get(1).getParameterisedUrl());
		assertTrue(whc.getSlackNotifications().get(1).getChannel().equals(url));
		assertTrue(whc.getSlackNotifications().get(1).getParameterisedUrl().equals(url + "?foo=bar"));
		assertTrue(whc.getSlackNotifications().get(1).isErrored());
		System.out.println(whc.getSlackNotifications().get(1).getErrorReason());
	}

    public void test_SlackNotificationCollectionWithPost() throws SlackNotificationParameterReferenceException, InterruptedException {
		Map <String, String> params = new HashMap<String, String>();
		//params.put("system.slacknotifications.1.url", url + "/200");
		params.put("system.slacknotifications.1.url", "http://localhost/slacknotifications/" );
		params.put("system.slacknotifications.1.enabled", "true");
		params.put("system.slacknotifications.1.parameter.1.name","fod");
		params.put("system.slacknotifications.1.parameter.1.value","baa");
		params.put("system.slacknotifications.1.parameter.2.name","slash");
		params.put("system.slacknotifications.1.parameter.2.value","dot");
		params.put("system.slacknotifications.2.url", "http://localhost/slacknotifications/test/" );
		params.put("system.slacknotifications.2.enabled", "true");
		SlackNotificationCollection whc = new SlackNotificationCollection(params);
		SlackNotificationTestServer s = startWebServer();
		for (Iterator<SlackNotification> i = whc.getSlackNotificationsAsCollection().iterator(); i.hasNext();){
			SlackNotification wh = i.next();
			try {
					wh.post();					
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//Loggers.SERVER.error(e.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//Loggers.SERVER.error(e.toString());
			}
		}
		stopWebServer(s);
		assertTrue(whc.getSlackNotifications().get(1).getStatus() == HttpStatus.SC_OK);
		assertTrue(whc.getSlackNotifications().get(2).getStatus() == HttpStatus.SC_NOT_FOUND);
	}
	*/
	public SlackNotificationTestServer startWebServer(){
		try {
			SlackNotificationTestServer s = new SlackNotificationTestServer(webserverHost, webserverPort);
			s.server.start();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void stopWebServer(SlackNotificationTestServer s) throws InterruptedException {
		try {
			s.server.stop();
			// Sleep to let the server shutdown cleanly.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Thread.sleep(1000);
		}
	}

	public SlackNotificationTestProxyServer startProxyServer(){
		try {
			SlackNotificationTestProxyServer p = new SlackNotificationTestProxyServer(webserverHost, proxyserverPort);
			p.server.start();
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public SlackNotificationTestProxyServer startProxyServerAuth(String username, String password){
		try {
			SlackNotificationTestProxyServer p = new SlackNotificationTestProxyServer(webserverHost, proxyserverPort,
					username, password);
			p.server.start();
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void stopProxyServer(SlackNotificationTestProxyServer p) {
		try {
			p.server.stop();
			// Sleep to let the server shutdown cleanly.
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
