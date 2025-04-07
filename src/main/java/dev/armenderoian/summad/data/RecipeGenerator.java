package dev.armenderoian.summad.data;

import dev.armenderoian.summad.registry.ItemContent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends FabricRecipeProvider {

    public RecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        // Death Reset Item Recipe
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ItemContent.DEATH_RESET_ITEM).pattern("ddd").pattern("sus").pattern("nnn")
                .input('d', Items.DIAMOND)
                .input('s', Items.NETHER_STAR)
                .input('u', Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .input('n', Items.NETHERITE_INGOT)
                .criterion("has_item", conditionsFromItem(Items.NETHER_STAR))
                .offerTo(recipeExporter, "crafting/death_reset_item");
    }
}
