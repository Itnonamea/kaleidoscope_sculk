package org.kaleidoscope_sculk.register;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("客户端启动 - {} 已加载", Kaleidoscope_sculk.MODID);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ANCIENT_BONE_FRAGMENT_PROJECTILE.get(),
                ThrownItemRenderer::new);
    }
}