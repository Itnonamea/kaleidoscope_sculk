package org.kaleidoscope_sculk.register;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, Kaleidoscope_sculk.MODID);

    public static final DeferredHolder<Potion, Potion> ABYSS_POTION = POTIONS.register(
            "abyss_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.ABYSS.getDelegate(), 1800, 0))
    );

    // 深邃药水颜色
    public static final int ABYSS_POTION_COLOR = 0x1B0C36;  // 深邃效果的颜色
}