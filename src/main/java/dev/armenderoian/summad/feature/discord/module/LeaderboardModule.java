package dev.armenderoian.summad.feature.discord.module;

import dev.armenderoian.summad.feature.leaderboard.LeaderboardFeature;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class LeaderboardModule extends DiscordModule {

    private final String leaderboardChannelId = ServerConfig.leaderboardChannelId;
    private TextChannel channel;
    private final Map<String, Message> leaderboardEmbeds = new HashMap<>();

    private final AtomicBoolean setup = new AtomicBoolean(false);

    @Override
    protected void start() throws Exception {
        channel = jda.getTextChannelById(leaderboardChannelId);
        if (channel == null) {
            throw new Exception("Could not find channel with id " + leaderboardChannelId);
        }

        channel.getHistory().retrievePast(Math.max(100, LeaderboardFeature.getLeaderboardCount())).queue(
                result -> {
                    result.forEach(message -> {
                        if (message.getEmbeds().isEmpty()) {
                            return;
                        }
                        var embed = message.getEmbeds().getFirst();
                        if (embed.getTitle() == null) {
                            return;
                        }
                        var title = embed.getTitle();
                        if (title.isEmpty()) {
                            return;
                        }
                        leaderboardEmbeds.put(title, message);
                    });

                    setup.set(true);
                },
                failure -> {
                    logger.error("Could not retrieve leaderboard embeds", failure);
                });
    }

    @Override
    public void stop() throws Exception {
        // pass
    }

    public void updateLeaderboards() {
        while (!setup.get()) Thread.yield();

        try {
            var leaderboards = LeaderboardFeature.getLeaderboards();
            for (var lb : leaderboards) {
                var title = lb.getName();
                if (leaderboardEmbeds.containsKey(title)) {
                    var message = leaderboardEmbeds.get(title);
                    if (message != null) {
                        message.editMessageEmbeds(lb.toDiscordMessage(10)).queue();
                    }
                } else {
                    channel.sendMessageEmbeds(lb.toDiscordMessage(10)).queue(message -> {
                        leaderboardEmbeds.put(title, message);
                    });
                }
            }
        } catch (Exception e) {
            logger.error("Error while updating leaderboards", e);
        }
    }
}
