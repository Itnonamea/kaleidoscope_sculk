package org.kaleidoscope_sculk.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.kaleidoscope_sculk.register.ModBlocks;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.ArrayList;
import java.util.List;

public class EchoCropBlock extends CropBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    private static final int MATURE_AGE = 7;
    private static final int RESET_AGE = 4;

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    public EchoCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MATURE_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.ECHO_SEED.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.SCULK_CATALYST);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(Blocks.SCULK_CATALYST);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean isMoving) {
        if (!level.getBlockState(pos.below()).is(Blocks.SCULK_CATALYST)) {
            destroyPlant(level, pos, state);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.getBlockState(pos.below()).is(Blocks.SCULK_CATALYST)) {
            destroyPlant(level, pos, state);
            return;
        }

        int currentAge = state.getValue(AGE);

        if (currentAge < MATURE_AGE) {
            float growthChance = getGrowthSpeed(state, level, pos);
            if (random.nextInt((int)(25.0F / growthChance) + 1) == 0) {
                int newAge = currentAge + 1;
                level.setBlock(pos, state.setValue(AGE, newAge), 2);

                if (newAge >= MATURE_AGE) {
                    createTopBlock(level, pos);
                }
            }
        }
    }

    private void createTopBlock(ServerLevel level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        if (level.isEmptyBlock(abovePos)) {
            level.setBlock(abovePos, ModBlocks.ECHO_CROP_TOP.get().defaultBlockState()
                    .setValue(EchoCropTopBlock.AGE, MATURE_AGE), Block.UPDATE_ALL);
        }
    }

    /**
     * 破坏整个植物，如果成熟则掉落物品
     */
    private void destroyPlant(Level level, BlockPos pos, BlockState state) {
        boolean isMature = state.getValue(AGE) >= MATURE_AGE;

        // 破坏顶层
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        if (aboveState.getBlock() instanceof EchoCropTopBlock) {
            if (isMature) {
                // 成熟时，让顶层掉落物品
                level.destroyBlock(abovePos, true);
            } else {
                // 未成熟时，直接破坏不掉落
                level.destroyBlock(abovePos, false);
            }
        }

        // 破坏底层
        level.destroyBlock(pos, false);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.BONE_MEAL)) {
            int currentAge = state.getValue(AGE);

            if (currentAge >= MATURE_AGE) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (!level.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) level;
                RandomSource random = serverLevel.getRandom();

                int newAge = Math.min(currentAge + random.nextInt(3) + 1, MATURE_AGE);
                serverLevel.setBlock(pos, state.setValue(AGE, newAge), Block.UPDATE_ALL);
                serverLevel.levelEvent(2005, pos, 0);

                if (newAge >= MATURE_AGE) {
                    createTopBlock(serverLevel, pos);
                }

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        // 底层不掉落任何物品
        return new ArrayList<>();
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        boolean isMature = state.getValue(AGE) >= MATURE_AGE;

        // 破坏顶层
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        if (aboveState.getBlock() instanceof EchoCropTopBlock) {
            if (isMature) {
                level.destroyBlock(abovePos, true);
            } else {
                level.destroyBlock(abovePos, false);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!newState.is(this)) {
            boolean isMature = state.getValue(AGE) >= MATURE_AGE;

            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.getBlock() instanceof EchoCropTopBlock) {
                if (isMature) {
                    level.destroyBlock(abovePos, true);
                } else {
                    level.destroyBlock(abovePos, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}