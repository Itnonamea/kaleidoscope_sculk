// kaleidoscope_sculk/effect/AbyssEffect.java
package org.kaleidoscope_sculk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class AbyssEffect extends MobEffect {

    public AbyssEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x1B0C36); // 深紫色
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}