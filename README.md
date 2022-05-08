# Slack Share Plugin for IntelliJ IDEA

Currently in development.

For now user token must be manually issued on Slack side. 
Proper auth is planned to be added later.
Releases to IDEA and Slack extension shops are also planned.
Confirmed compatibility with IDEA 2021.3.

#How to install?

1. Create new Slack app
   https://api.slack.com/apps

2. Use "From app manifest" option and copypaste the next manifest:

```
display_information:
  name: Share from JetBrains
oauth_config:
  scopes:
    user:
      - channels:read
      - chat:write
      - files:write
      - groups:read
      - im:read
      - mpim:read
      - users:read
settings:
  org_deploy_enabled: false
  socket_mode_enabled: false
  token_rotation_enabled: false
```

3. Install app to Slack workspace

4. Install .zip extension file through Settings->Plugins->Gear Icon->Install Plugin From Disk...

5. Open Slack App's settings on "OAuth & Permissions" tab and copy User OAuth Token

6. Try to share anything with extension or open extension settings (Settings->Tools->Slack Share) and paste your token
