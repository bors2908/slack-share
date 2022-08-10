# Slack Share Plugin for IntelliJ IDEA

Currently in development.

If plugin is loaded from github, the token must be manually issued on Slack side[1].
Releases to IDEA and Slack extension shops are planned. (Under review)
Confirmed compatibility with IDEA 2020.3-2022.2.

#How to install from .zip distribution?
1. Create new Slack app from manifest: [Link to app creation with pre-configured app manifest](https://api.slack.com/apps?new_app=1&manifest_json={"display_information":{"name":"Share%20from%20JetBrains"},"oauth_config":{"scopes":{"user":["channels:read","chat:write","files:write","groups:read","im:read","mpim:read","users:read"]}},"settings":{"org_deploy_enabled":false,"socket_mode_enabled":false,"token_rotation_enabled":false}})

2. Install app to Slack workspace

3. Open Slack App's settings on "OAuth & Permissions" tab and copy User OAuth Token

4. Install .zip extension file through Settings->Plugins->Gear Icon->Install Plugin From Disk...

5. Share anything with extension or open extension settings (Settings->Tools->Slack Share) and paste your token

[1] - Otherwise add your own slack app's client_id and secret to "secrets/secret.properties" file and build module slack-share-store for yourself.
