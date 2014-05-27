<%@ include file="/include.jsp" %>
	<div>
	
	<c:forEach items="${projectAndParents}" var="project"> 
		<h3 class="title">Slack notifications configured for ${project.project.fullName}</h3>
		<c:if test="${project.projectSlacknotificationCount == 0}" >
				<div style='margin-left: 1em; margin-right:1em;'>
				<p>There are no Slack notifications configured for this project.</p>
				<a href="./slacknotifications/index.html?projectId=${project.externalProjectId}">Add project Slack notifications</a>.
				</div>
		</c:if>
		<c:if test="${project.projectSlacknotificationCount > 0}" >
				<div style='margin-left: 1em; margin-right:1em;'>
				<c:if test="${not project.slackNotificationProjectSettings.isEnabled()}" >
					<div><strong>WARNING: Slack notification processing is currently disabled for this project</strong></div>
				</c:if>
				<p>There are <strong>${project.projectSlacknotificationCount}</strong> Slack notifications configured for all builds in this project.
					<a href="./slacknotifications/index.html?projectId=${project.externalProjectId}">Edit project Slack notifications</a>.</p>
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

				<div style='margin-top: 2.5em;'><h3 class="title">Slack notifications configured for ${projectName} &gt; ${config.buildName}</h3>
				
				<c:if test="${config.hasNoBuildSlackNotifications()}" >
						<div style='margin-left: 1em; margin-right:1em;'>
						<p>There are no Slack notifications configured for this specific build.</p>
						<a href="./slacknotifications/index.html?buildTypeId=${config.buildExternalId}">Add build Slack notifications</a>.
						</div>
					</div>
				</c:if>
				<c:if test="${config.hasBuildSlackNotifications()}" >
						<div style='margin-left: 1em; margin-right:1em;'>
						<p>There are <strong>${config.buildCount}</strong> Slack notifications for this specific build.
							<a href="./slacknotifications/index.html?buildTypeId=${config.buildExternalId}">Edit build Slack notifications</a>.</p>
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