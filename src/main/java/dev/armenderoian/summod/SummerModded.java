package dev.armenderoian.summod;

import dev.armenderoian.summod.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummerModded implements ModInitializer {

    public static final String MOD_ID = "summermodded";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
            postServerStart();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> SERVER = null);

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
}
