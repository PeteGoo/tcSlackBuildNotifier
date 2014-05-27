<%@ include file="/include.jsp" %>
	<div>
	
	<c:forEach items="${projectAndParents}" var="project"> 
		<h3 class="title">SlackNotifications configured for ${project.project.fullName}</h3>
		<c:if test="${project.projectSlacknotificationCount == 0}" >
				<div style='margin-left: 1em; margin-right:1em;'>
				<p>There are no SlackNotifications configured for this project.</p>
				<a href="./slacknotifications/index.html?projectId=${project.externalProjectId}">Add project SlackNotifications</a>.
				</div>
		</c:if>
		<c:if test="${project.projectSlacknotificationCount > 0}" >
				<div style='margin-left: 1em; margin-right:1em;'>
				<c:if test="${not project.slackNotificationProjectSettings.isEnabled()}" >
					<div><strong>WARNING: Slacknotification processing is currently disabled for this project</strong></div>
				</c:if>
				<p>There are <strong>${project.projectSlacknotificationCount}</strong> SlackNotifications configured for all builds in this project.
					<a href="./slacknotifications/index.html?projectId=${project.externalProjectId}">Edit project SlackNotifications</a>.</p>
				<table class="testList dark borderBottom">
					<thead><tr><th class=name>URL</th><th class=name>Enabled</th></tr></thead>
					<tbody>
					<c:forEach items="${project.projectSlacknotifications}" var="hook">
						<tr><td><c:out value="${hook.url}" /></td><td><c:out value="${hook.enabledListAsString}" /></td></tr>  
					</c:forEach>
					</tbody>
				</table>
				</div>
		</c:if>

			<c:forEach items="${project.buildSlacknotifications}" var="config">

				<div style='margin-top: 2.5em;'><h3 class="title">SlackNotifications configured for ${projectName} &gt; ${config.buildName}</h3>
				
				<c:if test="${config.hasNoBuildSlackNotifications()}" >
						<div style='margin-left: 1em; margin-right:1em;'>
						<p>There are no SlackNotifications configured for this specific build.</p>
						<a href="./slacknotifications/index.html?buildTypeId=${config.buildExternalId}">Add build SlackNotifications</a>.
						</div>
					</div>
				</c:if>
				<c:if test="${config.hasBuildSlackNotifications()}" >
						<div style='margin-left: 1em; margin-right:1em;'>
						<p>There are <strong>${config.buildCount}</strong> SlackNotifications for this specific build.
							<a href="./slacknotifications/index.html?buildTypeId=${config.buildExternalId}">Edit build SlackNotifications</a>.</p>
						<table class="testList dark borderBottom">
							<thead><tr><th class=name>URL</th><th class=name>Enabled</th></tr></thead>
							<tbody>
							<c:forEach items="${config.buildSlackNotificationList}" var="hook">
								<tr><td><c:out value="${hook.url}" /></td><td><c:out value="${hook.enabledListAsString}" /></td></tr>  
							</c:forEach>
							</tbody>
						</table>
						</div>
					</div>
				</c:if>
				
			</c:forEach>
			
		</c:forEach>
		</div>