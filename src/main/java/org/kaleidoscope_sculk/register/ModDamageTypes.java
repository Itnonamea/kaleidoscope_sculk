// java/org/kaleidoscope_sculk/register/ModDamageTypes.java
package org.kaleidoscope_sculk.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;

public class ModDamageTypes {

    public static final DeferredRegister<DamageType> DAMAGE_TYPES =
            DeferredRegister.create(Registries.DAMAGE_TYPE, Kaleidoscope_sculk.MODID);

    public static final ResourceKey<DamageType> DEEPSLATE_CAKE_SLICE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "deepslate_cake_slice")
    );
}