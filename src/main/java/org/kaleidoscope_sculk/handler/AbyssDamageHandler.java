// kaleidoscope_sculk/handler/AbyssDamageHandler.java
package org.kaleidoscope_sculk.handler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModEffects;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class AbyssDamageHandler {

    // 基准高度（海平面）
    private static final int SEA_LEVEL = 64;
    // 最大深度（y=-64以下不再增加）
    private static final int MAX_DEPTH = -64;
    // 最大倍率 (300%)
    private static final float MAX_MULTIPLIER = 4.0f;

    /**
     * 计算当前深度的伤害倍率
     * @param y 玩家Y坐标
     * @return 伤害倍率 (1.0 ~ 4.0)
     */
    private static float calculateDamageMultiplier(double y) {
        if (y >= SEA_LEVEL) {
            return 1.0f;
        }

        // 计算深度 (海平面以下为正数)
        double depth = SEA_LEVEL - y;
        // 最大深度范围
        double maxDepthRange = SEA_LEVEL - MAX_DEPTH; // 64 - (-64) = 128

        // 限制最大深度
        double clampedDepth = Math.min(depth, maxDepthRange);
        // 计算倍率: 1.0 + (depth / maxDepthRange) * 3.0
        float multiplier = 1.0f + (float) (clampedDepth / maxDepthRange) * (MAX_MULTIPLIER - 1.0f);

        return Math.min(MAX_MULTIPLIER, Math.max(1.0f, multiplier));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 获取攻击者
        net.minecraft.world.damagesource.DamageSource source = event.getSource();
        LivingEntity attacker = null;

        // 获取真正的攻击者
        if (source.getEntity() instanceof LivingEntity living) {
            attacker = living;
        } else if (source.getDirectEntity() instanceof LivingEntity living) {
            attacker = living;
        }

        // 如果攻击者不是玩家或没有深邃效果，直接返回
        if (!(attacker instanceof Player player)) return;
        if (!player.hasEffect(ModEffects.ABYSS.getDelegate())) return;

        // 获取当前深度倍率
        float multiplier = calculateDamageMultiplier(player.getY());

        // 如果倍率大于1，增加伤害
        if (multiplier > 1.0f) {
            float originalDamage = event.getAmount();
            float newDamage = originalDamage * multiplier;
            event.setAmount(newDamage);
        }
    }
}