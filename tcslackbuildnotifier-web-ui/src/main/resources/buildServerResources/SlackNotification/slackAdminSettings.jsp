<%@ include file="/include.jsp"%>

<c:url value="/slackNotifier/adminSettings.html" var="actionUrl" />

<div id="settingsContainer">
  <form action="${url}'/>" method="post" onsubmit="return Jabber.SettingsForm.submitSettings()" autocomplete="off">
    <div class="editNotificatorSettingsPage">
          <c:choose>
            <c:when test="${slackSettings.enabled}">
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
    </div>
  </form>
</div