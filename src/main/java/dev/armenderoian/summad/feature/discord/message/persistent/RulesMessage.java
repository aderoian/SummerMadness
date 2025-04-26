package dev.armenderoian.summad.feature.discord.message.persistent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.time.Instant;

public class RulesMessage extends PersistentMessage {
    public RulesMessage(String channelId) {
        super("server_rules", channelId);
    }

    @Override
    public MessageCreateData createMessage() {
        return new MessageCreateBuilder()
                .addEmbeds(new EmbedBuilder()
                        .setTitle("Server Rules")
                        .setDescription("Please read the server rules.")
                        .addField("1. Be Cool", "Jokes, swearing, and light roasting are fine — don't get personal or toxic.", false)
                        .addField("2. Pranks Good, Griefing Bad", "Fun traps and jokes are okay. Destroying bases or builds isn't.", false)
                        .addField("3. No Cheating", "No hacked clients, cheats, or exploits.", false)
                        .addField("4. PvP = Mutual", "Fight only if both sides agree. No random ganks.", false)
                        .addField("5. Don't Start Drama", "If you're causing problems instead of laughs, you're out.", false)
                        .setTimestamp(Instant.now())
                        .setColor(Color.CYAN)
                        .build())
                .build();
    }
}