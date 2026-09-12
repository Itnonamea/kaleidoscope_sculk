package org.kaleidoscope_sculk.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.kaleidoscope_sculk.register.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobEchoTargetMixin {

    @Unique
    private static Holder<MobEffect> echoCache;

    @Unique
    private static Holder<MobEffect> echo() {
        Holder<MobEffect> holder = echoCache;
        if (holder == null) {
            holder = ModEffects.ECHO.getDelegate();
            echoCache = holder;
        }
        return holder;
    }

    @Inject(method = "getTarget", at = @At("RETURN"), cancellable = true)
    private void kaleidoscope_sculk$hideEchoHolderFromTargeting(CallbackInfoReturnable<LivingEntity> cir) {
        LivingEntity target = cir.getReturnValue();
        if (target == null) return;
        if (target.hasEffect(echo())) {
            cir.setReturnValue(null);
        }
    }
}
