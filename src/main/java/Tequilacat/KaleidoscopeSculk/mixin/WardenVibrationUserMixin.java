package Tequilacat.KaleidoscopeSculk.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import Tequilacat.KaleidoscopeSculk.register.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.warden.Warden$VibrationUser")
public class WardenVibrationUserMixin {

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

    @Inject(method = "canReceiveVibration", at = @At("HEAD"), cancellable = true)
    private void onCanReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> gameEvent,
                                       Context context, CallbackInfoReturnable<Boolean> cir) {
        if (!(context.sourceEntity() instanceof LivingEntity livingEntity)) return;
        if (livingEntity.hasEffect(echo())) {
            cir.setReturnValue(false);
        }
    }
}
