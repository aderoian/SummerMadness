package dev.armenderoian.summod;

import dev.armenderoian.summod.item.ItemContent;
import dev.armenderoian.summod.item.ItemGroupContent;
import dev.armenderoian.summod.sound.SoundContent;
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
    }
}
