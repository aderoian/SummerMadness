package dev.armenderoian.summad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.armenderoian.summad.network.ClientboundPacket;
import dev.armenderoian.summad.registry.*;
import dev.armenderoian.summad.util.ClientConfig;
import dev.armenderoian.summad.util.ServerConfig;
import eu.midnightdust.lib.config.MidnightConfig;
import io.wispforest.owo.network.OwoNetChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SummerMadness implements ModInitializer {

    public static final String MOD_ID = "summermadness";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer SERVER;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(4);
    public static Path DATA_PATH;

    public static final OwoNetChannel NETWORK = OwoNetChannel.create(Identifier.of(MOD_ID, "network"));

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
            DATA_PATH = server.getRunDirectory().resolve("data").resolve(MOD_ID);
            if (!DATA_PATH.toFile().exists()) {
                DATA_PATH.toFile().mkdirs();
            }

            try {
                postServerStart();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            onDisable();
        });

        registerConfig();

        ItemGroupContent.registerItemGroups();

        ItemContent.registerItems();

        NETWORK.registerClientboundDeferred(ClientboundPacket.ClientboundCombatToast.class);
    }

    public void postServerStart() throws Exception {
        if (SERVER == null) {
            throw new IllegalStateException("Server is not initialized");
        }

        ModFeatureContent.registerFeatures();
    }

    private void registerConfig() {
        MidnightConfig.init(SummerMadness.MOD_ID + "-server", ServerConfig.class);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            MidnightConfig.init(SummerMadness.MOD_ID + "-client", ClientConfig.class);
    }

    public static void onDisable() {
        ModFeatureContent.onDisable();
        SCHEDULER.shutdownNow();
    }
}
