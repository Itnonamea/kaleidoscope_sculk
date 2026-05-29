package org.kaleidoscope_sculk.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DeepslateStoveBlock extends HorizontalDirectionalBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final MapCodec<DeepslateStoveBlock> CODEC = simpleCodec(DeepslateStoveBlock::new);

    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public DeepslateStoveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));
    }

    public DeepslateStoveBlock() {
        this(Properties.of()
                .mapColor(MapColor.DEEPSLATE)
                .sound(SoundType.DEEPSLATE)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> state.getValue(LIT) ? 13 : 0)
                .randomTicks()
                .strength(1.5F, 6.0F)
                .noOcclusion());
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z,
                        SoundEvents.CAMPFIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        0.5F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.6F, false);
            }

            level.addParticle(ParticleTypes.SMOKE,
                    x + (random.nextDouble() - 0.5) * 0.6,
                    pos.getY() + 0.6 + random.nextDouble() * 0.3,
                    z + (random.nextDouble() - 0.5) * 0.6,
                    0, 0.02, 0);

            Direction direction = state.getValue(FACING);
            double xOffset = direction.getAxis() == Direction.Axis.X ? direction.getStepX() * 0.5 : (random.nextDouble() - 0.5) * 0.8;
            double zOffset = direction.getAxis() == Direction.Axis.Z ? direction.getStepZ() * 0.5 : (random.nextDouble() - 0.5) * 0.8;

            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,  // 改为灵魂火焰
                    pos.getX() + 0.5 + xOffset,
                    pos.getY() + 0.6,
                    pos.getZ() + 0.5 + zOffset,
                    0, 0, 0);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT) && level.isRainingAt(pos.above())) {
            level.setBlockAndUpdate(pos, state.setValue(LIT, false));
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F);
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(LIT) && direction == Direction.UP && level.isWaterAt(neighborPos)) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.setBlockAndUpdate(pos, state.setValue(LIT, false));
                serverLevel.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F);
            }
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(LIT) && (stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE))) {
            level.setBlockAndUpdate(pos, state.setValue(LIT, true));

            if (stack.is(Items.FIRE_CHARGE)) {
                level.playSound(player, pos, SoundEvents.FIRECHARGE_USE,
                        SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            } else {
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE,
                        SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                // 修复：将 InteractionHand 转换为 EquipmentSlot
                stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ?
                        net.minecraft.world.entity.EquipmentSlot.MAINHAND :
                        net.minecraft.world.entity.EquipmentSlot.OFFHAND);
            }
            return ItemInteractionResult.SUCCESS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }
}