package dev.armenderoian.summad.feature.discord.message;

import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class JoinMessage extends AbstractMessage {
    public JoinMessage() {
        super("join_message");
    }

    @Override
    protected MessageCreateData createMessage() {
        return MessageCreateData.fromContent("Server IP: " + ServerConfig.serverIp);
    }
}