package org.kaleidoscope_sculk.handler;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModEffects;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class EchoEffectHandler {

    private static Holder<MobEffect> echoCache;

    private static Holder<MobEffect> echo() {
        Holder<MobEffect> holder = echoCache;
        if (holder == null) {
            holder = ModEffects.ECHO.getDelegate();
            echoCache = holder;
        }
        return holder;
    }

    @SubscribeEvent
    public static void onEchoBearerDealsDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;

        var source = event.getSource();
        LivingEntity attacker = null;
        if (source.getEntity() instanceof LivingEntity living) {
            attacker = living;
        } else if (source.getDirectEntity() instanceof LivingEntity living) {
            attacker = living;
        }
        if (attacker == null) return;
        if (!attacker.hasEffect(echo())) return;

        attacker.removeEffect(echo());
    }
}
