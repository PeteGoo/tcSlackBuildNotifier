package slacknotifications.teamcity.payload.util;


import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import slacknotifications.teamcity.BuildStateEnum;
import slacknotifications.teamcity.payload.content.ExtraParametersMap;
import slacknotifications.teamcity.payload.content.SlackNotificationPayloadContent;
import slacknotifications.testframework.SlackNotificationMockingFramework;
import slacknotifications.testframework.SlackNotificationMockingFrameworkImpl;

public class BeanUtilsTest {
	
	private ExtraParametersMap extraParameters;

	@Test
	public void testBeanUtils() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		extraParameters = new ExtraParametersMap(new TreeMap<String, String>());
		SlackNotificationMockingFramework framework = SlackNotificationMockingFrameworkImpl.create(BuildStateEnum.BEFORE_BUILD_FINISHED, extraParameters);
		SlackNotificationPayloadContent content = framework.getSlackNotificationContent();
		String buildFullName = (String) PropertyUtils.getProperty(content, "buildFullName");
		assertTrue(buildFullName.equals("Test Project :: Test Build"));
		
	}

}
