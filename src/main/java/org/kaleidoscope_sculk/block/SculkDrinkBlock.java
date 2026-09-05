package org.kaleidoscope_sculk.block;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.DrinkBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SculkDrinkBlock extends DrinkBlock {

    public SculkDrinkBlock(int maxCount, VoxelShape... shapes) {
        super(maxCount, shapes);
    }

    @Override
    public boolean tryIncreaseCount(Level level, BlockPos pos, BlockState state, ItemStack stack) {
        // 永远不允许叠加
        return false;
    }
}