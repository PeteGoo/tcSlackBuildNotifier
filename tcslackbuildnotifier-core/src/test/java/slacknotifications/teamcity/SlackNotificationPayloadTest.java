package slacknotifications.teamcity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SFinishedBuild;

import org.junit.Ignore;
import org.junit.Test;

import org.springframework.util.Assert;
import slacknotifications.*;
import slacknotifications.SlackNotification;
import slacknotifications.SlackNotificationImpl;
import slacknotifications.teamcity.payload.SlackNotificationPayloadDefaultTemplates;
import slacknotifications.teamcity.payload.SlackNotificationPayloadManager;
import slacknotifications.teamcity.settings.SlackNotificationConfig;
import slacknotifications.teamcity.settings.SlackNotificationProjectSettings;


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
