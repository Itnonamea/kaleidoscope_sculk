package org.kaleidoscope_sculk.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.kaleidoscope_sculk.register.ModBlocks;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider {

    public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        return new LootTableProvider(output, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootSubProvider::new, LootContextParamSets.BLOCK)),
                registries);
    }

    public static class ModBlockLootSubProvider extends BlockLootSubProvider {

        protected ModBlockLootSubProvider(HolderLookup.Provider registries) {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            dropSelf(ModBlocks.SCULK_STEW_BLOCK.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return List.of(ModBlocks.SCULK_STEW_BLOCK.get());
        }
    }
}