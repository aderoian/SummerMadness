package dev.armenderoian.summad.feature.discord.event;

import dev.armenderoian.summad.feature.discord.DiscordFeature;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ip":
            case "join":
                event.reply("Server IP: " + ServerConfig.serverIp)
                        .setEphemeral(true)
                        .queue();
                break;
            case "status":
                DiscordFeature.sendStatusMessage();
                event.reply("Send a status update.").setEphemeral(true).queue();
                break;
        }
    }
}
