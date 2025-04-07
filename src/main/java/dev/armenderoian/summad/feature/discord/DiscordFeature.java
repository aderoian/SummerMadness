package dev.armenderoian.summad.feature.discord;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.discord.event.BotEventListener;
import dev.armenderoian.summad.feature.discord.event.SlashCommandListener;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Collections;

public class DiscordFeature {

    public static void registerDiscordFeature() {
        var server = SummerMadness.SERVER;
        if (!server.isDedicated()) {
            SummerMadness.LOGGER.info("Not a dedicated server, skipping Discord feature registration.");
        }

        var token = ServerConfig.discordToken;
        var jda = JDABuilder.createLight(token, Collections.emptySet())
                .addEventListeners(new BotEventListener(), new SlashCommandListener())
                .build();

        var commands = jda.updateCommands();
        commands.addCommands(
                Commands.slash("ip", "Get the server IP address")
                        .setContexts(InteractionContextType.GUILD)
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                Commands.slash("join", "Get the server IP address")
                        .setContexts(InteractionContextType.GUILD)
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
        );

        commands.queue();
    }
}
