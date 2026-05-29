package org.kaleidoscope_sculk.mixin;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.Holder;
import org.kaleidoscope_sculk.register.ModPotions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PotionContents.class)
public class PotionContentsMixin {

    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void getPotionColor(CallbackInfoReturnable<Integer> cir) {
        PotionContents self = (PotionContents) (Object) this;
        Optional<Holder<Potion>> potionOpt = self.potion();

        if (potionOpt.isPresent() && potionOpt.get().is(ModPotions.ABYSS_POTION.getKey())) {
            cir.setReturnValue(0x1B0C36);  // 深邃药水颜色（深紫色）
        }
    }
}