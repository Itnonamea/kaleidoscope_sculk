package org.kaleidoscope_sculk.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FoodBiteBlock extends com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock {

    @Nullable
    private final ItemLike container;

    public FoodBiteBlock(FoodProperties foodProperties, int maxBites, VoxelShape shape) {
        this(foodProperties, maxBites, shape, null);
    }

    public FoodBiteBlock(FoodProperties foodProperties, int maxBites, VoxelShape shape,
                         @Nullable ItemLike container) {
        super(foodProperties, maxBites, null);
        this.container = container;
        this.setAABB(shape);
    }

    public FoodProperties getFoodProperties() {
        return this.foodProperties;
    }

    @Nullable
    public ItemLike getContainer() {
        return container;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        int currentBites = state.getValue(getBites());
        int maxBites = getMaxBites();
        return (maxBites - currentBites) * 15 / maxBites;
    }
}
