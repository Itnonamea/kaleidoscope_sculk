package org.kaleidoscope_sculk.handler;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModEffects;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class SculkDashCleanupHandler {

    private static final String SPEED_MODIFIER_ID = "sculk_dash_speed";

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect().is(ModEffects.SCULK_DASH.getDelegate())) {
            removeSpeedModifier(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance() != null &&
                event.getEffectInstance().getEffect().is(ModEffects.SCULK_DASH.getDelegate())) {
            removeSpeedModifier(event.getEntity());
        }
    }

    private static void removeSpeedModifier(LivingEntity entity) {
        if (entity == null) return;
        AttributeInstance attributeInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeInstance != null) {
            attributeInstance.removeModifier(
                    ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, SPEED_MODIFIER_ID)
            );
        }
    }
}