var SlackNotifierAdmin = {

    validate : function(){
        try{
            var teamName = document.forms["slackNotifierAdminForm"]["teamName"].value;
            var token = document.forms["slackNotifierAdminForm"]["token"].value;
            var iconUrl = document.forms["slackNotifierAdminForm"]["iconUrl"].value;
            var botName = document.forms["slackNotifierAdminForm"]["botName"].value;
            var maxCommitsToDisplay = document.forms["slackNotifierAdminForm"]["maxCommitsToDisplay"].value;
            var showCommits = document.forms["slackNotifierAdminForm"]["showCommits"].checked;
            var errors = [];

            if(!teamName){
                errors.push("Team name is required.")
            }
            if(!token){
                errors.push("Api token is required.")
            }
            if(!iconUrl){
                errors.push("Icon url is required.")
            }
            if(!botName){
                errors.push("Bot name is required.")
            }
            if(!showCommits){
                if(!maxCommitsToDisplay){
                    errors.push("Max commits to display is required.")
                }
                else if(parseInt(maxCommitsToDisplay) == Number.NaN){
                    errors.push("Max commits to display must be a valid integer")
                }
            }

            $('slackNotificationErrors').innerHTML = '';


            if(errors.length > 0) {
                var errorList = jQuery('<ul></ul>');

                jQuery.each(errors, function(index, error) {
                   var li = jQuery('<li/>')
                        .addClass('slack-config-error')
                        .text(error)
                        .appendTo(errorList);
                });
                errorList.appendTo($('slackNotificationErrors'));
                return false;
            }
            else {
                return true;
            }
        }
        catch(err){
            $('slackNotificationErrors').innerHTML = 'Oops! Something went wrong!';
            return false;
        }
    },

    sendTestNotification : function() {
        if(!SlackNotifierAdmin.validate()) {
            return false;
        }

        jQuery.ajax(
            {
                url: $("slackNotifierAdminForm").action,
                data: {
                    test: 1,
                    teamName: $("teamName").value,
                    defaultChannel: $("defaultChannel").value,
                    token: $("token").value,
                    botName: $("botName").value,
                    iconUrl: $("iconUrl").value,
                    maxCommitsToDisplay: $("maxCommitsToDisplay").value,
                    showCommits: $("showCommits").checked,
                    showCommitters: $("showCommitters").checked,
                    showElapsedBuildTime: $("showElapsedBuildTime").checked,
                    showBuildAgent: $("showBuildAgent").checked
                },
                type: "GET"
            }).done(function() {
                alert("Notification sent");
            }).fail(function() {
                alert("Failed to send notification!")
            });

        return false;
    },

    save : function() {
        if(!SlackNotifierAdmin.validate()) {
            return false;
        }

        jQuery.ajax(
            {
                url: $("slackNotifierAdminForm").action,
                data: {
                    edit: 1,
                    teamName: $("teamName").value,
                    defaultChannel: $("defaultChannel").value,
                    token: $("token").value,
                    botName: $("botName").value,
                    iconUrl: $("iconUrl").value,
                    maxCommitsToDisplay: $("maxCommitsToDisplay").value,
                    showCommits: $("showCommits").checked,
                    showCommitters: $("showCommitters").checked,
                    showElapsedBuildTime: $("showElapsedBuildTime").checked,
                    showBuildAgent: $("showBuildAgent").checked
                },
                type: "POST"
            }).done(function() {
                BS.reload();
            }).fail(function() {
                alert("Failed to save configuration!")
            });

        return false;
    }
}