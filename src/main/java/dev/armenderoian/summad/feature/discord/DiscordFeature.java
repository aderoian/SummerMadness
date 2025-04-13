package dev.armenderoian.summad.feature.discord;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.feature.discord.module.*;
import dev.armenderoian.summad.registry.ModFeatureContent;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DiscordFeature extends AbstractFeature {

    private JDA jda;
    private Guild guild;
    private static final HashMap<String, DiscordModule> modules = new HashMap<>();

    public static final StatusMessageModule STATUS_MESSAGE_MODULE = registerModule("status_message", new StatusMessageModule());
    public static final LeaderboardModule LEADERBOARD_MODULE = registerModule("leaderboard", new LeaderboardModule());
    public static final VerificationModule VERIFICATION_MODULE = registerModule("verification", new VerificationModule());
    public static final CommandsModule COMMANDS_MODULE = registerModule("commands", new CommandsModule());

    public DiscordFeature(String name) {
        super(name);
    }

    @Override
    public void onStart(MinecraftServer server) throws Exception {
        if (!server.isDedicated()) {
            logger.info("Not a dedicated server, skipping Discord feature registration.");
        }

        var token = ServerConfig.discordToken;
        SummerMadness.SCHEDULER.schedule(() -> {
            jda = JDABuilder.createLight(token, EnumSet.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES))
                    .addEventListeners(new BotEventListener())
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build();
        }, 0, TimeUnit.SECONDS);
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

    public HashMap<String, DiscordModule> getModules() {
        return modules;
    }

    public static <T extends DiscordModule> T registerModule(String name, T module) {
        if (modules.containsKey(name))
            throw new IllegalArgumentException("Discord module already registered: " + name);
        modules.put(name, module);
        return module;
    }

    private class BotEventListener extends ListenerAdapter {

        @Override
        public void onReady(@NotNull ReadyEvent event) {
            SummerMadness.LOGGER.info("Bot '{}' has logged in. Guilds: {}", event.getJDA().getSelfUser().getName(), event.getJDA().getGuilds().size());

            var modules = ModFeatureContent.DISCORD_FEATURE.getModules();
            guild = jda.getGuildById(ServerConfig.discordGuildId);

            if (guild == null) {
                logger.error("Guild not found. Please check your configuration.");
                return;
            }

            // sync commands
            // removes all commands and allows the modules to add their own
            jda.updateCommands().addCommands(List.of()).queue(); // should never have global commands
            guild.updateCommands().addCommands(List.of()).queue();

            logger.info("Starting modules...");
            modules.forEach((name, module) -> {
                try {
                    module.start(jda, guild, logger);
                    logger.info("Module {} started.", name);
                } catch (Exception e) {
                    logger.error("Failed to start module {}: {}", name, e.getMessage(), e);
                }
            });
        }
    }
}
