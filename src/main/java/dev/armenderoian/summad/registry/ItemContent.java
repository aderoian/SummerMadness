package dev.armenderoian.summad.registry;

import dev.armenderoian.summad.SummerMadness;
import dev.armenderoian.summad.custom.item.DeathResetItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ItemContent {

    public static final Item BUGS_MUSIC_DISK = registerItem("bugs_music_disc",
            new Item(new Item.Settings().rarity(Rarity.RARE).jukeboxPlayable(SoundContent.BUGS_SONG_KEY).maxCount(1)));

    public static final Item DEATH_RESET_ITEM = registerItem("death_reset_item",
            new DeathResetItem(new Item.Settings().rarity(Rarity.RARE).maxCount(1)));

    public static void registerItems() {
        SummerMadness.LOGGER.info("Registering items for '" + SummerMadness.MOD_ID + "'.");
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(SummerMadness.MOD_ID, name), item);
    }
}
