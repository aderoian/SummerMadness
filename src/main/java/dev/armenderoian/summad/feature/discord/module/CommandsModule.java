package dev.armenderoian.summad.feature.discord.module;

import dev.armenderoian.summad.feature.discord.DiscordFeature;
import dev.armenderoian.summad.feature.leaderboard.LeaderboardFeature;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class CommandsModule extends DiscordModule {

    private Role[] allowedRoles;
    private Role[] disallowedRoles;

    @Override
    protected void start() throws Exception {
        guild.updateCommands().addCommands(
                Commands.slash("join", "Get the information to join the server.")
                        .setContexts(InteractionContextType.GUILD)
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                Commands.slash("leaderboard", "View leaderboard information.")
                        .setContexts(InteractionContextType.GUILD)
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                        .addOptions(new OptionData(OptionType.STRING, "name", "The leaderboard you wish to view.", true)
                                        .addChoices(LeaderboardFeature.getLeaderboards().stream().map(lb -> new Command.Choice(lb.getName(), lb.getId())).toList()),
                                new OptionData(OptionType.USER, "user", "See a user's position on the leaderboard", false))
        ).queue(
                success -> {
                },
                failure -> logger.error("Failed to register slash commands.", failure)
        );

        allowedRoles = Arrays.stream(ServerConfig.allowedCommandRoles).map(id -> guild.getRoleById(id)).toArray(Role[]::new);
        disallowedRoles = Arrays.stream(ServerConfig.disallowedCommandRoles).map(id -> guild.getRoleById(id)).toArray(Role[]::new);

        jda.addEventListener(this);
    }

    public boolean canUserRunCommand(Member member) {
        if (member.getRoles().stream().anyMatch(role -> Arrays.asList(allowedRoles).contains(role))) {
            return false;
        } else if (member.getRoles().stream().anyMatch(role -> Arrays.asList(disallowedRoles).contains(role))) {
            return true;
        } else {
            return member.hasPermission(Permission.ADMINISTRATOR);
        }
    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if (!canUserRunCommand(Objects.requireNonNull(event.getMember()))) {
            event.getHook().sendMessage("You do not have permission to run this command.").setEphemeral(true).queue();
            return;
        }

        switch (event.getName()) {
            case "join":
                event.reply("Server IP: " + ServerConfig.serverIp)
                        .setEphemeral(true)
                        .queue();
                break;
            case "leaderboard":
                var leaderboardId = Objects.requireNonNull(event.getOption("name")).getAsString();
                var leaderboard = LeaderboardFeature.getLeaderboards().stream().filter(lb -> lb.getId().equals(leaderboardId)).findFirst().orElse(null);
                if (leaderboard == null) {
                    event.reply("Leaderboard not found.").setEphemeral(true).queue();
                    return;
                }

                var leaderboardEmbed = leaderboard.toDiscordMessage(10);

                var userOption = event.getOption("user");
                if (userOption != null) {
                    var user = userOption.getAsUser();
                    var userLink = DiscordFeature.VERIFICATION_MODULE.getVerifiedUserDatabase().findByDiscordId(user.getId());
                    if (userLink != null) {
                        var embed = leaderboard.toDiscordMessage(10, UUID.fromString(userLink.uuid));
                        if (embed != null) {
                            event.replyEmbeds(leaderboardEmbed).setEphemeral(true).queue(
                                    success -> {
                                    },
                                    failure -> logger.error("Failed to send leaderboard embed.", failure)
                            );
                        } else {
                            event.reply("User not found on the leaderboard.").setEphemeral(true).queue();
                        }
                    } else {
                        event.reply("User not found or is not verified, please try again.").setEphemeral(true).queue();
                    }
                } else {
                    event.replyEmbeds(leaderboardEmbed).setEphemeral(true).queue(
                            success -> {
                            },
                            failure -> logger.error("Failed to send leaderboard embed.", failure)
                    );
                }
        }
    }
}
