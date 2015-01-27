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
- *your default channel* is the channel with the # in front of it e.g. #general
- *your team name* is the team e.g. mycoolteam NOT mycoolteam.slack.com
- The user token is available when you got to https://api.slack.com/web while logged in to your Slack instance. Under "authentication" you should see a token displayed like below.

![Sample Auth Token](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/SlackToken.png)

## Usage

From the Slack tab on the Project or Build Configuration page, add a new Slack Notification and you're away!

![Sample Build Configuration](https://raw.github.com/petegoo/tcSlackBuildNotifier/master/docs/build-slack-config.png)

- Remember to put the # in front of the channel name if applicable.

### Mentions

In order to receive mentions and direct messages from the notifier you must go to your profile page in TeamCity and tell it your slack username (no need to include the @). Once you have done this you can be mentioned on failed builds. You can also subscribe to notifications on your profile page.
