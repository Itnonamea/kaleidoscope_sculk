package org.kaleidoscope_sculk;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)  // 删除 bus 参数
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLE_FEATURE = BUILDER
            .comment("是否启用某个功能")
            .define("enableFeature", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enableFeature;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enableFeature = ENABLE_FEATURE.get();
    }
}