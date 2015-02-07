<%@ include file="/include.jsp"%>

<c:url value="/slackNotifier/adminSettings.html" var="actionUrl" />
<bs:linkCSS dynamic="${true}">
  ${jspHome}SlackNotification/css/adminStyles.css
</bs:linkCSS>



<div id="settingsContainer">
  <form action="${actionUrl}" id="slackNotifierAdminForm" method="post" onsubmit="return SlackNotifierAdmin.save()" >
    <div class="editNotificatorSettingsPage">
          <c:choose>
            <c:when test="${disabled}">
              <div class="pauseNote" style="margin-bottom: 1em;">
                The notifier is <strong>disabled</strong>. All slack notifications are suspended&nbsp;&nbsp;<a class="btn btn_mini" href="#" id="enable-btn">Enable</a>
              </div>
            </c:when>
            <c:otherwise>
              <div style="margin-left: 0.6em;">
                The notifier is <strong>enabled</strong>&nbsp;&nbsp;<a class="btn btn_mini" href="#" id="disable-btn">Disable</a>
              </div>
            </c:otherwise>
          </c:choose>
          <bs:messages key="message" />
          <br/>
          <div style="slack-config-errors" id="slackNotificationErrors">
          </div>

          <table class="runnerFormTable">
                <tr class="groupingTitle">
                        <td colspan="2">General Configuration</td>
                </tr>
                <tr>
                    <th>
                        <label for="teamName">Team name: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="teamName" value="${teamName}" style="width: 300px;" />
                        <span class="smallNote">This must be the name as shown in the URL of your Slack team page. e.g. myslackteam.slack.com would mean you enter myslackteam.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="token">API token: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="token" value="${token}" style="width: 300px;" />
                        <span class="smallNote">A user OAuth token for your team. You can get this from the <a href="https://api.slack.com/web" target="_blank">api page</a> when you are signed in to your team. If you are using the "Incoming WebHooks" integration you can simply enter the webhook URL in here instead.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="botName">Bot name: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="botName" value="${botName}" style="width: 300px;" />
                        <span class="smallNote">The name that will be displayed by your bot messages. Mostly you can leave this as <b>TeamCity</b></span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="iconUrl">Icon url: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="iconUrl" value="${iconUrl}" style="width: 300px;" />
                        <span class="smallNote">The url of the icon to appear beside your bot. You can leave this as the default or customize it to a 32*32 png file.</span>
                        <span class="smallNote">The default is <b>https://raw.githubusercontent.com/PeteGoo/tcSlackBuildNotifier/master/docs/TeamCity32.png</b>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="defaultChannel">Default channel: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="defaultChannel" value="${defaultChannel}" style="width: 300px;" />
                        <span class="smallNote">The default channel. Please include the # if it is a channel. e.g. #general. You can also send directly toa  single user via the slackbot channel using @username</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="showBuildAgent">Show build agent: </label>
                    </th>
                    <td>
                        <forms:checkbox name="showBuildAgent" checked="${showBuildAgent}" value="${showBuildAgent}"/>
                        <span style="color: #888; font-size: 90%;">When checked, the name of the build agent will be shown in the notification.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="showElapsedBuildTime">Show elapsed build time: </label>
                    </th>
                    <td>
                        <forms:checkbox name="showElapsedBuildTime" checked="${showElapsedBuildTime}" value="${showElapsedBuildTime}"/>
                        <span style="color: #888; font-size: 90%;">When checked, the elapsed time taken to complete the build is displayed in the notification.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="showCommitters">Show committers: </label>
                    </th>
                    <td>
                        <forms:checkbox name="showCommitters" checked="${showCommitters}" value="${showCommitters}"/>
                        <span style="color: #888; font-size: 90%;">When checked, the committers responsible for the changes in the build will be displayed in the notification.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="showCommits">Show commits: </label>
                    </th>
                    <td>
                        <forms:checkbox name="showCommits" checked="${showCommits}" value="${showCommits}"/>
                        <span style="color: #888; font-size: 90%;">When checked, the commits, the username and the commit message for each change will be displayed in the notification.</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="maxCommitsToDisplay">Max commits to display: <l:star /></label>
                    </th>
                    <td>
                        <forms:textField name="maxCommitsToDisplay" value="${maxCommitsToDisplay}" style="width: 70px;" />
                        <span class="smallNote">The default channel. Please include the # if it is a channel. e.g. #general. You can also send directly toa  single user via the slackbot channel using @username</span>
                    </td>
                </tr>
           </table>
            <div class="saveButtonsBlock">
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

<script type="text/javascript">
	(function($) {
		var sendAction = function(enable) {
			$.post("${actionUrl}?action=" + (enable ? 'enable' : 'disable'),
					function() {
						BS.reload(true);
					});
			return false;
		};
		$("#enable-btn").click(function() {
			return sendAction(true);
		});
		$("#disable-btn")
            .click(
                function() {
                    if (!confirm("Slack notifications will not be sent until enabled. Disable the notifier?"))
                        return false;
                    return sendAction(false);
                });
	})(jQuery);
</script>