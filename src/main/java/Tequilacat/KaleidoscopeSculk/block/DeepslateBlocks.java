package Tequilacat.KaleidoscopeSculk.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import Tequilacat.KaleidoscopeSculk.register.ModItems;
import Tequilacat.KaleidoscopeSculk.register.ModRegistries;

import java.util.ArrayList;
import java.util.List;

public final class DeepslateBlocks {

    private DeepslateBlocks() {
    }

    public static class Cake extends Block {

        public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 4);
        public static final int MAX_BITES = 4;

        private static final int NUTRITION = 4;
        private static final float SATURATION = 0.2f;
        private static final float BITE_DAMAGE = 2.0f;

        private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);

        public Cake(Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0));
        }

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return SHAPE;
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(BITES);
        }

        @Nullable
        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            BlockPos pos = context.getClickedPos();
            Level level = context.getLevel();
            if (!level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
                return null;
            }
            return this.defaultBlockState().setValue(BITES, 0);
        }

        @Override
        protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                  Player player, InteractionHand hand, BlockHitResult hitResult) {
            // Holding a slice lets the player add one bite back.
            if (stack.is(ModItems.DEEPSLATE_CAKE_SLICE.get())) {
                int currentBites = state.getValue(BITES);
                if (currentBites <= 0 || currentBites >= MAX_BITES) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }

                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(BITES, Math.max(0, currentBites - 1)), Block.UPDATE_ALL);
                    level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }

                return ItemInteractionResult.SUCCESS;
            }

            return this.handleEatWithItem(state, level, pos, player);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                   Player player, BlockHitResult hitResult) {
            return this.handleEatInteraction(state, level, pos, player);
        }

        private InteractionResult handleEatInteraction(BlockState state, Level level, BlockPos pos, Player player) {
            int currentBites = state.getValue(BITES);
            if (currentBites >= MAX_BITES || !player.canEat(true)) {
                return InteractionResult.PASS;
            }

            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }

            player.getFoodData().eat(NUTRITION, SATURATION);
            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS,
                    1.0f, level.random.nextFloat() * 0.1f + 0.9f);
            advanceBite(state, level, pos, player, currentBites);

            return InteractionResult.SUCCESS;
        }

        private ItemInteractionResult handleEatWithItem(BlockState state, Level level, BlockPos pos, Player player) {
            int currentBites = state.getValue(BITES);
            if (currentBites >= MAX_BITES || !player.canEat(true)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }

            player.getFoodData().eat(NUTRITION, SATURATION);
            hurtOnBite(level, player);
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0));

            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS,
                    1.0f, level.random.nextFloat() * 0.1f + 0.9f);
            advanceBite(state, level, pos, player, currentBites);

            return ItemInteractionResult.SUCCESS;
        }

        private void advanceBite(BlockState state, Level level, BlockPos pos, Player player, int currentBites) {
            int newBites = currentBites + 1;
            if (newBites >= MAX_BITES) {
                level.destroyBlock(pos, false, player);
            } else {
                level.setBlock(pos, state.setValue(BITES, newBites), Block.UPDATE_ALL);
            }
        }

        private void hurtOnBite(Level level, Player player) {
            float newHealth = player.getHealth() - BITE_DAMAGE;
            if (newHealth <= 0.0f) {
                DamageSource damageSource = new DamageSource(
                        level.registryAccess()
                                .registryOrThrow(Registries.DAMAGE_TYPE)
                                .getHolderOrThrow(ModRegistries.DEEPSLATE_CAKE_SLICE),
                        player
                );
                player.hurt(damageSource, BITE_DAMAGE);
            } else {
                player.setHealth(newHealth);
                player.playSound(SoundEvents.PLAYER_HURT, 1.0f, 1.0f);
            }
        }

        @Override
        public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
            List<ItemStack> drops = new ArrayList<>();
            int remaining = MAX_BITES - state.getValue(BITES);
            if (remaining > 0) {
                drops.add(new ItemStack(ModItems.DEEPSLATE_CAKE_SLICE.get(), remaining));
            }
            return drops;
        }

        @Override
        public boolean hasAnalogOutputSignal(BlockState state) {
            return true;
        }

        @Override
        public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
            return MAX_BITES - state.getValue(BITES);
        }
    }

    public static class Stove extends HorizontalDirectionalBlock {

        public static final BooleanProperty LIT = BlockStateProperties.LIT;

        public static final MapCodec<Stove> CODEC = simpleCodec(Stove::new);

        private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

        public Stove(Properties properties) {
            super(properties);
            this.registerDefaultState(this.stateDefinition.any()
                    .setValue(FACING, Direction.NORTH)
                    .setValue(LIT, false));
        }

        public Stove() {
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
                double xOffset = direction.getAxis() == Direction.Axis.X
                        ? direction.getStepX() * 0.5 : (random.nextDouble() - 0.5) * 0.8;
                double zOffset = direction.getAxis() == Direction.Axis.Z
                        ? direction.getStepZ() * 0.5 : (random.nextDouble() - 0.5) * 0.8;

                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
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
                    stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND
                            ? EquipmentSlot.MAINHAND
                            : EquipmentSlot.OFFHAND);
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
}
