package org.kaleidoscope_sculk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SonicWaveEffect extends MobEffect {

    public SonicWaveEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x5B6E6A);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}