package dev.armenderoian.summad.data;

import dev.armenderoian.summad.data.provider.ModelDataProvider;
import dev.armenderoian.summad.data.provider.RecipeDataProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGeneration implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(ModelDataProvider::new);
        pack.addProvider(RecipeDataProvider::new);
    }
}
