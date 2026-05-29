//package org.kaleidoscope_sculk.block;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.food.FoodProperties;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.phys.shapes.VoxelShape;
//
//import javax.annotation.Nullable;
//import java.util.function.Supplier;
//
//public class SculkStewBlock extends FoodBiteBlock {
//
//    @Nullable
//    private final Supplier<MobEffectInstance> effectSupplier;
//    private final float effectChance;
//
//    public SculkStewBlock(FoodProperties foodProperties, int maxBites, VoxelShape shape,
//                          @Nullable Supplier<MobEffectInstance> effectSupplier,
//                          float effectChance, Properties properties) {
//        super(foodProperties, maxBites, shape, null, properties);
//        this.effectSupplier = effectSupplier;
//        this.effectChance = effectChance;
//    }
//
//    @Override
//    protected void onFinishEating(Level level, BlockPos pos, Player player) {
//        if (!player.getAbilities().instabuild) {
//            ItemStack flowerPot = Items.FLOWER_POT.getDefaultInstance();
//            net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
//                    level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, flowerPot
//            );
//            itemEntity.setDefaultPickUpDelay();
//            level.addFreshEntity(itemEntity);
//        }
//    }
//
//    @Override
//    protected InteractionResult eat(Level level, BlockPos pos, BlockState state, Player player) {
//        InteractionResult result = super.eat(level, pos, state, player);
//
//        if (result.consumesAction() && !level.isClientSide && effectSupplier != null) {
//            if (level.random.nextFloat() < effectChance) {
//                MobEffectInstance newEffect = effectSupplier.get();
//                if (newEffect != null) {
//                    player.removeEffect(newEffect.getEffect());
//                    player.addEffect(newEffect);
//                }
//            }
//        }
//
//        return result;
//    }
//}