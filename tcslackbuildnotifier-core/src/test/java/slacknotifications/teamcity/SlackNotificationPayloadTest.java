package slacknotifications.teamcity;

import org.junit.Test;
import slacknotifications.Attachment;
import slacknotifications.SlackNotificationImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class SlackNotificationPayloadTest {


    @Test
    public void TestAttachmentListToJson()
    {
        List<Attachment> attachmentList = new ArrayList<Attachment>();
        Attachment attachment = new Attachment("fallback", "text", "pretext", "#ff0000");
        attachmentList.add(attachment);

        String json = SlackNotificationImpl.convertAttachmentsToJson(attachmentList);

        assertNotNull(json);
        assertNotSame("", json);

        assertTrue(json.startsWith("["));

        System.out.println(json);
    }

}
