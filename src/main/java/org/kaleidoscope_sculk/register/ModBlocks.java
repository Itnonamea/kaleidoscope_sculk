package org.kaleidoscope_sculk;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.kaleidoscope_sculk.block.EchoCropBlock;
import org.kaleidoscope_sculk.block.SculkStewBlock;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, Kaleidoscope_sculk.MODID);

    public static final VoxelShape SHAPE_MEDIUM = Block.box(4.0, 0.0, 4.0, 12.0, 7.0, 12.0);

    public static final VoxelShape SHAPE_MEDIUM_WITH_MAT = Shapes.or(
            Block.box(4.0, 0.0, 4.0, 12.0, 7.0, 12.0),
            Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0)
    );

    public static final DeferredHolder<Block, SculkStewBlock> SCULK_STEW_BLOCK =
            BLOCKS.register("sculk_stew_block", () -> {

                ResourceLocation warmthId = ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "warmth");
                ResourceKey<MobEffect> warmthKey = ResourceKey.create(Registries.MOB_EFFECT, warmthId);
                Holder<MobEffect> warmthHolder = BuiltInRegistries.MOB_EFFECT.getHolder(warmthKey).orElse(null);

                return new SculkStewBlock(
                        new FoodProperties.Builder()
                                .nutrition(6)
                                .saturationModifier(0.2f)
                                .build(),
                        3,
                        SHAPE_MEDIUM_WITH_MAT,
                        warmthHolder != null ? () -> new MobEffectInstance(warmthHolder, 200, 0) : null,
                        1.0f,
                        Block.Properties.of()
                                .strength(0.5f)
                                .noOcclusion()
                                .instabreak()
                );
            });

    // 回声作物
    public static final DeferredHolder<Block, EchoCropBlock> ECHO_CROP =
            BLOCKS.register("echo_crop", () -> new EchoCropBlock(
                    Block.Properties.of()
                            .noCollission()
                            .randomTicks()  // 必须添加这个
                            .instabreak()
                            .noOcclusion()
            ));
}