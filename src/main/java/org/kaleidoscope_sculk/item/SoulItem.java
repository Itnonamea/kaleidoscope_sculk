package org.kaleidoscope_sculk.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SoulItem extends Item {

    public SoulItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockState newState = null;

        // 沙子 -> 灵魂沙
        if (state.is(Blocks.SAND)) {
            newState = Blocks.SOUL_SAND.defaultBlockState();
        }
        // 土 -> 灵魂土
        else if (state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.ROOTED_DIRT)) {
            newState = Blocks.SOUL_SOIL.defaultBlockState();
        }
        // 火 -> 灵魂火
        else if (state.is(Blocks.FIRE)) {
            newState = Blocks.SOUL_FIRE.defaultBlockState();
        }

        if (newState != null) {
            if (!level.isClientSide) {
                level.setBlock(pos, newState, Block.UPDATE_ALL);
                // 修复：使用 .value() 获取实际的 SoundEvent
                level.playSound(null, pos, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 1.0f, 1.0f);

                if (player != null && !player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}