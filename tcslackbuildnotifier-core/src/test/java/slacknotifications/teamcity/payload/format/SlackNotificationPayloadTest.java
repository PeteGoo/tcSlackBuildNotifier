package slacknotifications.teamcity.payload.format;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.SortedMap;
import java.util.TreeMap;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SFinishedBuild;

import org.junit.Test;

import slacknotifications.teamcity.MockSBuildType;
import slacknotifications.teamcity.MockSProject;
import slacknotifications.teamcity.MockSRunningBuild;
import slacknotifications.teamcity.payload.SlackNotificationPayloadDefaultTemplates;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;

public class SlackNotificationPayloadTest {
	
	@Test
	public void test_Xml(){
		MockSBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
		String triggeredBy = "SubVersion";
		MockSRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, triggeredBy, Status.NORMAL, "Running", "TestBuild01");
		SFinishedBuild previousBuild = mock(SFinishedBuild.class);
		MockSProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
		sBuildType.setProject(sProject);
		SBuildServer mockServer = mock(SBuildServer.class);
		when(mockServer.getRootUrl()).thenReturn("http://test.url");

		
		SlackNotificationPayloadManager wpm = new SlackNotificationPayloadManager(mockServer);
		SlackNotificationPayloadXml whp = new SlackNotificationPayloadXml(wpm);
		whp.register();
		SortedMap<String, String> extraParameters = new TreeMap<String, String>();
		
		extraParameters.put("item1", "content1");
		extraParameters.put("item2", "content2");
		extraParameters.put("item3", "content3");
		extraParameters.put("item4", "content4");
		extraParameters.put("item5", "content5");
		
		System.out.println(sRunningBuild.getBuildDescription());
		assertTrue(wpm.getFormat("xml").getContentType().equals("text/xml"));
		assertTrue(wpm.getFormat("xml").getFormatDescription().equals("XML"));
		System.out.println(wpm.getFormat("xml").buildFinished(sRunningBuild, previousBuild, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates()));
	}
	
	
	@Test
	public void test_Json(){
		MockSBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
		String triggeredBy = "SubVersion";
		MockSRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, triggeredBy, Status.NORMAL, "Running", "TestBuild01");
		SFinishedBuild previousBuild = mock(SFinishedBuild.class);
		MockSProject sProject = new MockSProject("Test Project", "A test project", "project1", "ATestProject", sBuildType);
		sBuildType.setProject(sProject);
		SBuildServer mockServer = mock(SBuildServer.class);
		when(mockServer.getRootUrl()).thenReturn("http://test.url");

		
		SlackNotificationPayloadManager wpm = new SlackNotificationPayloadManager(mockServer);
		SlackNotificationPayloadJson whp = new SlackNotificationPayloadJson(wpm);
		whp.register();
		SortedMap<String, String> extraParameters = new TreeMap<String, String>();
		
		extraParameters.put("item1", "content1");
		extraParameters.put("item2", "content2");
		extraParameters.put("item3", "content3");
		extraParameters.put("item4", "content4");
		extraParameters.put("item5", "content5");
		
		System.out.println(sRunningBuild.getBuildDescription());
		assertTrue(wpm.getFormat("json").getContentType().equals("application/json"));
		assertTrue(wpm.getFormat("json").getFormatDescription().equals("JSON"));
		System.out.println(wpm.getFormat("json").buildStarted(sRunningBuild, previousBuild, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates()));
	}
	
	@Test
	public void test_NvPairs(){
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
		
		extraParameters.put("item1", "content1");
		extraParameters.put("item2", "content2");
		extraParameters.put("item3", "content3");
		extraParameters.put("item4", "content4");
		extraParameters.put("item5", "content5");
		
		System.out.println(sRunningBuild.getBuildDescription());
		assertTrue(wpm.getFormat("nvpairs").getContentType().equals("application/x-www-form-urlencoded"));
		assertTrue(wpm.getFormat("nvpairs").getFormatDescription().equals("Name Value Pairs"));
		System.out.println(wpm.getFormat("nvpairs").buildStarted(sRunningBuild, previousBuild, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates()));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void test_Empty(){
		MockSBuildType sBuildType = new MockSBuildType("Test Build", "A Test Build", "bt1");
		String triggeredBy = "SubVersion";
		MockSRunningBuild sRunningBuild = new MockSRunningBuild(sBuildType, triggeredBy, Status.NORMAL, "Running","TestBuild01");
		SFinishedBuild previousBuild = mock(SFinishedBuild.class);
		SBuildServer mockServer = mock(SBuildServer.class);
		when(mockServer.getRootUrl()).thenReturn("http://test.url");

		
		SlackNotificationPayloadManager wpm = new SlackNotificationPayloadManager(mockServer);
		SlackNotificationPayloadEmpty whp = new SlackNotificationPayloadEmpty(wpm);
		whp.register();
		SortedMap<String, String> extraParameters = new TreeMap<String, String>();
		
		extraParameters.put("item1", "content1");
		extraParameters.put("item2", "content2");
		extraParameters.put("item3", "content3");
		extraParameters.put("item4", "content4");
		extraParameters.put("item5", "content5");
		
		System.out.println(sRunningBuild.getBuildDescription());
		wpm.getFormat("empty").setRank(5000);
		
		assertTrue(wpm.getFormat("empty").getRank().equals(5000));
		assertTrue(wpm.getFormat("empty").getContentType().equals("text/plain"));
		assertTrue(wpm.getFormat("empty").getFormatDescription().equals("None"));
		assertTrue(wpm.getFormat("empty").getCharset().equals("UTF-8"));
		assertTrue(wpm.getFormat("empty").buildStarted(sRunningBuild, previousBuild, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates()).equals(""));
		assertTrue(wpm.getFormat("empty").beforeBuildFinish(sRunningBuild, previousBuild, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates()).equals(""));
		assertTrue(wpm.getFormat("empty").buildChangedStatus(sRunningBuild, previousBuild, Status.NORMAL, Status.ERROR, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates()).equals(""));
		assertTrue(wpm.getFormat("empty").buildFinished(sRunningBuild, previousBuild, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates()).equals(""));
		assertTrue(wpm.getFormat("empty").buildInterrupted(sRunningBuild, previousBuild, extraParameters, SlackNotificationPayloadDefaultTemplates.getDefaultEnabledPayloadTemplates()).equals(""));
		
	}
	
	@Test
	public void test_Null(){
		SBuildServer mockServer = mock(SBuildServer.class);
		when(mockServer.getRootUrl()).thenReturn("http://test.url");

		
		SlackNotificationPayloadManager wpm = new SlackNotificationPayloadManager(mockServer);
		assertTrue(wpm.getRegisteredFormats().isEmpty());
		assertTrue(wpm.getRegisteredFormatsAsCollection().isEmpty());
		assertNull(wpm.getFormat("SomethingThatDoesNotExist"));
	}
	
	
}
