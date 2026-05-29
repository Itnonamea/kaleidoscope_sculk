package org.kaleidoscope_sculk.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class FoodBiteBlock extends Block {

    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 4);

    protected final FoodProperties foodProperties;
    protected final int maxBites;
    private final int nutrition;
    private final float saturationModifier;
    protected VoxelShape shape;

    public FoodBiteBlock(FoodProperties foodProperties, int maxBites, Properties properties) {
        super(properties);
        this.foodProperties = foodProperties;
        this.maxBites = maxBites;
        this.nutrition = foodProperties.nutrition();
        this.saturationModifier = 0.6f; // 直接设置默认值，不调用不存在的方法
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BITES);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        int bites = state.getValue(BITES);

        if (bites >= maxBites) {
            level.destroyBlock(pos, false, player);
            onFinishEating(level, pos, player);
            return InteractionResult.SUCCESS;
        }

        if (!player.canEat(foodProperties.canAlwaysEat())) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            player.getFoodData().eat(nutrition, saturationModifier);

            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS,
                    0.5F, level.random.nextFloat() * 0.1F + 0.9F);

            level.gameEvent(player, GameEvent.EAT, pos);

            if (bites + 1 >= maxBites) {
                level.destroyBlock(pos, false, player);
                onFinishEating(level, pos, player);
            } else {
                level.setBlock(pos, state.setValue(BITES, bites + 1), Block.UPDATE_ALL);
            }
        }

        return InteractionResult.SUCCESS;
    }

    protected void onFinishEating(Level level, BlockPos pos, Player player) {
    }
}