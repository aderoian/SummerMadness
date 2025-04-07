package dev.armenderoian.summad.util;

import eu.midnightdust.lib.config.MidnightConfig;

public class ServerConfig extends MidnightConfig {

    @Entry
    public static String discordToken = "bot_token";
    @Entry
    public static String discordGuildId = "guild_id";
    @Entry
    public static String serverIp = "server_ip";
}
