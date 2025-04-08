package dev.armenderoian.summad.feature.discord;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.feature.AbstractFeature;
import dev.armenderoian.summad.feature.discord.event.BotEventListener;
import dev.armenderoian.summad.feature.discord.event.SlashCommandListener;
import dev.armenderoian.summad.feature.discord.module.DiscordModule;
import dev.armenderoian.summad.feature.discord.module.LeaderboardModule;
import dev.armenderoian.summad.feature.discord.module.StatusMessageModule;
import dev.armenderoian.summad.util.ServerConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.server.MinecraftServer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DiscordFeature extends AbstractFeature {

    private JDA jda;
    private static final HashMap<String, DiscordModule> modules = new HashMap<>();

    public static final StatusMessageModule STATUS_MESSAGE_MODULE = registerModule("status_message", new StatusMessageModule());
    public static final LeaderboardModule LEADERBOARD_MODULE = registerModule("leaderboard", new LeaderboardModule());

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
                    .addEventListeners(new BotEventListener(), new SlashCommandListener())
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
}
