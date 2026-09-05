// java/org/kaleidoscope_sculk/item/DeepslateCakeSliceItem.java
package org.kaleidoscope_sculk.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.kaleidoscope_sculk.block.DeepslateCakeBlock;
import org.kaleidoscope_sculk.register.ModBlocks;
import org.kaleidoscope_sculk.register.ModDamageTypes;

import java.util.List;

public class DeepslateCakeSliceItem extends Item {

    public DeepslateCakeSliceItem(Properties properties) {
        super(properties.food(new FoodProperties.Builder()
                .alwaysEdible()
                .nutrition(4)
                .saturationModifier(0.3f)
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 0), 1.0f)
                .build()));
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

        if (!level.getBlockState(placePos).canBeReplaced()) {
            return InteractionResult.PASS;
        }

        if (!level.getBlockState(placePos.below()).isSolid()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            BlockState cakeState = ModBlocks.DEEPSLATE_CAKE.get().defaultBlockState()
                    .setValue(DeepslateCakeBlock.BITES, DeepslateCakeBlock.MAX_BITES - 1);

            level.setBlock(placePos, cakeState, Block.UPDATE_ALL);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            level.playSound(null, placePos,
                    SoundEvents.DEEPSLATE_PLACE,
                    SoundSource.BLOCKS,
                    1.0F, 1.0F);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.canEat(true)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (living instanceof Player player) {
            // 创建自定义伤害源
            DamageSource damageSource = new DamageSource(
                    level.registryAccess()
                            .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(ModDamageTypes.DEEPSLATE_CAKE_SLICE),
                    player
            );

            // 扣除2点血量
            float currentHealth = player.getHealth();
            float newHealth = currentHealth - 2.0f;

            if (newHealth <= 0) {
                // 玩家死亡
                player.hurt(damageSource, 2.0f);
            } else {
                // 直接设置血量
                player.setHealth(newHealth);
                // 播放受伤音效
                player.playSound(SoundEvents.PLAYER_HURT, 1.0F, 1.0F);
            }

            // 应用食物效果（生命恢复）
            super.finishUsingItem(stack, level, living);

            // 播放吃深板岩的音效
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.DEEPSLATE_BREAK,
                    SoundSource.PLAYERS,
                    1.0F, 1.5F);
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.kaleidoscope_sculk.deepslate_cake_slice.tooltip")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.kaleidoscope_sculk.deepslate_cake_slice.effect")
                .withStyle(net.minecraft.ChatFormatting.BLUE));
        tooltip.add(Component.translatable("item.kaleidoscope_sculk.deepslate_cake_slice.damage")
                .withStyle(net.minecraft.ChatFormatting.RED));
    }
}