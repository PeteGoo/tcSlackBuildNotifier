<%@ include file="/include.jsp" %>
	<div><h3 class="title">SlackNotifications configured for ${projectName}</h3>
	
<c:if test="${noProjectSlackNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<p>There are no SlackNotifications configured for this project.</p>
		<a href="./slacknotifications/index.html?projectId=${projectExternalId}">Add project SlackNotifications</a>.
		</div>
	</div>
</c:if>
<c:if test="${projectSlackNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<c:if test="${projectSlackNotificationsDisabled}" >
			<div><strong>WARNING: Slacknotification processing is currently disabled for this project</strong></div>
		</c:if>
		<p>There are <strong>${projectSlackNotificationCount}</strong> SlackNotifications configured for all builds in this project.
			<a href="./slacknotifications/index.html?projectId=${projectExternalId}">Edit project SlackNotifications</a>.</p>
		<table class="testList dark borderBottom">
			<thead><tr><th class=name>URL</th><th class=name>Enabled</th></tr></thead>
			<tbody>
			<c:forEach items="${projectSlackNotificationList}" var="hook">
				<tr><td><c:out value="${hook.url}" /></td><td><c:out value="${hook.enabledListAsString}" /></td></tr>  
			</c:forEach>
			</tbody>
		</table>
		</div>
	</div>
</c:if>

<div style='margin-top: 2.5em;'><h3 class="title">SlackNotifications configured for ${projectName} &gt; ${buildName}</h3>

<c:if test="${noBuildSlackNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<p>There are no SlackNotifications configured for this specific build.</p>
		<a href="./slacknotifications/index.html?buildTypeId=${buildExternalId}">Add build SlackNotifications</a>.
		</div>
	</div>
</c:if>
<c:if test="${buildSlackNotifications}" >
		<div style='margin-left: 1em; margin-right:1em;'>
		<p>There are <strong>${buildSlackNotificationCount}</strong> SlackNotifications for this specific build.
			<a href="./slacknotifications/index.html?buildTypeId=${buildExternalId}">Edit build SlackNotifications</a>.</p>
		<table class="testList dark borderBottom">
			<thead><tr><th class=name>URL</th><th class=name>Enabled</th></tr></thead>
			<tbody>
			<c:forEach items="${buildSlackNotificationList}" var="hook">
				<tr><td><c:out value="${hook.url}" /></td><td><c:out value="${hook.enabledListAsString}" /></td></tr>  
			</c:forEach>
			</tbody>
		</table>
		</div>
	</div>
</c:if>