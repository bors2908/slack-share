# Slack Share Plugin for IntelliJ IDEA

Currently in development.

[Download From JetBrains Marketplace.](https://plugins.jetbrains.com/plugin/19621-slack-share)

If plugin is loaded from GitHub, the token must be manually issued on Slack side[1].

Confirmed compatibility with IDEA 2020.3-2023.3.

#How to install from .zip distribution?
1. Install .zip extension file through Settings->Plugins->Gear Icon->Install Plugin From Disk...

2. Share anything with extension or open extension settings (Settings->Tools->Slack Share)

3. Go through Slack App creation process using "Create App" button in "Add Manually" section if you haven't already.

4. Install it to your workspace. 

5. Open Slack App's settings on "OAuth & Permissions" tab, copy User OAuth Token and paste it in the plugin settings.

[1] - Otherwise add your own Slack app's client_id and secret to "secrets/secret.properties" file and build module slack-share-store for yourself.
