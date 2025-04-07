package dev.armenderoian.summad.feature.discord;

import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.feature.discord.event.BotEventListener;
import dev.armenderoian.summad.feature.discord.event.SlashCommandListener;
import dev.armenderoian.summad.feature.discord.module.DiscordModule;
import dev.armenderoian.summad.feature.discord.module.StatusMessageModule;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.server.MinecraftServer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Objects;

public class DiscordFeature extends AbstractFeature {

    private static JDA jda;
    private static final HashMap<String, DiscordModule> modules = new HashMap<>();

    public static StatusMessageModule STATUS_MESSAGE_MODULE = registerModule("status_message", new StatusMessageModule());

    public DiscordFeature(String name) {
        super(name);
    }

    @Override
    public void onStart(MinecraftServer server) throws Exception {
        if (!server.isDedicated()) {
            logger.info("Not a dedicated server, skipping Discord feature registration.");
        }

        var token = ServerConfig.discordToken;
        jda = JDABuilder.createLight(token, EnumSet.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES))
                .addEventListeners(new BotEventListener(), new SlashCommandListener())
                .build();

        jda.awaitReady();

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
        );

        commands.queue();

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

    @Override
    public void onStop() throws Exception {
        logger.info("Disabling Discord feature...");
        logger.info("Stopping modules...");
        modules.forEach((name, module) -> {
            try {
                module.stop();
                logger.info("Module {} stopped.", name);
            } catch (Exception e) {
                logger.error("Failed to stop module {}: {}", name, e.getMessage(), e);
            }
        });
        jda.shutdown();
    }

    public static <T extends DiscordModule> T registerModule(String name, T module) {
        if (modules.containsKey(name))
            throw new IllegalArgumentException("Discord module already registered: " + name);
        modules.put(name, module);
        return module;
    }
}
