package org.kaleidoscope_sculk.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;
import org.kaleidoscope_sculk.block.FoodBiteBlock;
import org.kaleidoscope_sculk.register.ModBlocks;

import java.util.Optional;

import static net.neoforged.neoforge.common.ItemAbilities.SWORD_DIG;

public final class SimpleItems {

    private SimpleItems() {
    }

    public static class ErosionKnife extends SwordItem {

        public ErosionKnife() {
            super(Tiers.STONE, new Properties()
                    .durability(500)
                    .attributes(SwordItem.createAttributes(Tiers.STONE, 3, -2.0f)));
        }

        @Override
        public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
            return itemAbility == SWORD_DIG;
        }
    }

    public static class SilentKnife extends SwordItem {

        private static final float ATTACK_DAMAGE = 5.0f;
        private static final float ATTACK_SPEED = -2.2f;

        public SilentKnife() {
            super(Tiers.DIAMOND, new Properties()
                    .durability(1800)
                    .attributes(SwordItem.createAttributes(Tiers.DIAMOND, ATTACK_DAMAGE, ATTACK_SPEED)));
        }

        @Override
        public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
            return itemAbility == SWORD_DIG;
        }
    }

    /** Echo Seed: planted on top of a sculk catalyst. */
    public static class EchoSeed extends Item {

        private final Block cropBlock;

        public EchoSeed(Block cropBlock, Properties properties) {
            super(properties);
            this.cropBlock = cropBlock;
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos clickedPos = context.getClickedPos();
            var player = context.getPlayer();
            ItemStack stack = context.getItemInHand();

            BlockPos abovePos = clickedPos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            BlockState belowState = level.getBlockState(clickedPos);

            if (aboveState.canBeReplaced() && belowState.is(Blocks.SCULK_CATALYST)) {
                if (!level.isClientSide) {
                    level.setBlock(abovePos, ModBlocks.ECHO_CROP.get().defaultBlockState(), Block.UPDATE_ALL);

                    if (player != null && !player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }
    }

    /** Soul: turns sand into soul sand, dirt into soul soil, and fire into soul fire. */
    public static class Soul extends Item {

        public Soul(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            var player = context.getPlayer();
            ItemStack stack = context.getItemInHand();

            BlockState newState = null;

            if (state.is(Blocks.SAND)) {
                newState = Blocks.SOUL_SAND.defaultBlockState();
            } else if (state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.ROOTED_DIRT)) {
                newState = Blocks.SOUL_SOIL.defaultBlockState();
            } else if (state.is(Blocks.FIRE)) {
                newState = Blocks.SOUL_FIRE.defaultBlockState();
            }

            if (newState != null) {
                if (!level.isClientSide) {
                    level.setBlock(pos, newState, Block.UPDATE_ALL);
                    level.playSound(null, pos, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 1.0f, 1.0f);

                    if (player != null && !player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }
    }

    /** Whole dish: nutrition equals per-bite nutrition x bite count. */
    public static class FoodBlock extends BowlFoodBlockItem {

        public FoodBlock(Block block) {
            super(block, getWholeDishFoodProperties(block), getBlockContainer(block));
        }

        private static FoodProperties getBlockFoodProperties(Block block) {
            return block instanceof FoodBiteBlock foodBite
                    ? foodBite.getFoodProperties()
                    : new FoodProperties.Builder().build();
        }

        private static FoodProperties getWholeDishFoodProperties(Block block) {
            FoodProperties biteFood = getBlockFoodProperties(block);
            if (!(block instanceof FoodBiteBlock foodBite)) {
                return biteFood;
            }
            int maxBites = Math.max(1, foodBite.getMaxBites());
            return new FoodProperties(
                    biteFood.nutrition() * maxBites,
                    biteFood.saturation(),
                    biteFood.canAlwaysEat(),
                    biteFood.eatSeconds(),
                    Optional.empty(),
                    biteFood.effects()
            );
        }

        @Nullable
        private static ItemLike getBlockContainer(Block block) {
            return block instanceof FoodBiteBlock foodBite ? foodBite.getContainer() : null;
        }
    }
}
