package org.kaleidoscope_sculk.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.kaleidoscope_sculk.register.ModBlocks;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.ArrayList;
import java.util.List;

public class EchoCropBlock extends CropBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    private static final int MATURE_AGE = 7;
    private static final int RESET_AGE = 0;

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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos,
                                       Player player) {
        return new ItemStack(ModItems.SCULK_PINAPPLE.get());
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

    private static boolean isOnValidSoil(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(Blocks.SCULK_CATALYST);
    }

    private static void destroyTopIfPresent(Level level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        if (level.getBlockState(abovePos).getBlock() instanceof EchoCropTopBlock) {
            level.destroyBlock(abovePos, false);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return isOnValidSoil(level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean isMoving) {
        if (!isOnValidSoil(level, pos)) {
            destroyPlant(level, pos, state);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!isOnValidSoil(level, pos)) {
            destroyPlant(level, pos, state);
            return;
        }

        int currentAge = state.getValue(AGE);

        if (currentAge < MATURE_AGE) {
            float growthChance = getGrowthSpeed(state, level, pos);
            if (random.nextInt((int) (25.0F / growthChance) + 1) == 0) {
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

    
    private void destroyPlant(Level level, BlockPos pos, BlockState state) {
        destroyTopIfPresent(level, pos);

        
        level.destroyBlock(pos, true);
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
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age < MATURE_AGE) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }

        if (!level.isClientSide) {
            int seedCount = 1 + level.random.nextInt(2);
            popResource(level, pos, new ItemStack(ModItems.ECHO_SEED.get(), seedCount));
            popResource(level, pos, new ItemStack(ModItems.SCULK_PINAPPLE.get()));

            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);

            destroyTopIfPresent(level, pos);

            
            level.setBlock(pos, state.setValue(AGE, RESET_AGE), Block.UPDATE_ALL);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();

        
        if (state.getValue(AGE) >= MATURE_AGE) {
            int seedCount = 1 + builder.getLevel().random.nextInt(2);
            drops.add(new ItemStack(ModItems.ECHO_SEED.get(), seedCount));
            drops.add(new ItemStack(ModItems.SCULK_PINAPPLE.get()));
        }

        return drops;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        
        destroyTopIfPresent(level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!newState.is(this)) {
            
            destroyTopIfPresent(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
