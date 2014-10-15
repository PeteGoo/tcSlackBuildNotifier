tcSlackBuildNotifier
====================

## Overview

Posts Build Status to [Slack](http://www.slack.com)

![Sample Notification](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/build-status_pass.png)
![Sample Notification](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/build-status_fail.png)

_Tested on TeamCity 8.1.2 (build 29993)_

## Installation
Head over to the releases section and drop the zip into your [team city plugins directory](http://confluence.jetbrains.com/display/TCD8/Installing+Additional+Plugins)

## Configuration

Drop the following configuration into your main-config somewhere within the <server></server> tags

```xml

<slacknotifications 
  defaultChannel="<your default channel>" 
  teamName="<your team name>" 
  token="<Your user token available from the API page on slack>" 
  iconurl="https://raw.githubusercontent.com/PeteGoo/tcSlackBuildNotifier/master/docs/TeamCity32.png" 
  botname="Team City"
  showBuildAgent="true"
  showElapsedBuildTime="true"
  showCommits="true"
  showCommitters="false"
  maxCommitsToDisplay="5">
</slacknotifications>
```

The user token is available when you got to https://api.slack.com while logged in to your Slack instance. Under "authentication" you should see a token displayed like below.

![Sample Auth Token](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/SlackToken.png)

## Usage

From the Slack tab on the Project or Build Configuration page, add a new Slack Notification and you're away!

![Sample Build Configuration](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/build-slack-config.png)
