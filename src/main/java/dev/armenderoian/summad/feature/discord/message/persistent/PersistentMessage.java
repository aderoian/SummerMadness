package dev.armenderoian.summad.feature.discord.message.persistent;

import dev.armenderoian.summad.feature.discord.message.AbstractMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class PersistentMessage extends AbstractMessage {
    private @Nullable String messageId = null;

    public PersistentMessage(String id, String channelId) {
        super(id, channelId);
    }

    public @Nullable String getMessageId() {
        return messageId;
    }

    public abstract MessageCreateData createMessage();

    @Override
    public void trySendMessage(Guild guild) {
        if (messageId != null) return;

        sendMessage(guild).thenApply(message -> {
            if (message != null) {
                messageId = message.getId();
                return message;
            } else {
                return null;
            }
        });
    }

    @Override
    protected CompletableFuture<@Nullable Message> sendMessage(Guild guild) {
        if (messageId == null) {
            return super.sendMessage(guild);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
}