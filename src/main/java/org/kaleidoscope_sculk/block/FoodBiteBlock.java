package org.kaleidoscope_sculk.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FoodBiteBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 7);

    protected final FoodProperties foodProperties;
    protected final int maxBites;
    protected final VoxelShape shape;
    protected final Supplier<Item> extraDropSupplier;

    // 效果相关
    @Nullable
    protected final Supplier<MobEffectInstance> effectSupplier;
    protected final float effectChance;

    // 原有构造函数（无效果）
    public FoodBiteBlock(FoodProperties foodProperties, int maxBites, VoxelShape shape, Properties properties) {
        this(foodProperties, maxBites, shape, null, null, 0f, properties);
    }

    // 原有构造函数（有额外掉落）
    public FoodBiteBlock(FoodProperties foodProperties, int maxBites, VoxelShape shape,
                         @Nullable Supplier<Item> extraDropSupplier, Properties properties) {
        this(foodProperties, maxBites, shape, extraDropSupplier, null, 0f, properties);
    }

    // 新构造函数（带效果）
    public FoodBiteBlock(FoodProperties foodProperties, int maxBites, VoxelShape shape,
                         @Nullable Supplier<MobEffectInstance> effectSupplier,
                         float effectChance, Properties properties) {
        this(foodProperties, maxBites, shape, null, effectSupplier, effectChance, properties);
    }

    // 完整构造函数
    public FoodBiteBlock(FoodProperties foodProperties, int maxBites, VoxelShape shape,
                         @Nullable Supplier<Item> extraDropSupplier,
                         @Nullable Supplier<MobEffectInstance> effectSupplier,
                         float effectChance, Properties properties) {
        super(properties);
        this.foodProperties = foodProperties;
        this.maxBites = maxBites;
        this.shape = shape;
        this.extraDropSupplier = extraDropSupplier;
        this.effectSupplier = effectSupplier;
        this.effectChance = effectChance;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(BITES, 0));
    }

    public int getMaxBites() {
        return maxBites;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BITES);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        return eat(level, pos, state, player);
    }

    protected InteractionResult eat(Level level, BlockPos pos, BlockState state, Player player) {
        int currentBites = state.getValue(BITES);

        // 最后一口：只收盘子，不给食物效果
        if (currentBites >= maxBites) {
            if (!level.isClientSide) {
                level.destroyBlock(pos, false, player);
                onFinishEating(level, pos, player);
            }
            return InteractionResult.SUCCESS;
        }

        if (!player.canEat(foodProperties.canAlwaysEat())) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            player.getFoodData().eat(foodProperties.nutrition(), foodProperties.saturation());

            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS,
                    0.5F, level.random.nextFloat() * 0.1F + 0.9F);

            level.gameEvent(player, GameEvent.EAT, pos);

            level.setBlock(pos, state.setValue(BITES, currentBites + 1), Block.UPDATE_ALL);

            // 添加效果（每一口都触发）
            applyEffect(level, player);
        }

        return InteractionResult.SUCCESS;
    }

    // 应用效果的方法
    protected void applyEffect(Level level, Player player) {
        if (effectSupplier != null && level.random.nextFloat() < effectChance) {
            MobEffectInstance effect = effectSupplier.get();
            if (effect != null) {
                // 如果已有同类型效果，先移除再添加（刷新持续时间）
                player.removeEffect(effect.getEffect());
                player.addEffect(effect);
            }
        }
    }

    protected void onFinishEating(Level level, BlockPos pos, Player player) {
        if (!player.getAbilities().instabuild) {
            // 返还碗
            ItemStack bowl = Items.BOWL.getDefaultInstance();
            net.minecraft.world.entity.item.ItemEntity bowlEntity = new net.minecraft.world.entity.item.ItemEntity(
                    level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, bowl
            );
            bowlEntity.setDefaultPickUpDelay();
            level.addFreshEntity(bowlEntity);

            // 额外返还物品
            if (extraDropSupplier != null) {
                Item extraItem = extraDropSupplier.get();
                if (extraItem != null) {
                    ItemStack extraStack = extraItem.getDefaultInstance();
                    net.minecraft.world.entity.item.ItemEntity extraEntity = new net.minecraft.world.entity.item.ItemEntity(
                            level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, extraStack
                    );
                    extraEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(extraEntity);
                }
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        int currentBites = state.getValue(BITES);

        if (currentBites == 0) {
            drops.add(new ItemStack(this.asItem()));
        } else if (currentBites < maxBites) {
            net.minecraft.world.entity.Entity entity = builder.getOptionalParameter(LootContextParams.THIS_ENTITY);
            if (entity instanceof Player player && !player.getAbilities().instabuild) {
                drops.add(new ItemStack(Items.BOWL));
                if (extraDropSupplier != null) {
                    Item extraItem = extraDropSupplier.get();
                    if (extraItem != null) {
                        drops.add(new ItemStack(extraItem));
                    }
                }
            }
        }

        return drops;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        int currentBites = state.getValue(BITES);
        return (maxBites - currentBites) * (15 / maxBites);
    }
}