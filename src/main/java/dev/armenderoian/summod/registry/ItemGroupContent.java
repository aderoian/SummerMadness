package dev.armenderoian.summod.registry;

import dev.armenderoian.summod.SummerModded;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemGroupContent {

    public static ItemGroup SUMMER_MODDED_GROUP = registerItemGroup("summermodded_group",
            FabricItemGroup.builder().icon(() -> new ItemStack(ItemContent.BUGS_MUSIC_DISK))
                    .displayName(Text.translatable("itemgroup.summermodded"))
                    .entries((displayContext, entries) -> {
                        entries.add(ItemContent.BUGS_MUSIC_DISK);
                    })
                    .build());

    public static void registerItemGroups() {
        SummerModded.LOGGER.info("Registering item groups for '" + SummerModded.MOD_ID + "'.");
    }

    public static ItemGroup registerItemGroup(String name, ItemGroup group) {
        return Registry.register(Registries.ITEM_GROUP, Identifier.of(SummerModded.MOD_ID, name), group);
    }
}
