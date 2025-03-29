package dev.armenderoian.summod;

import dev.armenderoian.summod.registry.ItemContent;
import dev.armenderoian.summod.registry.ItemGroupContent;
import dev.armenderoian.summod.registry.LootTableContent;
import dev.armenderoian.summod.registry.SoundContent;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SummerModded implements ModInitializer {

    public static final String MOD_ID = "summermodded";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ItemGroupContent.registerItemGroups();

        ItemContent.registerItems();

        SoundContent.registerSounds();

        LootTableContent.registerLootTables();
    }
}
