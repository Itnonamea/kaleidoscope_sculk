package org.kaleidoscope_sculk.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.kaleidoscope_sculk.block.DeepslateCakeBlock;
import org.kaleidoscope_sculk.register.ModBlocks;
import org.kaleidoscope_sculk.register.ModDamageTypes;

import java.util.List;

public class DeepslateCakeSliceItem extends Item {

    private static final float BITE_DAMAGE = 2.0f;
    private static final int USE_DURATION = 32;

    public DeepslateCakeSliceItem(Properties properties) {
        super(properties.food(new FoodProperties.Builder()
                .alwaysEdible()
                .nutrition(4)
                .saturationModifier(1.4f)
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 0), 1.0f)
                .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 2), 1.0f)
                .build()));
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.DEEPSLATE_BREAK;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        
        if (clickedState.getBlock() instanceof DeepslateCakeBlock) {
            return InteractionResult.PASS;
        }

        BlockPos placePos = clickedPos.relative(context.getClickedFace());
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockState placeState = level.getBlockState(placePos);
        if (!placeState.canBeReplaced() || !level.getBlockState(placePos.below()).isSolid()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            BlockState cakeState = ModBlocks.DEEPSLATE_CAKE.get()
                    .defaultBlockState()
                    .setValue(DeepslateCakeBlock.BITES, 3);
            level.setBlock(placePos, cakeState, 3);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            level.playSound(null, placePos, SoundEvents.DEEPSLATE_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.canEat(true)) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        ItemStack result = super.finishUsingItem(stack, level, living);

        if (!level.isClientSide && living instanceof Player player) {
            float newHealth = player.getHealth() - BITE_DAMAGE;
            if (newHealth <= 0.0f) {
                DamageSource damageSource = level.damageSources()
                        .source(ModDamageTypes.DEEPSLATE_CAKE_SLICE, player);
                player.hurt(damageSource, BITE_DAMAGE);
            } else {
                player.setHealth(newHealth);
                player.playSound(SoundEvents.PLAYER_HURT, 1.0f, 1.0f);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.DEEPSLATE_BREAK, SoundSource.PLAYERS, 1.0f, 1.5f);
        }

        return result;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.kaleidoscope_sculk.deepslate_cake_slice.tooltip")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.kaleidoscope_sculk.deepslate_cake_slice.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.kaleidoscope_sculk.deepslate_cake_slice.damage")
                .withStyle(ChatFormatting.RED));
    }
}
