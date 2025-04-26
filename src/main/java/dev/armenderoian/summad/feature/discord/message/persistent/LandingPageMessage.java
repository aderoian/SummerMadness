package dev.armenderoian.summad.feature.discord.message.persistent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;

public class LandingPageMessage extends PersistentMessage {
    public LandingPageMessage(String channelId) {
        super("landing_page", channelId);
    }

    @Override
    public MessageCreateData createMessage() {
        return new MessageCreateBuilder()
                .setEmbeds(new EmbedBuilder()
                        .setTitle("Welcome to the Discord!")
                        .setDescription("To get started, please read the rules a verify yourself by using the `/verify` command.")
                        .setColor(Color.YELLOW)
                        .build())
                .build();
    }
}