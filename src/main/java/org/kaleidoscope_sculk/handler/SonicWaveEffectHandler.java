package org.kaleidoscope_sculk.handler;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModEffects;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class SonicWaveEffectHandler {

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        MobEffectInstance newEffect = event.getEffectInstance();

        if (newEffect == null || !newEffect.getEffect().is(ModEffects.SONIC_WAVE.getDelegate())) {
            return;
        }

        var entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.level().isClientSide) return;

        // 获取现有声波效果
        MobEffectInstance existingEffect = player.getEffect(ModEffects.SONIC_WAVE.getDelegate());

        if (existingEffect != null) {
            // 等级直接相加
            int totalAmplifier = existingEffect.getAmplifier() + newEffect.getAmplifier() + 1;
            int totalDuration = existingEffect.getDuration() + newEffect.getDuration();

            // 限制最大等级
            totalAmplifier = Math.min(totalAmplifier, 20);

            // 阻止原始效果添加
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);

            // 移除旧效果
            player.removeEffect(ModEffects.SONIC_WAVE.getDelegate());

            // 添加叠加后的效果
            player.addEffect(new MobEffectInstance(
                    ModEffects.SONIC_WAVE.getDelegate(),
                    totalDuration,
                    totalAmplifier
            ));
        }


    }
}