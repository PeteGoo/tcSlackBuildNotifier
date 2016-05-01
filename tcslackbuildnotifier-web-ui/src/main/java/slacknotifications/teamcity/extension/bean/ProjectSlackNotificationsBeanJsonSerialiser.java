package slacknotifications.teamcity.extension.bean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class ProjectSlackNotificationsBeanJsonSerialiser {
	private ProjectSlackNotificationsBeanJsonSerialiser(){}
	public static String serialise(ProjectSlackNotificationsBean project){
		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("projectSlacknotificationConfig", ProjectSlackNotificationsBean.class);
        /* For some reason, the items are coming back as "@name" and "@value"
         * so strip those out with a regex.
         */
		return xstream.toXML(project);
	}

}
