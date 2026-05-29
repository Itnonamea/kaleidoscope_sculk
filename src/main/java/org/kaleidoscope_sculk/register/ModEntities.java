package org.kaleidoscope_sculk.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.entity.AncientBoneFragmentProjectile;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Kaleidoscope_sculk.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<AncientBoneFragmentProjectile>> ANCIENT_BONE_FRAGMENT_PROJECTILE =
            ENTITY_TYPES.register("ancient_bone_fragment_projectile",
                    () -> EntityType.Builder.<AncientBoneFragmentProjectile>of(
                                    AncientBoneFragmentProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(64)
                            .updateInterval(10)
                            .build("ancient_bone_fragment_projectile"));
}