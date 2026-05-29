package org.kaleidoscope_sculk.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.Random;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class SoulFireDropHandler {

    private static final Random RANDOM = new Random();
    private static final float DROP_CHANCE = 0.03f; // 3% 几率

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();

        if (state.is(Blocks.SOUL_FIRE)) {
            if (RANDOM.nextFloat() < DROP_CHANCE) {
                if (!level.isClientSide) {
                    ItemEntity drop = new ItemEntity(
                            level,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            ModItems.SOUL.get().getDefaultInstance()
                    );
                    drop.setDefaultPickUpDelay();
                    level.addFreshEntity(drop);
                }
            }
        }
    }
}