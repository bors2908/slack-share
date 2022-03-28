# Slack Share Plugin for IntelliJ IDEA

Currently in development.

For now user token must be manually issued on Slack side. Proper auth is planned to add late.
Release to IDEA and Slack extension shops is also planned.

#How to install?

1. Create new Slack app
   https://api.slack.com/apps

2. Add User scopes in Oauth & Permission tab
- channels:read
- chat:write
- files:write
- groups:read
- im:read
- mpim:read
- users:read

3. Install app to workspace

4. (Potentially unsafe) Create file
   WINDOWS %LOCALAPPDATA%\slack-share\slack-share
   MAC_OS ~/Library/Application Support/slack-share/slack-share
   LINUX ~/.local/share/slack-share/slack-share

5. (Potentially unsafe) Copy Oauth User Token and paste it into created file

6. Install .zip extension file through Settings->Plugins->Gear Icon->Install Plugin From Disk...
