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

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // 语言文件
        generator.addProvider(true, new ModLanguageProvider(output, "en_us"));
        generator.addProvider(true, new ModLanguageProvider(output, "zh_cn"));

        // 物品模型
        generator.addProvider(true, new ModItemModelProvider(output, existingFileHelper));

        // 战利品表
        generator.addProvider(true, ModLootTableProvider.create(output, lookupProvider));

        generator.addProvider(true, new ModRecipeProvider(output, lookupProvider));
    }
}