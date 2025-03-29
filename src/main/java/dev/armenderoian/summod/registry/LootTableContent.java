package dev.armenderoian.summod.registry;

import dev.armenderoian.summod.SummerModded;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.List;

public class LootTableContent {

    public static final List<RegistryKey<LootTable>> INJECTED_LOOT_TABLES = List.of(
            LootTables.VILLAGE_DESERT_HOUSE_CHEST,
            LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
            LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
            LootTables.VILLAGE_PLAINS_CHEST,
            LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
            LootTables.JUNGLE_TEMPLE_CHEST,
            LootTables.ABANDONED_MINESHAFT_CHEST,
            LootTables.DESERT_PYRAMID_CHEST,
            LootTables.STRONGHOLD_CORRIDOR_CHEST,
            LootTables.SIMPLE_DUNGEON_CHEST
    );

    public static void registerLootTables() {
        SummerModded.LOGGER.info("Registering loot tables for '" + SummerModded.MOD_ID + "'.");
        LootTableEvents.MODIFY.register(LootTableContent::onLootTableModify);
    }

    private static void onLootTableModify(RegistryKey<LootTable> id, LootTable.Builder supplier, LootTableSource source, RegistryWrapper.WrapperLookup registries) {
        if (source.isBuiltin() && INJECTED_LOOT_TABLES.contains(id)) {
            SummerModded.LOGGER.info("Injecting loot table for '{}'.", getInjectedLootTableKey(id).toString());

            // Load loot table from data pack
            supplier.pool(LootPool.builder()
                    .with(LootTableEntry.builder(getInjectedLootTableKey(id)))
                    .build());
        }
    }

    private static RegistryKey<LootTable> getInjectedLootTableKey(RegistryKey<LootTable> id) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(SummerModded.MOD_ID, "inject/" + id.getValue().getPath()));
    }
}
