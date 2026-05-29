package org.kaleidoscope_sculk.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import org.kaleidoscope_sculk.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.warden.Warden$VibrationUser")
public class WardenVibrationUserMixin {

    @Inject(method = "canReceiveVibration", at = @At("HEAD"), cancellable = true)
    private void onCanReceiveVibration(ServerLevel level, BlockPos pos, Holder<GameEvent> gameEvent,
                                       Context context, CallbackInfoReturnable<Boolean> cir) {
        // 检查事件来源实体
        if (context.sourceEntity() instanceof LivingEntity livingEntity) {
            // 如果来源实体有 Echo 效果，坚守者不接收振动
            if (livingEntity.hasEffect(ModEffects.ECHO.getDelegate())) {
                cir.setReturnValue(false);
            }
        }
    }
}