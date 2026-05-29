package org.kaleidoscope_sculk;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.kaleidoscope_sculk.network.SonicBoomPacket;
import org.kaleidoscope_sculk.register.*;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(Kaleidoscope_sculk.MODID)
public class Kaleidoscope_sculk {
    public static final String MODID = "kaleidoscope_sculk";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Kaleidoscope_sculk(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        ModItems.CREATIVE_TABS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

        // 注册网络
        modEventBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SonicBoomPacket.TYPE, SonicBoomPacket.CODEC, SonicBoomPacket::handle);
    }

    @net.neoforged.fml.common.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("客户端启动 - {} 已加载", MODID);
        }
    }
}