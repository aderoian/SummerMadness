package dev.armenderoian.summad.feature.discord.event;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.discord.DiscordFeature;
import dev.armenderoian.summad.registry.ModFeatureContent;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BotEventListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        SummerMadness.LOGGER.info("Bot '{}' has logged in. Guilds: {}", event.getJDA().getSelfUser().getName(), event.getJDA().getGuilds().size());

        var jda = event.getJDA();
        var logger = ModFeatureContent.DISCORD_FEATURE.getLogger();
        var modules = ModFeatureContent.DISCORD_FEATURE.getModules();

        var guildId = ServerConfig.discordGuildId;
        var commands = Objects.requireNonNull(jda.getGuildById(guildId)).updateCommands();
        commands.addCommands(
                Commands.slash("ip", "Get the server IP address")
                        .setContexts(InteractionContextType.GUILD)
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                Commands.slash("join", "Get the server IP address")
                        .setContexts(InteractionContextType.GUILD)
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                Commands.slash("status", "Get the server status")
                        .setContexts(InteractionContextType.GUILD)
                        .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
        ).queue();

        logger.info("Starting modules...");
        modules.forEach((name, module) -> {
            try {
                module.start(jda, logger);
                logger.info("Module {} started.", name);
            } catch (Exception e) {
                logger.error("Failed to start module {}: {}", name, e.getMessage(), e);
            }
        });
    }
}
