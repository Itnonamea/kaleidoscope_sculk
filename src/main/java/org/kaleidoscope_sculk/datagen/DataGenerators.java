package org.kaleidoscope_sculk.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // 注册语言文件生成器
        generator.addProvider(true, new ModLanguageProvider(output));

        // 注册物品模型生成器
        generator.addProvider(true, new ModItemModelProvider(output, existingFileHelper));

        // 注册配方生成器
        generator.addProvider(true, new ModRecipeProvider(output, lookupProvider));

        // 注册标签生成器（可选）
        generator.addProvider(true, new ModItemTagsProvider(output, lookupProvider, existingFileHelper));
    }
}