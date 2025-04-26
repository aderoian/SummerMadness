package dev.armenderoian.summad.feature.discord.message;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractMessage {
    private final String id;

    public AbstractMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    protected abstract MessageCreateData createMessage();

    public void trySendMessage(Guild guild, String channelId) {
        sendMessage(guild, channelId);
    }

    public void tryReplyMessage(SlashCommandInteractionEvent event) {
        try (var message = createMessage()) {
            event.reply(message).queue();
        }
    }

    public void tryReplyMessage(SlashCommandInteractionEvent event, boolean ephemeral) {
        try (var message = createMessage()) {
            if (ephemeral) {
                event.reply(message).setEphemeral(true).queue();
            } else {
                event.reply(message).queue();
            }
        }
    }

    public void tryReplyMessage(InteractionHook hook) {
        try (var message = createMessage()) {
            hook.sendMessage(message).queue();
        }
    }

    public void tryReplyMessage(InteractionHook hook, boolean ephemeral) {
        try (var message = createMessage()) {
            if (ephemeral) {
                hook.sendMessage(message).setEphemeral(true).queue();
            } else {
                hook.sendMessage(message).queue();
            }
        }
    }

    protected CompletableFuture<@Nullable Message> sendMessage(Guild guild, String channelId) {
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
