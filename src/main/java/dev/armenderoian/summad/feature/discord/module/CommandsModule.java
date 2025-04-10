package dev.armenderoian.summad.feature.discord.module;

import dev.armenderoian.summad.feature.discord.DiscordFeature;
import dev.armenderoian.summad.feature.leaderboard.LeaderboardFeature;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

public class CommandsModule extends DiscordModule {

    @Override
    protected void start() throws Exception {
        guild.updateCommands().addCommands(
                Commands.slash("join", "Get the information to join the server.")
                        .setContexts(InteractionContextType.GUILD)
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                Commands.slash("status", "Get the server status.")
                        .setContexts(InteractionContextType.GUILD)
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                Commands.slash("leaderboard", "View leaderboard information.")
                        .setContexts(InteractionContextType.GUILD)
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                        .addOptions(new OptionData(OptionType.STRING, "name", "The leaderboard you wish to view.", true)
                                .addChoices(LeaderboardFeature.getLeaderboards().stream().map(lb -> new Command.Choice(lb.getName(), lb.getId())).toList()))
        ).queue(
                success -> {},
                failure -> {logger.error("Failed to register slash commands.", failure);}
        );

        jda.addEventListener(this);
    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "join":
                event.reply("Server IP: " + ServerConfig.serverIp)
                        .setEphemeral(true)
                        .queue();
                break;
            case "status":
                DiscordFeature.STATUS_MESSAGE_MODULE.sendStatusMessage();
                event.reply("Send a status update.").setEphemeral(true).queue();
                break;
            case "leaderboard":
                var leaderboardId = Objects.requireNonNull(event.getOption("name")).getAsString();
                var leaderboard = LeaderboardFeature.getLeaderboards().stream().filter(lb -> lb.getId().equals(leaderboardId)).findFirst().orElse(null);
                if (leaderboard == null) {
                    event.reply("Leaderboard not found.").setEphemeral(true).queue();
                    return;
                }

                var leaderboardEmbed = leaderboard.toDiscordMessage(10);
                event.replyEmbeds(leaderboardEmbed).setEphemeral(true).queue(
                        success -> {},
                        failure -> {logger.error("Failed to send leaderboard embed.", failure);}
                );
        }
    }
}
