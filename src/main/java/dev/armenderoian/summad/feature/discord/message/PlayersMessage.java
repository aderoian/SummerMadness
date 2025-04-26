package dev.armenderoian.summad.feature.discord.message;

import dev.armenderoian.summad.SummerMadness;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.time.Instant;

public class PlayersMessage extends AbstractMessage{
    public PlayersMessage() {
        super("players");
    }

    @Override
    protected MessageCreateData createMessage() {
        var players = SummerMadness.SERVER.getPlayerManager().getPlayerList();
        return new MessageCreateBuilder()
                .addEmbeds(
                        new EmbedBuilder()
                                .setTitle("Online Players")
                                .setDescription("Current players online: " + players.size() + "/" + SummerMadness.SERVER.getPlayerManager().getMaxPlayerCount() + "\n\n" +
                                        String.join("\n", players.stream().map(player -> "- " + player.getName().getString()).toArray(String[]::new)))
                                .setColor(0x00FF00)
                                .setTimestamp(Instant.now())
                                .build())
                .build();
    }
}
