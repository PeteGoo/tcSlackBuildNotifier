<%@ include file="/include.jsp" %>
<c:set var="title" value="SlackNotifications" scope="request"/>
<bs:page>

    <jsp:attribute name="head_include">
      <bs:linkCSS>
        /css/admin/adminMain.css
        /css/admin/projectConfig.css
        /css/forms.css
        /css/admin/vcsRootsTable.css
        
    /css/visibleProjects.css
    /css/addSidebar.css
    /css/settingsTable.css
    /css/profilePage.css
    /css/userRoles.css
    
    ${jspHome}SlackNotification/css/styles.css
        
      </bs:linkCSS>
      <bs:linkScript>
        /js/bs/blocks.js
        /js/bs/blocksWithHeader.js
        /js/bs/forms.js
        /js/bs/modalDialog.js
        /js/bs/editBuildType.js
        /js/bs/editProject.js
        /js/bs/adminActions.js
      </bs:linkScript>

      <script type="text/javascript">
        BS.Navigation.items = [
		  {title: "Projects", url: '<c:url value="/overview.html"/>'},
		  <c:if test="${haveProject}"> 
		  	{title: "${projectName}", url: '<c:url value="/project.html?projectId=${projectExternalId}"/>'},
		  </c:if>
		  <c:if test="${haveBuild}"> 
		  	{title: "${buildName}", url: '<c:url value="/viewType.html?buildTypeId=${buildExternalId}"/>'},
		  </c:if>
          {title: "${title}", selected:true}
        ];
    
      </script>
    </jsp:attribute>
    
    
      
    <jsp:attribute name="body_include">
    <c:if test="${includeJquery}">
    	<script type=text/javascript src="..${jspHome}SlackNotification/js/jquery-1.4.3.min.js"></script>
    </c:if>
	<script type=text/javascript src="..${jspHome}SlackNotification/js/jquery.easytabs.min.js"></script>
    <script type=text/javascript>
		var jQuerySlacknotification = jQuery.noConflict();
		var slacknotificationDialogWidth = -1;
		jQuerySlacknotification(document).ready( function() {
				jQuerySlacknotification('#tab-container').easytabs({
					  animate: false,
					  updateHash: false
				});
		});

		function selectBuildState(){
			doExtraCompleted();
		}

		function doExtraCompleted(){
			if(jQuerySlacknotification('#buildSuccessful').is(':checked')){
				jQuerySlacknotification('.onBuildFixed').removeClass('onCompletionDisabled');
				jQuerySlacknotification('tr.onBuildFixed td input').removeAttr('disabled');
			} else {
				jQuerySlacknotification('.onBuildFixed').addClass('onCompletionDisabled');
				jQuerySlacknotification('tr.onBuildFixed td input').attr('disabled', 'disabled');
			} 
			if(jQuerySlacknotification('#buildFailed').is(':checked')){
				jQuerySlacknotification('.onBuildFailed').removeClass('onCompletionDisabled');
				jQuerySlacknotification('tr.onBuildFailed td input').removeAttr('disabled');
			} else {
				jQuerySlacknotification('.onBuildFailed').addClass('onCompletionDisabled');
				jQuerySlacknotification('tr.onBuildFailed td input').attr('disabled', 'disabled');
			}
		}

		function toggleCustomContentEnabled(){
            if(jQuerySlacknotification('#customContentEnabled').is(':checked')){
                jQuerySlacknotification('.onCustomContentEnabled').removeClass('onCompletionDisabled');
                jQuerySlacknotification('tr.onCustomContentEnabled td input').removeAttr('disabled');
            } else {
                jQuerySlacknotification('.onCustomContentEnabled').addClass('onCompletionDisabled');
                jQuerySlacknotification('tr.onCustomContentEnabled td input').attr('disabled', 'disabled');
            }
		}
		
		function toggleAllBuildTypesSelected(){
			jQuerySlacknotification.each(jQuerySlacknotification('.buildType_single'), function(){
				jQuerySlacknotification(this).attr('checked', jQuerySlacknotification('input.buildType_all').is(':checked'))
			});
			updateSelectedBuildTypes();
		}
		
		function updateSelectedBuildTypes(){
			var subText = "";
		    if(jQuerySlacknotification('#buildTypeSubProjects').is(':checked')){
		    	subText = " &amp; sub-projects";
		    }
		
			if(jQuerySlacknotification('#slackNotificationFormContents input.buildType_single:checked').length == jQuerySlacknotification('#slackNotificationFormContents input.buildType_single').length){
				jQuerySlacknotification('input.buildType_all').attr('checked', true);
				jQuerySlacknotification('span#selectedBuildCount').html("all" + subText);
			} else {
				jQuerySlacknotification('input.buildType_all').attr('checked', false);
				jQuerySlacknotification('span#selectedBuildCount').html(jQuerySlacknotification('#slackNotificationFormContents input.buildType_single:checked').length + subText);
			}

		}
		
		function populateSlackNotificationDialog(id){
			jQuerySlacknotification('#buildList').empty();
			jQuerySlacknotification.each(ProjectBuilds.projectSlacknotificationConfig.slackNotificationList, function(thing, config){
				if (id === config[0]){
					var slacknotification = config[1];
				
					jQuerySlacknotification('#slackNotificationId').val(slacknotification.uniqueKey);
					jQuerySlacknotification('#slackNotificationToken').val(slacknotification.token);
					jQuerySlacknotification('#slackNotificationChannel').val(slacknotification.channel);
					jQuerySlacknotification('#filterBranchName').val(slacknotification.filterBranchName);
				    jQuerySlacknotification('#slackNotificationsEnabled').attr('checked', slacknotification.enabled);
				    jQuerySlacknotification.each(slacknotification.states, function(name, value){
				    	jQuerySlacknotification('#' + value.buildStateName).attr('checked', value.enabled);
				    });
				    
                    jQuerySlacknotification('#buildTypeSubProjects').attr('checked', slacknotification.subProjectsEnabled);
					jQuerySlacknotification.each(slacknotification.builds, function(){
						 if (this.enabled){
					 	 	jQuerySlacknotification('#buildList').append('<p style="border-bottom:solid 1px #cccccc; margin:0; padding:0.5em;"><label><input checked onclick="updateSelectedBuildTypes();" type=checkbox style="padding-right: 1em;" name="buildTypeId" value="' + this.buildTypeId + '"class="buildType_single">' + this.buildTypeName + '</label></p>');
						 } else {
						 	 jQuerySlacknotification('#buildList').append('<p style="border-bottom:solid 1px #cccccc; margin:0; padding:0.5em;"><label><input onclick="updateSelectedBuildTypes();" type=checkbox style="padding-right: 1em;" name="buildTypeId" value="' + this.buildTypeId + '"class="buildType_single">' + this.buildTypeName + '</label></p>');
						 }
					});
					jQuerySlacknotification('#mentionChannelEnabled').attr('checked', slacknotification.mentionChannelEnabled);
					jQuerySlacknotification('#mentionSlackUserEnabled').attr('checked', slacknotification.mentionSlackUserEnabled);
					jQuerySlacknotification('#mentionHereEnabled').attr('checked', slacknotification.mentionHereEnabled);
					jQuerySlacknotification('#mentionWhoTriggeredEnabled').attr('checked', slacknotification.mentionWhoTriggeredEnabled);
					jQuerySlacknotification('#maxCommitsToDisplay').val(slacknotification.maxCommitsToDisplay);
					jQuerySlacknotification('#customContentEnabled').attr('checked', slacknotification.customContentEnabled);
					jQuerySlacknotification('#showBuildAgent').attr('checked', slacknotification.showBuildAgent);
					jQuerySlacknotification('#showCommits').attr('checked', slacknotification.showCommits);
					jQuerySlacknotification('#showCommitters').attr('checked', slacknotification.showCommitters);
					jQuerySlacknotification('#showTriggeredBy').attr('checked', slacknotification.showTriggeredBy);
					jQuerySlacknotification('#showElapsedBuildTime').attr('checked', slacknotification.showElapsedBuildTime);
					jQuerySlacknotification('#botName').val(slacknotification.botName);
					jQuerySlacknotification('#iconUrl').val(slacknotification.iconUrl)
					jQuerySlacknotification('#showFailureReason').attr('checked', slacknotification.showFailureReason);
				}
			});
			updateSelectedBuildTypes();
		}
		
		function addSlackNotificationsFromJsonCallback(){
			jQuerySlacknotification.each(ProjectBuilds.projectSlacknotificationConfig.slackNotificationList, function(thing, config){
				if ('new' !== config[0]){
					var slacknotification = config[1];
					jQuerySlacknotification('.slackNotificationRowTemplate')
									.clone()
									.removeAttr("id")
									.attr("id", "viewRow_" + slacknotification.uniqueKey)
									.removeClass('slackNotificationRowTemplate')
									.addClass('slackNotificationRow')
									.appendTo('#slackNotificationTable > tbody');
					jQuerySlacknotification("#viewRow_" + slacknotification.uniqueKey + " > td.slackNotificationRowItemChannel").html(slacknotification.channel).click(function(){BS.EditSlackNotificationDialog.showDialog(slacknotification.uniqueKey, '#hookPane');});
					jQuerySlacknotification("#viewRow_" + slacknotification.uniqueKey + " > td.slackNotificationRowItemEvents").html(slacknotification.enabledEventsListForWeb).click(function(){BS.EditSlackNotificationDialog.showDialog(slacknotification.uniqueKey,'#hookPane');});
					jQuerySlacknotification("#viewRow_" + slacknotification.uniqueKey + " > td.slackNotificationRowItemBuilds").html(slacknotification.enabledBuildsListForWeb).click(function(){BS.EditSlackNotificationDialog.showDialog(slacknotification.uniqueKey, '#buildPane');});
					jQuerySlacknotification("#viewRow_" + slacknotification.uniqueKey + " > td.slackNotificationRowItemEdit > a").click(function(){BS.EditSlackNotificationDialog.showDialog(slacknotification.uniqueKey,'#hookPane');});
					jQuerySlacknotification("#viewRow_" + slacknotification.uniqueKey + " > td.slackNotificationRowItemDelete > a").click(function(){BS.SlackNotificationForm.removeSlackNotification(slacknotification.uniqueKey,'#hookPane');});
					
				}
			});
		}

		BS.EditSlackNotificationDialog = OO.extend(BS.AbstractModalDialog, {
			  getContainer : function() {
			    return $('editSlackNotificationDialog');
			  },

			  showDialog : function(id, tab) {
				BS.SlackNotificationForm.clearErrors();
			    
			    populateSlackNotificationDialog(id);
			    doExtraCompleted();
			    toggleCustomContentEnabled();
			    
			    var title = id == "new" ? "Add New" : "Edit";
			    title += " SlackNotification";

			    $('slackNotificationDialogTitle').innerHTML = title;


			    if (slacknotificationDialogWidth < 0){
			    	slacknotificationDialogWidth = jQuerySlacknotification('#editSlackNotificationDialog').innerWidth();
			    } else {
			    	jQuerySlacknotification('#editSlackNotificationDialog').innerWidth(slacknotificationDialogWidth);
			    }
			    
			    this.showCentered();
			    jQuerySlacknotification('#buildPane').innerHeight(jQuerySlacknotification('#hookPane').innerHeight());
				jQuerySlacknotification('#tab-container').easytabs('select', tab);
			    
			    $('slackNotificationChannel').focus();
			  },

			  cancelDialog : function() {
			    this.close();
			  }
			});

		BS.BaseSaveSlackNotificationListener = OO.extend(BS.SaveConfigurationListener, {
			  onBeginSave : function(form) {
			    form.formElement().slackNotificationChannel.value = BS.Util.trimSpaces(form.formElement().slackNotificationChannel.value);
			    form.clearErrors();
			    form.hideSuccessMessages();
			    form.disable();
			    form.setSaving(true);
			  }
			});

		BS.SlackNotificationForm = OO.extend(BS.AbstractWebForm, {
			  setSaving : function(saving) {
			    if (saving) {
			      BS.Util.show('slackNotificationSaving');
			    } else {
			      BS.Util.hide('slackNotificationSaving');
			    }
			  },

			  formElement : function() {
			    return $('SlackNotificationForm');
			  },

			  saveSlackNotification : function() {
			    this.formElement().submitAction.value = 'updateSlackNotification';
			    var that = this;

			    BS.FormSaver.save(this, this.formElement().action, OO.extend(BS.ErrorsAwareListener,
			 	{
			      onEmptySlackNotificationChannelError : function(elem) {
			        $("error_slackNotificationChannel").innerHTML = elem.firstChild.nodeValue;
			        that.highlightErrorField($('slackNotificationChannel'));
			      },



			      onCompleteSave : function(form, responseXML, err) {
			    	BS.ErrorsAwareListener.onCompleteSave(form, responseXML, err);
			        form.enable();
			        if (!err) {
			          $('systemParams').updateContainer();
			          BS.EditSlackNotificationDialog.close();
			        }
			      }
			    }));

			    return false;
			  },

			  removeSlackNotification : function(paramId) {
			    var that = this;

			    if (!confirm("Are you sure you want to delete this SlackNotification?")) return;

			    var url = this.formElement().action + "&submitAction=removeSlackNotification&removedSlackNotificationId=" + paramId;

			    BS.ajaxRequest(url, {
			      onComplete: function() {
			        $('systemParams').updateContainer();
			        BS.EditSlackNotificationDialog.close();
			      }
			    });
			  }
			});
	</script>
    <div class="editBuildPageGeneral" style="background-color:white; float:left; margin:0; padding:0; width:70%;">
    
        <c:choose>  
    		<c:when test="${haveBuild}"> 
			    <h2 class="noBorder">SlackNotifications applicable to build ${buildName}</h2>
			    To edit all slacknotifications for builds in the project <a href="index.html?projectId=${projectExternalId}">edit Project slacknotifications</a>.
         	</c:when>  
         	<c:otherwise>  
			    <h2 class="noBorder">Slack notifications configured for project ${projectName}</h2>
         	</c:otherwise>  
		</c:choose>  


  		<div id="messageArea"></div>
	    <div id="systemParams"><!--  begin systemParams div -->

		<c:choose>
			<c:when test="${not haveProject}">
				<strong>${errorReason}</strong><br/>Please access this page via the SlackNotifications tab on a project or build overview page.
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${hasPermission}">
					<%@ include file="slackNotificationInclude.jsp" %>
					</c:when>
					<c:otherwise>
						<strong>You must have Project Administrator permission to edit SlackNotifications</strong>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>

        </div><!--  end systemParams div -->
      </div>
    <div id=sidebarAdmin>
      <div class=configurationSection>
      	<h2>Slack Notification Information</h2>
          <p>Slack notifications appear in your slack channels when builds are started and/or completed. </p>

			<c:choose>
				<c:when test="${ShowFurtherReading == 'ALL'}">
				          <p>Further Reading:
				          <ul>${moreInfoText}
				          	<li><a href="https://github.com/PeteGoo/tcSlackBuildNotifier">tcSlackBuildNotifier plugin</a></li>
				          </ul>	
				</c:when>
		
				<c:when test="${ShowFurtherReading == 'DEFAULT'}">
				          <p>Further Reading:
				          <li><a href="https://github.com/PeteGoo/tcSlackBuildNotifier">tcSlackBuildNotifier plugin</a></li>
				          </ul>	
				</c:when>
		
				<c:when test="${ShowFurtherReading == 'SINGLE'}">
				          <p>Further Reading:
				          <ul>${moreInfoText}</ul>
				</c:when>
			</c:choose>

      </div>
    </div>
    <script type=text/javascript>
	        $('systemParams').updateContainer = function() {
        <c:choose>  
    		<c:when test="${haveBuild}"> 
	          	jQuerySlacknotification.get("settingsList.html?buildTypeId=${buildExternalId}", function(data) {
         	</c:when>  
         	<c:otherwise>  
	          	jQuerySlacknotification.get("settingsList.html?projectId=${projectId}", function(data) {
         	</c:otherwise>  
		</c:choose>  	        
	          		ProjectBuilds = data;
	          		jQuerySlacknotification('.slackNotificationRow').remove();
	          		addSlackNotificationsFromJsonCallback();
				});
	        }

	</script>
    </jsp:attribute>
</bs:page>
