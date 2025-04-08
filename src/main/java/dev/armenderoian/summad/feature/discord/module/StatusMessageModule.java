package dev.armenderoian.summad.feature.discord.module;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.minecraft.util.TimeHelper;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StatusMessageModule extends DiscordModule {

    private final String statusChannelId = ServerConfig.statusUpdateChannelId;
    private final int statusUpdateInterval = ServerConfig.statusUpdateInterval;
    private final String serverIp = ServerConfig.serverIp;

    private ScheduledFuture<?> updateTask;
    private TextChannel statusChannel;
    private String statusMessageId = "";
    private final Instant startTime = Instant.now();

    @Override
    public void start() throws Exception {
        updateTask = SummerMadness.SCHEDULER.scheduleWithFixedDelay(this::sendStatusMessage, 0, statusUpdateInterval, TimeUnit.SECONDS);
    }

    @Override
    public void stop() throws Exception {
        sendStatusMessage(false);
        if (updateTask != null) {
            updateTask.cancel(true);
        }
    }

    public void sendStatusMessage() {
        sendStatusMessage(true);
    }

    public void sendStatusMessage(boolean running) {
        try {
            var channelId = statusChannelId;
            if (statusChannel == null) {
                statusChannel = jda.getTextChannelById(channelId);
                if (statusChannel == null) {
                    logger.error("Failed to get status channel: {}", channelId);
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
                            logger.error("Failed to send status message: {}", e.getMessage(), e);
                        }
                    });
                }
            } else {
                statusChannel.editMessageById(statusMessageId, MessageEditData.fromEmbeds(getStatusMessage(running))).queue();
            }
        } catch (Exception e) {
            logger.error("Failed to send status message: {}", e.getMessage(), e);
        }
    }

    private MessageEmbed getStatusMessage(boolean running) {
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
                .addField("IP", serverIp, false)
                .addField("Version", running ? SummerMadness.SERVER.getVersion() : "N/A", false)
                .addField("Uptime", uptime, false)
                .addField("TPS", running ? String.format("%.2f", tps) : "N/A", false)
                .addField("Player Count", running ? SummerMadness.SERVER.getCurrentPlayerCount() + "/" + SummerMadness.SERVER.getMaxPlayerCount() : "0", false)
                .addField("Players", running ? String.join(", ", SummerMadness.SERVER.getCurrentPlayerCount() > 0 ? SummerMadness.SERVER.getPlayerNames() : new String[]{"None"}) : "None", false)
                .setFooter(running ? "Last checked" : "Last online")
                .setTimestamp(Instant.now())
                .build();

    }
}
