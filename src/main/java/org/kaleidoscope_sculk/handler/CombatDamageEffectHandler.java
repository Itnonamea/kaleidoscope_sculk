package org.kaleidoscope_sculk.handler;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModEffects;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class CombatDamageEffectHandler {

    private static Holder<MobEffect> abyssCache;
    private static Holder<MobEffect> echoCache;

    private static Holder<MobEffect> abyss() {
        Holder<MobEffect> holder = abyssCache;
        if (holder == null) {
            holder = ModEffects.ABYSS.getDelegate();
            abyssCache = holder;
        }
        return holder;
    }

    private static Holder<MobEffect> echo() {
        Holder<MobEffect> holder = echoCache;
        if (holder == null) {
            holder = ModEffects.ECHO.getDelegate();
            echoCache = holder;
        }
        return holder;
    }

    private static final int START_DEPTH = 0;
    private static final int MAX_DEPTH = -60;
    private static final float BASE_MAX_BONUS_DAMAGE = 5.0f;
    private static final float BONUS_PER_LEVEL = 3.0f;
    private static final double DEPTH_SCALE = 15.0;
    private static final double MAX_DEPTH_RANGE = START_DEPTH - MAX_DEPTH;

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;

        LivingEntity attacker = resolveAttacker(event.getSource());
        if (attacker == null) return;

        if (attacker instanceof Player player) {
            MobEffectInstance abyss = player.getEffect(abyss());
            if (abyss != null) {
                float bonusDamage = calculateAbyssBonus(player.getY(), abyss.getAmplifier());
                if (bonusDamage > 0.0f) {
                    event.setAmount(event.getAmount() + bonusDamage);
                }
            }
        }

        if (attacker.hasEffect(echo())) {
            attacker.removeEffect(echo());
        }
    }

    private static LivingEntity resolveAttacker(DamageSource source) {
        if (source.getEntity() instanceof LivingEntity living) {
            return living;
        }
        return source.getDirectEntity() instanceof LivingEntity living ? living : null;
    }

    private static float calculateAbyssBonus(double y, int amplifier) {
        if (y >= START_DEPTH) {
            return 0.0f;
        }

        double depth = START_DEPTH - y;
        double clampedDepth = Math.min(depth, MAX_DEPTH_RANGE);
        double ratio = clampedDepth / (clampedDepth + DEPTH_SCALE);

        float maxBonus = BASE_MAX_BONUS_DAMAGE + (amplifier * BONUS_PER_LEVEL);
        float bonusDamage = (float) (ratio * maxBonus);

        return Math.min(maxBonus, Math.max(0.0f, bonusDamage));
    }
}
