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
import org.kaleidoscope_sculk.network.ModPackets;
import org.kaleidoscope_sculk.register.ModBlocks;
import org.kaleidoscope_sculk.register.ModEffects;
import org.kaleidoscope_sculk.register.ModItems;
import org.kaleidoscope_sculk.register.ModRegistries;
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
            LOGGER.error("Could not locate the BlockEntityType.validBlocks field; drink block registration will be skipped", e);
            return null;
        }
    }

    public Kaleidoscope_sculk(IEventBus modEventBus) {
        ModRegistries.registerTeas();

        ModItems.ITEMS.register(modEventBus);
        ModItems.CREATIVE_TABS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModRegistries.ENTITY_TYPES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModRegistries.FLUID_TYPES.register(modEventBus);
        ModRegistries.FLUIDS.register(modEventBus);
        ModRegistries.POTIONS.register(modEventBus);
        ModRegistries.DATA_COMPONENTS.register(modEventBus);
        ModRegistries.DAMAGE_TYPES.register(modEventBus);

        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(ModItems::onBuildCreativeTabContents);
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

                LOGGER.info("Added sculk_brew_bottle / huadiao_wine / honglan_wine to Tavern's DRINK_BE");
            } catch (Exception e) {
                LOGGER.error("Failed to add blocks to Tavern's DRINK_BE", e);
            }
        });
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ModPackets.SonicBoom.TYPE, ModPackets.SonicBoom.CODEC, ModPackets.SonicBoom::handle);
        registrar.playToServer(ModPackets.SoulSailAbsorb.TYPE, ModPackets.SoulSailAbsorb.CODEC,
                ModPackets.SoulSailAbsorb::handle);
    }
}
