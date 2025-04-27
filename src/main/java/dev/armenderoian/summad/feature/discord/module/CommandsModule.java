package dev.armenderoian.summad.feature.discord.module;

import dev.armenderoian.summad.feature.discord.DiscordFeature;
import dev.armenderoian.summad.feature.leaderboard.LeaderboardFeature;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CommandsModule extends DiscordModule {

    private Role[] allowedRoles;
    private Role[] disallowedRoles;

    private final List<String> commands = List.of("join", "leaderboard", "players");

    @Override
    protected void start() throws Exception {
        guild.upsertCommand(Commands.slash("join", "Get the information to join the server.")
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED)).queue();
        guild.upsertCommand(Commands.slash("leaderboard", "View leaderboard information.")
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                .addOptions(new OptionData(OptionType.STRING, "name", "The leaderboard you wish to view.", true)
                                .addChoices(LeaderboardFeature.getLeaderboards().stream().map(lb -> new Command.Choice(lb.getName(), lb.getId())).toList()),
                        new OptionData(OptionType.USER, "user", "See a user's position on the leaderboard", false))).queue();
        guild.upsertCommand(Commands.slash("players", "View the current players on the server.")
                .setContexts(InteractionContextType.GUILD)
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED)).queue();

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
        if (!commands.contains(event.getName()))
            return;

        event.deferReply().queue();

        if (!canUserRunCommand(Objects.requireNonNull(event.getMember()))) {
            event.getHook().sendMessage("You do not have permission to run this command.").setEphemeral(true).queue();
            return;
        }

        switch (event.getName()) {
            case "join":
                MessageModule.JOIN_MESSAGE.tryReplyMessage(event.getHook());
                break;
            case "leaderboard":
                var leaderboardId = Objects.requireNonNull(event.getOption("name")).getAsString();
                var leaderboard = LeaderboardFeature.getLeaderboards().stream().filter(lb -> lb.getId().equals(leaderboardId)).findFirst().orElse(null);
                if (leaderboard == null) {
                    event.getHook().sendMessage("Leaderboard not found.").setEphemeral(true).queue();
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
                            event.getHook().sendMessageEmbeds(embed).queue(
                                    success -> {
                                    },
                                    failure -> logger.error("Failed to send leaderboard embed.", failure)
                            );
                        } else {
                            event.getHook().sendMessage("User not found on the leaderboard.").setEphemeral(true).queue();
                        }
                    } else {
                        event.getHook().sendMessage("User not found or is not verified, please try again.").setEphemeral(true).queue();
                    }
                } else {
                    event.getHook().sendMessageEmbeds(leaderboardEmbed).queue(
                            success -> {
                            },
                            failure -> logger.error("Failed to send leaderboard embed.", failure)
                    );
                }
                break;
            case "players":
                MessageModule.PLAYERS_MESSAGE.tryReplyMessage(event.getHook(), false);
                break;
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getChannelType() != ChannelType.TEXT) return;

        var message = event.getMessage().getContentRaw();
        if (message.equalsIgnoreCase("!messages_refresh")) {
            if (event.getMember() == null) return;
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR))
                return;

            event.getMessage().delete().queue();
            DiscordFeature.MESSAGE_MODULE.editMessages();
        } else if (message.equalsIgnoreCase("!verify_reset")) {
            if (event.getMember() == null) return;
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR))
                return;

            var args = message.split(" ");
            if (args.length < 2) {
                return;
            }

            var user = event.getGuild().getMemberById(args[1]);
            if (user == null) {
                return;
            }

            DiscordFeature.VERIFICATION_MODULE.resetUserVerification(user);
        }
    }
}
