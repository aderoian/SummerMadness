package dev.armenderoian.summad.feature.discord.message;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractMessage {
    private String id;
    private String channelId;

    public AbstractMessage(String id, String channelId) {
        this.id = id;
        this.channelId = channelId;
    }

    public String getId() {
        return id;
    }

    public String getChannelId() {
        return channelId;
    }

    protected abstract MessageCreateData createMessage();

    public void trySendMessage(Guild guild) {
        sendMessage(guild);
    }

    protected CompletableFuture<@Nullable Message> sendMessage(Guild guild) {
        try (var message = createMessage()) {
            var channel = guild.getTextChannelById(channelId);
            if (channel == null)
                return CompletableFuture.completedFuture(null);

            try {
                return channel.sendMessage(message).submit();
            } catch (Exception e) {
                return CompletableFuture.completedFuture(null);
            }
        }
    }
}
