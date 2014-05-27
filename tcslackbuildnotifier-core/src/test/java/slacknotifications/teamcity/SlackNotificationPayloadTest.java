package slacknotifications.teamcity;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SFinishedBuild;

import org.junit.Test;

import slacknotifications.*;
import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationImpl;
import slacknotifications.teamcity.payload.SlackNotificationPayload;
import slacknotifications.teamcity.payload.SlackNotificationPayloadDefaultTemplates;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.payload.format.SlackNotificationPayloadNameValuePairs;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;


public class SlackNotificationPayloadTest {

	@Test
	public void TestNVPairsPayloadContent(){
		
		MockSBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
		String triggeredBy = "SubVersion";
		MockSRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, triggeredBy, Status.NORMAL, "Running", "TestBuild01");
		SFinishedBuild previousBuild = mock(SFinishedBuild.class); 	
		MockSProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
		sBuildType.setProject(sProject);
		SBuildServer mockServer = mock(SBuildServer.class);
		when(mockServer.getRootUrl()).thenReturn("http://test.url");
		
		SlackNotificationPayloadManager wpm = new SlackNotificationPayloadManager(mockServer);
		SlackNotificationPayloadNameValuePairs whp = new SlackNotificationPayloadNameValuePairs(wpm);
		whp.register();
		SortedMap<String, String> extraParameters = new TreeMap<String, String>();
		extraParameters.put("something", "somewhere");
		//String content = wpm.getFormat("nvpairs").buildStarted(sRunningBuild, extraParameters);
		System.out.println(sRunningBuild.getBuildDescription());
		assertTrue(wpm.getFormat("nvpairs").getContentType().equals("application/x-www-form-urlencoded"));
		assertTrue(wpm.getFormat("nvpairs").getFormatDescription().equals("Name Value Pairs"));
		System.out.println(wpm.getFormat("nvpairs").buildStarted(sRunningBuild, previousBuild, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates()));
		
	}
	
	@Test
	public void TestNVPairsPayloadWithPostToJetty() throws InterruptedException{
		
		MockSBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
		String triggeredBy = "SubVersion";
		MockSRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, triggeredBy, Status.NORMAL, "Running", "TestBuild01");
		SFinishedBuild previousBuild = mock(SFinishedBuild.class);
		MockSProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
		sBuildType.setProject(sProject);
		SBuildServer mockServer = mock(SBuildServer.class);
		when(mockServer.getRootUrl()).thenReturn("http://test.url");
		
		SlackNotificationTest test = new SlackNotificationTest();
		String url = "http://" + test.webserverHost + ":" + test.webserverPort + "/200";
		SlackNotificationTestServer s = test.startWebServer();
		
		SlackNotificationPayloadManager wpm = new SlackNotificationPayloadManager(mockServer);
		SlackNotificationPayloadNameValuePairs whp = new SlackNotificationPayloadNameValuePairs(wpm);
		whp.register();
		SlackNotificationProjectSettings whps = new SlackNotificationProjectSettings();
		
		BuildState state = new BuildState().setAllEnabled();
		whps.addNewSlackNotification("project1", url, true, state, "nvpairs", true, true, new HashSet<String>());
		
    	for (SlackNotificationConfig whc : whps.getSlackNotificationsConfigs()){
			SlackNotification wh = new SlackNotificationImpl();
			wh.setChannel(whc.getChannel());
			wh.setEnabled(whc.getEnabled());
			//slackNotification.addParams(slackNotificationConfig.getParams());
			wh.setBuildStates(whc.getBuildStates());
			//slackNotification.setProxy(whps. getProxyConfigForUrl(whc.getChannel()));
			//this.getFromConfig(slackNotification, whc);
			
			if (wpm.isRegisteredFormat(whc.getPayloadFormat())){
				//slackNotification.addParam("notifyType", state);
				//addMessageParam(sRunningBuild, slackNotification, stateShort);
				//slackNotification.addParam("buildStatus", sRunningBuild.getStatusDescriptor().getText());
				//addCommonParams(sRunningBuild, slackNotification);
				SlackNotificationPayload payloadFormat = wpm.getFormat(whc.getPayloadFormat());
				wh.setContentType(payloadFormat.getContentType());
				wh.setCharset(payloadFormat.getCharset());
				wh.setPayload(payloadFormat.buildStarted(sRunningBuild, previousBuild, whc.getParams(), whc.getEnabledTemplates()));
				if (wh.isEnabled()){
					try {
						wh.post();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						test.stopWebServer(s);
					}
				}
		
			}
    	}
		
	}

}
