tcSlackBuildNotifier
====================

[![Join the chat at https://gitter.im/PeteGoo/tcSlackBuildNotifier](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/PeteGoo/tcSlackBuildNotifier?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Overview

Posts Build Status to [Slack](http://www.slack.com)

![Sample Notification](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/build-status_pass.png)
![Sample Notification](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/build-status_fail.png)

## What's New

This is fork of https://github.com/PeteGoo/tcSlackBuildNotifier, with ability to customize message with freemarker templates, including any build parameters into the message.

For example, if the builds have any of parameters `newVersion`, `toVersion`, `testMode`, `emailToAddress` or `services`, for the message body defined as
```
<#if newVersion?has_content || toVersion?has_content>: ${newVersion}${toVersion}</#if>
<#if testMode?has_content && testMode="true"> `TEST MODE`</#if>
<#if emailToAddress?has_content> to ${emailToAddress} about</#if>
<#if services?has_content>: ${services}</#if>
```

Like this:
![Configuration](https://raw.github.com/Ecwid/tcSlackBuildNotifier/master/docs/configuration.png)

We will see the following message in Slack:

![Sample Notification](https://raw.github.com/Ecwid/tcSlackBuildNotifier/master/docs/custom_message.png)

Also there is ability to remove header and make compact one line message format (if "Message title" is not specified, and other build options are not selected).


## Installation
Head over to the [releases](https://github.com/PeteGoo/tcSlackBuildNotifier/releases) section and get the zip labelled `tcSlackNotifierPlugin.zip` from there (do not download the one on this page). Copy the zip file into your [team city plugins directory](https://confluence.jetbrains.com/display/TCD9/Installing+Additional+Plugins)

You will need to restart the TeamCity service before you can configure the plugin.

## Configuration

Once you have installed the plugin and restarted head on over to the Admin page and configure your Slack settings.

![Admin Page Configuration](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/AdminPageBig.png)

- *your default channel* is the channel with the # in front of it e.g. #general
- *your team name* is the team e.g. mycoolteam NOT mycoolteam.slack.com
- The user token is available when you got to https://api.slack.com/web while logged in to your Slack instance. Under "authentication" you should see a token displayed like below. Alternatively you can use the full URL from the `Incoming Webhooks` integration in the API token field. The webhooks integration approach is sometimes more preferable as the token is not tied to a user account.

![Sample Auth Token](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/SlackToken.png)

## Usage

From the Slack tab on the Project or Build Configuration page, add a new Slack Notification and you're away!

![Sample Build Configuration](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/build-slack-config.png)

- Remember to put the # in front of the channel name if applicable.

### Mentions

In order to receive mentions and direct messages from the notifier you must go to your profile page in TeamCity and tell it your slack username (no need to include the @). Once you have done this you can be mentioned on failed builds. You can also subscribe to notifications on your profile page.
