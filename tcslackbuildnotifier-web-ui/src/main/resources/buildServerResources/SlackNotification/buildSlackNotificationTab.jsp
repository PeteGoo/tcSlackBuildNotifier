<%@ include file="/include.jsp" %>
	<div><h3 class="title">Slack notifications configured for ${projectName}</h3>
	
<c:if test="${noProjectSlackNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<p>There are no Slack notifications configured for this project.</p>
		<a href="./slacknotifications/index.html?projectId=${projectExternalId}">Add project SlackNotifications</a>.
		</div>
	</div>
</c:if>
<c:if test="${projectSlackNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<c:if test="${projectSlackNotificationsDisabled}" >
			<div><strong>WARNING: Slack notification processing is currently disabled for this project</strong></div>
		</c:if>
		<p>There are <strong>${projectSlackNotificationCount}</strong> Slack notifications configured for all builds in this project.
			<a href="./slacknotifications/index.html?projectId=${projectExternalId}">Edit project Slack notifications</a>.</p>
		<table class="testList dark borderBottom">
			<thead><tr><th class=name>Channel</th><th class=name>Enabled</th></tr></thead>
			<tbody>
			<c:forEach items="${projectSlackNotificationList}" var="notification">
				<tr><td><c:out value="${notification.channel}" /></td><td><c:out value="${notification.enabledListAsString}" /></td></tr>
			</c:forEach>
			</tbody>
		</table>
		</div>
	</div>
</c:if>

<div style='margin-top: 2.5em;'><h3 class="title">Slack notifications configured for ${projectName} &gt; ${buildName}</h3>

<c:if test="${noBuildSlackNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<p>There are no Slack notifications configured for this specific build.</p>
		<a href="./slacknotifications/index.html?buildTypeId=${buildExternalId}">Add build Slack notifications</a>.
		</div>
	</div>
</c:if>
<c:if test="${buildSlackNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<p>There are <strong>${buildSlackNotificationCount}</strong> Slack notifications for this specific build.
			<a href="./slacknotifications/index.html?buildTypeId=${buildExternalId}">Edit build Slack notifications</a>.</p>
		<table class="testList dark borderBottom">
			<thead><tr><th class=name>Channel</th><th class=name>Enabled</th></tr></thead>
			<tbody>
			<c:forEach items="${buildSlackNotificationList}" var="notification">
				<tr><td><c:out value="${notification.channel}" /></td><td><c:out value="${channel.enabledListAsString}" /></td></tr>
			</c:forEach>
			</tbody>
		</table>
		</div>
	</div>
</c:if>