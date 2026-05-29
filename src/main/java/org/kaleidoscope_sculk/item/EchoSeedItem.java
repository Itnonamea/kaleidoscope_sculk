package org.kaleidoscope_sculk.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.kaleidoscope_sculk.register.ModBlocks;

public class EchoSeedItem extends Item {

    private final Block cropBlock;

    public EchoSeedItem(Block cropBlock, Properties properties) {
        super(properties);
        this.cropBlock = cropBlock;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockPos abovePos = clickedPos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        BlockState belowState = level.getBlockState(clickedPos);

        // 检查下方是否是幽匿催发体
        if (aboveState.canBeReplaced() && belowState.is(Blocks.SCULK_CATALYST)) {

            if (!level.isClientSide) {
                // 只放置底层作物
                level.setBlock(abovePos, ModBlocks.ECHO_CROP.get().defaultBlockState(), Block.UPDATE_ALL);

                if (player != null && !player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}