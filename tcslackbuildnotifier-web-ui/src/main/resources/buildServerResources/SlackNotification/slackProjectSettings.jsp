<%@ include file="/include.jsp"%>

<bs:linkCSS dynamic="${true}">
  ${jspHome}SlackNotification/css/adminStyles.css
</bs:linkCSS>

<div id="settingsContainer">
  <form action="${actionUrl}" id="slackNotifierAdminForm" method="post" onsubmit="return SlackNotifierAdmin.save()" >
    <div class="editNotificatorSettingsPage">
          <div>
             <span class="slackNotifierVersionInfo">Version: <c:out value='${pluginVersion}'/>&nbsp;<a href="https://github.com/petegoo/tcSlackBuildNotifier" class="helpIcon" style="vertical-align: middle;" target="_blank"><bs:helpIcon/></a></span>
          </div>
          <p>Leave the following blank to keep the global settings defined in the TeamCity Administration section. Anything you enter here will override the global settings.</p>
          <bs:messages key="message" />
          <br/>
          <div style="slack-config-errors" id="slackNotificationErrors">
          </div>

          <table class="runnerFormTable">
                <tr class="groupingTitle">
                        <td colspan="2">General Configuration&nbsp;<a href="https://github.com/petegoo/tcSlackBuildNotifier" class="helpIcon" style="vertical-align: middle;" target="_blank"><bs:helpIcon/></a></td>
                </tr>
                <tr>
                    <th>
                        <label for="teamName">Team name: </label>
                    </th>
                    <td>
                        <forms:textField name="teamName" value="${teamName}" style="width: 300px;" />
                        <span class="smallNote">This must be the name as shown in the URL of your Slack team page. e.g. myslackteam.slack.com would mean you enter myslackteam.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="token">API token: </label>
                    </th>
                    <td>
                        <forms:textField name="token" value="${token}" style="width: 300px;" />
                        <span class="smallNote">A user OAuth token for your team. You can get this from the <a href="https://api.slack.com/web" target="_blank">api page</a> when you are signed in to your team. If you are using the "Incoming WebHooks" integration you can simply enter the webhook URL in here instead.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="defaultChannel">Default channel: </label>
                    </th>
                    <td>
                        <forms:textField name="defaultChannel" value="${defaultChannel}" style="width: 300px;" />
                        <span class="smallNote">The default channel. Please include the # if it is a channel. e.g. #general. You can also send directly to a single user via the slackbot channel using @username</span>
                    </td>
                </tr>
           </table>
            <div class="saveButtonsBlock">
                <input type="hidden" id="publicKey" name="publicKey" value="<c:out value='${hexEncodedPublicKey}'/>"/>
                <forms:submit label="Save" />
                <forms:submit id="testConnection" type="button" label="Send test notification" onclick="return SlackNotifierAdmin.sendTestNotification()"/>
                <forms:saving />
            </div>
    </div>
  </form>

  <bs:linkScript>
    ${jspHome}SlackNotification/js/slackNotifierAdmin.js
  </bs:linkScript>
</div>