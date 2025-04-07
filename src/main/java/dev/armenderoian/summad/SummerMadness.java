package dev.armenderoian.summad;

import dev.armenderoian.summad.registry.*;
import dev.armenderoian.summad.util.ClientConfig;
import dev.armenderoian.summad.util.ServerConfig;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummerMadness implements ModInitializer {

    public static final String MOD_ID = "summermadness";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
            postServerStart();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> SERVER = null);

        registerConfig();

        ItemGroupContent.registerItemGroups();

        ItemContent.registerItems();

        SoundContent.registerSounds();

        LootTableContent.registerLootTables();
    }

    public void postServerStart() {
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
}
