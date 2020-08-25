tcSlackBuildNotifier
====================

## Deprecation notice

**This project is no longer actively maintained** - JetBrains has added Slack functionality to [TeamCity 2020.1](https://blog.jetbrains.com/teamcity/2020/05/teamcity-2020-1-conditional-build-steps-support-for-kubernetes-slack-notifier-integration-with-azure-devops-and-jira-software-cloud-and-more/)

[![Join the chat at https://gitter.im/PeteGoo/tcSlackBuildNotifier](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/PeteGoo/tcSlackBuildNotifier?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Overview

Posts Build Status to [Slack](http://www.slack.com)

![Sample Notification](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/build-status_pass.png)
![Sample Notification](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/build-status_fail.png)

_Tested on TeamCity 8.1.2 (build 29993), 9.1 (build 36973)_

## Installation
Head over to the [releases](https://github.com/PeteGoo/tcSlackBuildNotifier/releases) section and get the zip labelled `tcSlackNotifierPlugin.zip` from there (do not download the one on this page). Copy the zip file into your [TeamCity plugins directory](https://confluence.jetbrains.com/display/TCD9/Installing+Additional+Plugins).

You will need to restart the TeamCity service before you can configure the plugin.

## Configuration

Once you have installed the plugin and restarted head on over to the Admin page and configure your Slack settings.

![Admin Page Configuration](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/AdminPageBig.png)

- *your default channel* is the channel with the # in front of it e.g. #general.
- *your team name* is the team e.g. mycoolteam NOT mycoolteam.slack.com.
- The user token is available when you got to https://api.slack.com/web while logged in to your Slack instance. Under "authentication" you should see a token displayed like below. Alternatively you can use the full URL from the `Incoming Webhooks` integration in the API token field. The webhooks integration approach is sometimes more preferable as the token is not tied to a user account.

![Sample Auth Token](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/SlackToken.png)

## Usage

From the Slack tab on the project or build page (_not_ the Build Configuration Settings page), add a new Slack Notification and you're away!

![Sample Build Configuration](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/build-slack-config.png)

- Remember to put the # in front of the channel name if applicable.

### Mentions

In order to receive mentions and direct messages from the notifier you must go to your profile page in TeamCity and tell it your Slack username (no need to include the @). Once you have done this you can be mentioned on failed builds. You can also subscribe to notifications on your profile page.

## Contribution

In order to contribute to the project you first need to checkout the project sources. This project uses the TeamCity Plugin SDK for development.

In order to test the plugin simply run the following command with java and mvn installed:

    mvn package
    mvn -pl :tcslackbuildnotifier-plugin tc-sdk:start

By default it will install TeamCity in the version listed in the property in the root `pom.xml`. However you can overwrite this setting by using the `-DteamcityVersion=10.0` switch.

Other available commands can be found [here](https://github.com/JetBrains/teamcity-sdk-maven-plugin).
