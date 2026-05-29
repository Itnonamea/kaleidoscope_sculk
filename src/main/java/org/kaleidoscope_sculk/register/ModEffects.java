package org.kaleidoscope_sculk;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.kaleidoscope_sculk.effect.EchoEffect;
import org.kaleidoscope_sculk.effect.SculkEffect;
import org.kaleidoscope_sculk.effect.SonicWaveEffect;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Kaleidoscope_sculk.MODID);

    public static final DeferredHolder<MobEffect, SonicWaveEffect> SONIC_WAVE =
            MOB_EFFECTS.register("sonic_wave", SonicWaveEffect::new);

    public static final DeferredHolder<MobEffect, SculkEffect> SCULK =
            MOB_EFFECTS.register("sculk", SculkEffect::new);

    public static final DeferredHolder<MobEffect, EchoEffect> ECHO =
            MOB_EFFECTS.register("echo", EchoEffect::new);
}