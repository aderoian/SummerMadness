package dev.armenderoian.summad.registry;

import dev.armenderoian.summad.SummerMadness;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemGroupContent {

    public static ItemGroup SUMMER_MODDED_GROUP = registerItemGroup("summermadness_group",
            FabricItemGroup.builder().icon(() -> new ItemStack(ItemContent.DEATH_RESET_ITEM))
                    .displayName(Text.translatable("itemgroup.summermadness"))
                    .entries((displayContext, entries) -> {
                        entries.add(ItemContent.DEATH_RESET_ITEM);
                    })
                    .build());

    public static void registerItemGroups() {
        SummerMadness.LOGGER.info("Registering item groups for '" + SummerMadness.MOD_ID + "'.");
    }

    public static ItemGroup registerItemGroup(String name, ItemGroup group) {
        return Registry.register(Registries.ITEM_GROUP, Identifier.of(SummerMadness.MOD_ID, name), group);
    }
}
