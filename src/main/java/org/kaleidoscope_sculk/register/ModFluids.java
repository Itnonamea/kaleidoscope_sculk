package org.kaleidoscope_sculk.register;

import com.github.ysbbbbbb.kaleidoscopetavern.fluid.JuiceFluidType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Kaleidoscope_sculk.MODID);

    public static final DeferredHolder<FluidType, JuiceFluidType> SCULK_DUST_JUICE_TYPE =
            FLUID_TYPES.register("sculk_juice",
                    () -> new JuiceFluidType(
                            ResourceLocation.parse(Kaleidoscope_sculk.MODID + ":sculk_juice"),
                            0
                    )
            );

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, Kaleidoscope_sculk.MODID);

    // 注意：这里使用 DeferredHolder，需要先声明再使用
    public static final DeferredHolder<Fluid, FlowingFluid> SCULK_JUICE =
            FLUIDS.register("sculk_juice",
                    () -> new BaseFlowingFluid.Source(makeProperties())
            );

    public static final DeferredHolder<Fluid, FlowingFluid> SCULK_DUST_JUICE_FLOWING =
            FLUIDS.register("sculk_juice_flowing",
                    () -> new BaseFlowingFluid.Flowing(makeProperties())
            );

    private static BaseFlowingFluid.Properties makeProperties() {
        return new BaseFlowingFluid.Properties(
                SCULK_DUST_JUICE_TYPE,
                SCULK_JUICE,
                SCULK_DUST_JUICE_FLOWING
        )
                .bucket(() -> ModItems.SCULK_JUICE_BUCKET.get());  // ← 关联桶
    }
}