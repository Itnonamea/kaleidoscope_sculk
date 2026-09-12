package org.kaleidoscope_sculk;

import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.DrinkBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.kaleidoscope_sculk.network.SonicBoomPacket;
import org.kaleidoscope_sculk.register.ModBlocks;
import org.kaleidoscope_sculk.register.ModDamageTypes;
import org.kaleidoscope_sculk.register.ModDataComponents;
import org.kaleidoscope_sculk.register.ModEffects;
import org.kaleidoscope_sculk.register.ModEntities;
import org.kaleidoscope_sculk.register.ModFluids;
import org.kaleidoscope_sculk.register.ModItems;
import org.kaleidoscope_sculk.register.ModPotions;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Mod(Kaleidoscope_sculk.MODID)
public class Kaleidoscope_sculk {

    public static final String MODID = "kaleidoscope_sculk";

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Field VALID_BLOCKS_FIELD = lookupValidBlocksField();

    private static Field lookupValidBlocksField() {
        try {
            Field field = BlockEntityType.class.getDeclaredField("validBlocks");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            LOGGER.error("无法定位 BlockEntityType.validBlocks 字段，饮品方块注册将跳过", e);
            return null;
        }
    }

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

        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::registerPayloads);
    }

    
    @SuppressWarnings("unchecked")
    private void onCommonSetup(FMLCommonSetupEvent event) {
        if (VALID_BLOCKS_FIELD == null) {
            return;
        }

        event.enqueueWork(() -> {
            try {
                BlockEntityType<DrinkBlockEntity> drinkBe =
                        (BlockEntityType<DrinkBlockEntity>) com.github.ysbbbbbb.kaleidoscopetavern.init.ModBlocks.DRINK_BE.get();

                Set<Block> validBlocks = (Set<Block>) VALID_BLOCKS_FIELD.get(drinkBe);
                Set<Block> newValidBlocks = new HashSet<>(validBlocks.size() + 3);
                newValidBlocks.addAll(validBlocks);
                newValidBlocks.add(ModBlocks.SCULK_BREW_BOTTLE.get());
                newValidBlocks.add(ModBlocks.HUADIAO_WINE.get());
                newValidBlocks.add(ModBlocks.HONGLAN_WINE.get());
                VALID_BLOCKS_FIELD.set(drinkBe, newValidBlocks);

                LOGGER.info("已将 sculk_brew_bottle / huadiao_wine / honglan_wine 添加到 Tavern 的 DRINK_BE");
            } catch (Exception e) {
                LOGGER.error("添加方块到 DRINK_BE 失败", e);
            }
        });
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(SonicBoomPacket.TYPE, SonicBoomPacket.CODEC, SonicBoomPacket::handle);
    }
}
