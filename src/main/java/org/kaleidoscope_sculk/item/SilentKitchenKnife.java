package org.kaleidoscope_sculk.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.kaleidoscope_sculk.component.SilentKnifeData;
import org.kaleidoscope_sculk.register.ModDataComponents;
import org.kaleidoscope_sculk.register.ModEffects;

import java.util.List;

public class SilentKitchenKnife extends SwordItem {

    // 基础攻击伤害和攻速
    private static final float ATTACK_DAMAGE = 5.0f;  // 总伤害 = 工具基础伤害 + 此值
    private static final float ATTACK_SPEED = -2.2f;

    public SilentKitchenKnife() {
        super(Tiers.DIAMOND, new Properties()
                .durability(1800)
                .attributes(SwordItem.createAttributes(Tiers.DIAMOND, (int) ATTACK_DAMAGE, ATTACK_SPEED))
                .component(ModDataComponents.SILENT_KNIFE_DATA.get(), SilentKnifeData.DEFAULT)
        );
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            if (isFullyCharged(player)) {
                addChargeCount(stack, player);
            }
        }

        return result;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        boolean result = super.mineBlock(stack, level, state, pos, miner);

        if (!level.isClientSide && miner instanceof Player player) {
            if (isFullyCharged(player)) {
                addChargeCount(stack, player);
            }
        }

        return result;
    }

    private boolean isFullyCharged(Player player) {
        float attackStrength = player.getAttackStrengthScale(0.5f);
        return attackStrength >= 0.95f;
    }

    private void addChargeCount(ItemStack stack, Player player) {
        SilentKnifeData data = stack.get(ModDataComponents.SILENT_KNIFE_DATA.get());
        if (data == null) data = SilentKnifeData.DEFAULT;

        boolean shouldGiveBuff = data.canGiveBuff();
        SilentKnifeData newData = data.increment();
        stack.set(ModDataComponents.SILENT_KNIFE_DATA.get(), newData);

        if (shouldGiveBuff && player.level() instanceof ServerLevel) {
            player.addEffect(new MobEffectInstance(
                    ModEffects.SONIC_WAVE.getDelegate(),
                    3600,
                    0
            ));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP,
                    net.minecraft.sounds.SoundSource.PLAYERS, 1.5f, 1.0f);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        SilentKnifeData data = stack.get(ModDataComponents.SILENT_KNIFE_DATA.get());
        if (data == null) data = SilentKnifeData.DEFAULT;

        int progress = data.getProgress();
        tooltip.add(Component.translatable("item.kaleidoscope_sculk.silent_knife.charge", progress, 20)
                .withStyle(net.minecraft.ChatFormatting.DARK_PURPLE));

        StringBuilder bar = new StringBuilder("§7[");
        for (int i = 0; i < 20; i++) {
            if (i < progress) {
                bar.append("§5█");
            } else {
                bar.append("§8░");
            }
        }
        bar.append("§7]");
        tooltip.add(Component.literal(bar.toString()));

        if (progress == 19) {
            tooltip.add(Component.translatable("item.kaleidoscope_sculk.silent_knife.next_buff")
                    .withStyle(net.minecraft.ChatFormatting.GOLD));
        }
    }
}