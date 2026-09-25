package Tequilacat.KaleidoscopeSculk.register;

import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopetavern.fluid.JuiceFluidType;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import Tequilacat.KaleidoscopeSculk.Kaleidoscope_sculk;
import Tequilacat.KaleidoscopeSculk.entity.AncientBoneFragmentProjectile;

public class ModRegistries {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Kaleidoscope_sculk.MODID);

    public static final DeferredHolder<FluidType, JuiceFluidType> SCULK_DUST_JUICE_TYPE =
            FLUID_TYPES.register("sculk_juice", () -> new JuiceFluidType(
                    ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sculk_juice"), 0));

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, Kaleidoscope_sculk.MODID);

    public static final DeferredHolder<Fluid, FlowingFluid> SCULK_JUICE =
            FLUIDS.register("sculk_juice", () -> new BaseFlowingFluid.Source(makeFluidProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> SCULK_DUST_JUICE_FLOWING =
            FLUIDS.register("sculk_juice_flowing", () -> new BaseFlowingFluid.Flowing(makeFluidProperties()));

    private static BaseFlowingFluid.Properties makeFluidProperties() {
        return new BaseFlowingFluid.Properties(SCULK_DUST_JUICE_TYPE, SCULK_JUICE, SCULK_DUST_JUICE_FLOWING)
                .bucket(() -> ModItems.SCULK_JUICE_BUCKET.get());
    }

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Kaleidoscope_sculk.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<AncientBoneFragmentProjectile>>
            ANCIENT_BONE_FRAGMENT_PROJECTILE = ENTITY_TYPES.register(
            "ancient_bone_fragment_projectile",
            () -> EntityType.Builder.<AncientBoneFragmentProjectile>of(
                            AncientBoneFragmentProjectile::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(64)
                    .updateInterval(10)
                    .build("ancient_bone_fragment_projectile"));

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(BuiltInRegistries.POTION, Kaleidoscope_sculk.MODID);

    public static final DeferredHolder<Potion, Potion> ABYSS_POTION = POTIONS.register(
            "abyss_potion",
            () -> new Potion(new MobEffectInstance(ModEffects.ABYSS.getDelegate(), 1800, 0)));

    /** Abyss potion color, used by PotionContentsMixin. */
    public static final int ABYSS_POTION_COLOR = 0x1B0C36;

    public static final DeferredRegister<DamageType> DAMAGE_TYPES =
            DeferredRegister.create(Registries.DAMAGE_TYPE, Kaleidoscope_sculk.MODID);

    public static final ResourceKey<DamageType> DEEPSLATE_CAKE_SLICE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "deepslate_cake_slice"));

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Kaleidoscope_sculk.MODID);

    /** Soul Sail reaping cooldown end time (game ticks); the cooldown is bound to the item itself. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> SOUL_SAIL_COOLDOWN =
            DATA_COMPONENTS.register("soul_sail_cooldown",
                    () -> DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .networkSynchronized(ByteBufCodecs.VAR_LONG)
                            .build());

    private static final int CUPS_PER_BLOCK = 4;
    private static final int ECHO_PUER_ECHO_DURATION = 20 * 60 * 8;

    public static final ResourceLocation ECHO_PUER =
            ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "echo_puer");

    private static boolean teasRegistered;

    public static void registerTeas() {
        if (teasRegistered) {
            return;
        }
        teasRegistered = true;

        new TeacupRegistry().registerTeacupData(ECHO_PUER,
                TeacupRegistry.TeacupData.create(CUPS_PER_BLOCK)
                        .addEffect(() -> new MobEffectInstance(
                                ModEffects.ECHO.getDelegate(), ECHO_PUER_ECHO_DURATION, 0)));
    }
}
