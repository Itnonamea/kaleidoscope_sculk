// java/org/kaleidoscope_sculk/block/DeepslateCakeBlock.java
package org.kaleidoscope_sculk.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.kaleidoscope_sculk.register.ModDamageTypes;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.ArrayList;
import java.util.List;

public class DeepslateCakeBlock extends Block {

    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 4);
    public static final int MAX_BITES = 4;

    // 固定形状：14x7x14
    private static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);

    public DeepslateCakeBlock(Properties properties) {
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

        if (!level.getBlockState(pos.below()).isSolid()) {
            return null;
        }

        return this.defaultBlockState().setValue(BITES, 0);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        // 使用蛋糕片补充蛋糕
        if (stack.is(ModItems.DEEPSLATE_CAKE_SLICE.get())) {
            int currentBites = state.getValue(BITES);

            // 只有蛋糕被吃过（bites > 0）且未吃完（bites < 8）才能补充
            if (currentBites > 0 && currentBites < MAX_BITES) {
                if (!level.isClientSide) {
                    int newBites = Math.max(0, currentBites - 1);
                    level.setBlock(pos, state.setValue(BITES, newBites), Block.UPDATE_ALL);

                    level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // 手持其他物品时也能吃蛋糕（使用 alwaysEdible = true 允许饥饿值满时也吃）
        return handleEat(state, level, pos, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        // 空手右键点击 - 吃蛋糕
        return handleEatInteraction(state, level, pos, player);
    }

    /**
     * 处理吃蛋糕逻辑（返回 InteractionResult，用于 useWithoutItem）
     */
    private InteractionResult handleEatInteraction(BlockState state, Level level, BlockPos pos, Player player) {
        int currentBites = state.getValue(BITES);

        // 如果已经吃完
        if (currentBites >= MAX_BITES) {
            return InteractionResult.PASS;
        }

        // 使用 alwaysEdible = true，允许饥饿值满时也吃
        if (!player.canEat(true)) {
            return InteractionResult.PASS;
        }

        // 客户端直接返回成功
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // 服务端执行进食逻辑
        player.getFoodData().eat(4, 0.3f);

        // 播放进食音效
        level.playSound(null, pos, SoundEvents.GENERIC_EAT,
                SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);

        int newBites = currentBites + 1;

        if (newBites >= MAX_BITES) {
            // 全部吃完，破坏方块
            level.destroyBlock(pos, false, player);
        } else {
            level.setBlock(pos, state.setValue(BITES, newBites), Block.UPDATE_ALL);
        }

        return InteractionResult.SUCCESS;
    }

    private ItemInteractionResult handleEat(BlockState state, Level level, BlockPos pos, Player player) {
        int currentBites = state.getValue(BITES);

        if (currentBites >= MAX_BITES) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!player.canEat(true)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        // 补充饥饿值（2点饥饿值，0.1饱和度）
        player.getFoodData().eat(4, 0.3f);

        // 扣除2点血量
        float currentHealth = player.getHealth();
        float newHealth = currentHealth - 2.0f;

        if (newHealth <= 0) {
            DamageSource damageSource = new DamageSource(
                    level.registryAccess()
                            .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(ModDamageTypes.DEEPSLATE_CAKE_SLICE),
                    player
            );
            player.hurt(damageSource, 2.0f);
        } else {
            player.setHealth(newHealth);
            player.playSound(SoundEvents.PLAYER_HURT, 1.0F, 1.0F);
        }

        // 添加生命恢复效果
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 0));

        // 播放进食音效
        level.playSound(null, pos, SoundEvents.GENERIC_EAT,
                SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);

        int newBites = currentBites + 1;

        if (newBites >= MAX_BITES) {
            level.destroyBlock(pos, false, player);
        } else {
            level.setBlock(pos, state.setValue(BITES, newBites), Block.UPDATE_ALL);
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();

        int currentBites = state.getValue(BITES);
        int remaining = MAX_BITES - currentBites;

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