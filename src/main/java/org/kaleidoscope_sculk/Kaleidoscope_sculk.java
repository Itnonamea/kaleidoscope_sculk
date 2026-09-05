package org.kaleidoscope_sculk;

import com.github.ysbbbbbb.kaleidoscopetavern.item.DrinkBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.kaleidoscope_sculk.handler.SoulSailXpHandler;
import org.kaleidoscope_sculk.network.SonicBoomPacket;
import org.kaleidoscope_sculk.register.*;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

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
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModDamageTypes.DAMAGE_TYPES.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);

        NeoForge.EVENT_BUS.register(SoulSailXpHandler.class);

        // 使用反射将方块添加到主模组的 DRINK_BE
        modEventBus.addListener(this::onCommonSetup);

        modEventBus.addListener(this::registerPayloads);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                var drinkBe = com.github.ysbbbbbb.kaleidoscopetavern.init.ModBlocks.DRINK_BE.get();
                Field validBlocksField = net.minecraft.world.level.block.entity.BlockEntityType.class
                        .getDeclaredField("validBlocks");
                validBlocksField.setAccessible(true);
                @SuppressWarnings("unchecked")
                Set<Block> validBlocks = (Set<Block>) validBlocksField.get(drinkBe);
                Set<Block> newValidBlocks = new HashSet<>(validBlocks);

                // 添加三个方块
                newValidBlocks.add(ModBlocks.SCULK_BREW_BOTTLE.get());
                newValidBlocks.add(ModBlocks.HONGLAN_JIU_BOTTLE.get());
                newValidBlocks.add(ModBlocks.HUADIAO_JIU_BOTTLE.get());

                validBlocksField.set(drinkBe, newValidBlocks);
            } catch (Exception e) {
            }
        });
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
    private void registerItems(RegisterEvent event) {
        // 只处理物品注册事件
        if (event.getRegistryKey() == Registries.ITEM) {
            event.register(Registries.ITEM, helper -> {
                // 此时主模组的物品已经全部注册完成
                helper.register(
                        ResourceLocation.parse(Kaleidoscope_sculk.MODID + ":sculk_brew"),
                        new DrinkBlockItem(ModBlocks.SCULK_BREW_BOTTLE.get())
                );
                LOGGER.info("已延迟注册 sculk_brew (DrinkBlockItem)");
            });
        }
    }
}