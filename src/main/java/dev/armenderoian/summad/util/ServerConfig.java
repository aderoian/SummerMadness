package dev.armenderoian.summad.util;

import eu.midnightdust.lib.config.MidnightConfig;

public class ServerConfig extends MidnightConfig {

    @Entry
    public static String discordToken = "bot_token";
    @Entry
    public static String discordGuildId = "guild_id";
    @Entry
    public static String serverIp = "server_ip";
    @Entry
    public static String statusUpdateChannelId = "status_update_channel_id";
    @Entry
    public static int statusUpdateInterval = 30; // in seconds

    @Entry
    public static int combatLoggerCooldown = 5; // in seconds

    @Entry
    public static String leaderboardChannelId = "leaderboard_channel_id";
    @Entry
    public static int leaderboardUpdateInterval = 60 * 60; // in seconds

    @Entry
    public static String landingPageChannelId = "landing_page_channel_id";
    @Entry
    public static String memberManagementChannelId = "member_management_channel_id";
    @Entry
    public static String[] verificationRolesToAdd = new String[0];
    @Entry
    public static String[] verificationRolesToRemove = new String[0];
}
