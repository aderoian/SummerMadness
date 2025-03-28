package dev.armenderoian.summod.item;

import dev.armenderoian.summod.SummerModded;
import dev.armenderoian.summod.sound.SoundContent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ItemContent {

    public static final Item BUGS_MUSIC_DISK = registerItem("bugs_music_disc",
            new Item(new Item.Settings().rarity(Rarity.RARE).jukeboxPlayable(SoundContent.BUGS_SONG_KEY).maxCount(1)));

    public static void registerItems() {
        SummerModded.LOGGER.info("Registering items for '" + SummerModded.MOD_ID + "'.");
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(SummerModded.MOD_ID, name), item);
    }
}
