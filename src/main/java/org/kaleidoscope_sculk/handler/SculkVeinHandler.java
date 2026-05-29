package org.kaleidoscope_sculk.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModItems;
import org.kaleidoscope_sculk.register.ModTags;

import java.util.Random;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class SculkVeinDropHandler {

    private static final Random RANDOM = new Random();
    private static final float DROP_CHANCE = 0.05f; // 5%

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getPlayer();

        // 检查是否是幽匿脉络
        if (!state.is(Blocks.SCULK_VEIN)) return;

        // 检查玩家是否手持厨刀（如果是厨刀破坏，则不触发掉落）
        ItemStack handItem = player.getMainHandItem();
        if (handItem.is(ModTags.KITCHEN_KNIVES)) return;

        // 5% 几率掉落幽匿猪儿虫
        if (RANDOM.nextFloat() < DROP_CHANCE) {
            if (!level.isClientSide) {
                ItemEntity drop = new ItemEntity(
                        level,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        ModItems.SCULK_CATERPILLAR.get().getDefaultInstance()
                );
                drop.setDefaultPickUpDelay();
                level.addFreshEntity(drop);
            }
        }
    }
}