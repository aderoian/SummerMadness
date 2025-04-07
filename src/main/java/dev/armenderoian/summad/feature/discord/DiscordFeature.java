package dev.armenderoian.summad.feature.discord;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.discord.event.BotEventListener;
import dev.armenderoian.summad.feature.discord.event.SlashCommandListener;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.minecraft.util.TimeHelper;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordFeature {

    private static JDA jda;

    private static ScheduledExecutorService scheduler;

    private static TextChannel statusChannel;
    private static String statusMessageId = "";

    private static final Instant startTime = Instant.now();

    public static void registerDiscordFeature() {
        var server = SummerMadness.SERVER;
        if (!server.isDedicated()) {
            SummerMadness.LOGGER.info("Not a dedicated server, skipping Discord feature registration.");
        }

        try {
            var token = ServerConfig.discordToken;
            jda = JDABuilder.createLight(token, EnumSet.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES))
                    .addEventListeners(new BotEventListener(), new SlashCommandListener())
                    .build();

            scheduler = Executors.newSingleThreadScheduledExecutor();

            jda.awaitReady();

            var guildId = ServerConfig.discordGuildId;
            var commands = Objects.requireNonNull(jda.getGuildById(guildId)).updateCommands();
            commands.addCommands(
                    Commands.slash("ip", "Get the server IP address")
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                    Commands.slash("join", "Get the server IP address")
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                    Commands.slash("status", "Get the server status")
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
            );

            commands.queue();

            scheduler.scheduleAtFixedRate(DiscordFeature::sendStatusMessage, 0, ServerConfig.statusUpdateInterval, TimeUnit.SECONDS);
        } catch (Exception e) {
            SummerMadness.LOGGER.error("Failed to register Discord feature: {}", e.getMessage(), e);
        }
    }

    public static void onDisable() {
        SummerMadness.LOGGER.info("Discord feature disabled.");
        if (scheduler != null) scheduler.shutdownNow();
        sendStatusMessage(false);
        jda.shutdown();
    }

    public static void sendStatusMessage() {
        sendStatusMessage(true);
    }

    public static void sendStatusMessage(boolean running) {
        try {
            var channelId = ServerConfig.statusUpdateChannelId;
            if (statusChannel == null) {
                statusChannel = jda.getTextChannelById(channelId);
                if (statusChannel == null) {
                    SummerMadness.LOGGER.error("Failed to get status channel: {}", channelId);
                    return;
                }
            }

            if (statusMessageId.isEmpty()) {
                var messages = statusChannel.getHistory().retrievePast(1).complete();
                if (!messages.isEmpty()) {
                    statusMessageId = messages.getFirst().getId();
                    statusChannel.editMessageById(statusMessageId, MessageEditData.fromEmbeds(getStatusMessage(running))).queue();
                } else {
                    statusChannel.sendMessage(MessageCreateData.fromEmbeds(getStatusMessage(running))).queue(null, e -> {
                        if (e instanceof ErrorResponseException ere) {
                            statusMessageId = "";
                            sendStatusMessage();
                        } else {
                            SummerMadness.LOGGER.error("Failed to send status message: {}", e.getMessage(), e);
                        }
                    });
                }
            } else {
                statusChannel.editMessageById(statusMessageId, MessageEditData.fromEmbeds(getStatusMessage(running))).queue();
            }
        } catch (Exception e) {
            SummerMadness.LOGGER.error("Failed to send status message: {}", e.getMessage(), e);
        }
    }

    private static MessageEmbed getStatusMessage(boolean running) {
        String uptime;
        float tps;
        String status;
        Color color = Color.RED;
        if (running) {
            var duration = Duration.between(startTime, Instant.now());
            var days = duration.toDays();
            var hours = duration.toHours() % 24;
            var minutes = duration.toMinutes() % 60;
            uptime = String.format("%d day%s, %d hour%s, %d minute%s", days, days > 1 ? "s" : "", hours, hours > 1 ? "s" : "", minutes, minutes > 1 ? "s" : "");

            tps = Math.min(20f, 1 / Math.min(0.05f, (float) SummerMadness.SERVER.getAverageNanosPerTick() / TimeHelper.SECOND_IN_NANOS));
            if (tps >= 19.25f) {
                status = ":green_circle: Online";
                color = Color.GREEN;
            } else if (tps >= 15f) {
                status = ":yellow_circle: Online • Lagging";
                color = Color.YELLOW;
            } else if (tps >= 10f) {
                status = ":orange_circle: Online • Slow";
                color = Color.ORANGE;
            } else if (tps >= 5f) status = ":red_circle: Online • Extremely Slow";
            else if (tps >= 0f) status = ":red_circle: Online • Unplayable";
            else status = ":red_circle: Offline";
        } else {
            uptime = "N/A";
            status = ":red_circle: Offline";
            tps = 0f;
        }

        return new EmbedBuilder()
                .setTitle(":earth_americas: Minecraft Server Status")
                .setColor(color)
                .addField("Status", status, false)
                .addField("IP", ServerConfig.serverIp, false)
                .addField("Version", running ? SummerMadness.SERVER.getVersion() : "N/A", false)
                .addField("Uptime", uptime, false)
                .addField("TPS", running ? String.format("%.2f", tps) : "N/A", false)
                .addField("Player Count", running ? SummerMadness.SERVER.getCurrentPlayerCount() + "/" + SummerMadness.SERVER.getMaxPlayerCount() : "0", false)
                .addField("Players", running ? String.join(", ", SummerMadness.SERVER.getCurrentPlayerCount() > 0 ? SummerMadness.SERVER.getPlayerNames() : new String[] {"None"}) : "None", false)
                .setFooter(running ? "Last checked" : "Last online")
                .setTimestamp(Instant.now())
                .build();

    }
}
