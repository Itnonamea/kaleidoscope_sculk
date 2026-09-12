package org.kaleidoscope_sculk.register;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.WARMTH;
import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR;
import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.MUSTARD;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.DrinkBlock;
import org.kaleidoscope_sculk.block.DeepslateCakeBlock;
import org.kaleidoscope_sculk.block.DeepslateStoveBlock;
import org.kaleidoscope_sculk.block.EchoCropBlock;
import org.kaleidoscope_sculk.block.EchoCropTopBlock;
import org.kaleidoscope_sculk.block.FoodBiteBlock;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, Kaleidoscope_sculk.MODID);

    
    public static final int MUSTARD_DURATION = 6000; 

    public static final VoxelShape SHAPE_MEDIUM = Block.box(4.0, 0.0, 4.0, 12.0, 7.0, 12.0);
    public static final VoxelShape PLATE = Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0);
    public static final VoxelShape SHAPE_MEDIUM_WITH_MAT = Shapes.or(
            Block.box(4.0, 0.0, 4.0, 12.0, 7.0, 12.0),
            Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0)
    );

    public static final VoxelShape SHAPE_PORK_RIBS = Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);

    public static final VoxelShape SHAPE_PORK_RIBS_PLATE = Shapes.or(
            Block.box(3.0, 0.0, 3.0, 13.0, 6.0, 13.0),
            Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0)
    );

    
    public static final DeferredHolder<Block, DrinkBlock> SCULK_BREW_BOTTLE =
            BLOCKS.register("sculk_brew_bottle", () -> new DrinkBlock(
                    4,
                    Block.box(5, 0, 5, 11, 10, 11),
                    Block.box(5, 0, 5, 11, 10, 11),
                    Block.box(5, 0, 5, 11, 10, 11),
                    Block.box(5, 0, 5, 11, 10, 11)
            ));

    public static final DeferredHolder<Block, DrinkBlock> HUADIAO_WINE =
            BLOCKS.register("huadiao_wine", () -> new DrinkBlock(
                    3,
                    Block.box(4, 0, 4, 12, 12, 12),
                    Block.box(2, 0, 2, 14, 12, 14),
                    Block.box(1, 0, 1, 15, 12, 15)
            ));

    public static final DeferredHolder<Block, DrinkBlock> HONGLAN_WINE =
            BLOCKS.register("honglan_wine", () -> new DrinkBlock(
                    3,
                    Block.box(3, 0, 3, 13, 14, 13),
                    Block.box(2, 0, 2, 14, 14, 14),
                    Block.box(1, 0, 1, 15, 14, 15)
            ));

    
    public static final DeferredHolder<Block, DeepslateCakeBlock> DEEPSLATE_CAKE =
            BLOCKS.register("deepslate_cake", () -> new DeepslateCakeBlock(
                    Block.Properties.of()
                            .sound(SoundType.DEEPSLATE)
                            .strength(0.5f)
                            .noOcclusion()
                            .instabreak()
            ));

    
    public static final DeferredHolder<Block, DeepslateStoveBlock> DEEPSLATE_STOVE =
            BLOCKS.register("deepslate_stove", () -> new DeepslateStoveBlock());

    public static final DeferredHolder<Block, FoodBiteBlock> ANCIENT_CITY_STYLE_SASHIMI_BLOCK =
            BLOCKS.register("ancient_city_style_sashimi", () -> new FoodBiteBlock(
                    new FoodProperties.Builder()
                            .alwaysEdible()
                            .nutrition(4)
                            .saturationModifier(1.2f)
                            .effect(ModBlocks::createMustardEffect, 1.0f)
                            .effect(() -> new MobEffectInstance(ModEffects.ECHO.getDelegate(), 3600, 0), 1.0f)
                            .build(),
                    4,
                    PLATE,
                    Items.BOWL
            ));

    public static final DeferredHolder<Block, FoodBiteBlock> SCULK_STEW_BLOCK =
            BLOCKS.register("sculk_stew_block", () -> new FoodBiteBlock(
                    new FoodProperties.Builder()
                            .alwaysEdible()
                            .nutrition(4)
                            .saturationModifier(1.0f)
                            .effect(() -> new MobEffectInstance(WARMTH.getDelegate(), 6000, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(ModEffects.ECHO.getDelegate(), 2400, 0), 1.0f)
                            .build(),
                    3,
                    SHAPE_MEDIUM_WITH_MAT,
                    Items.FLOWER_POT
            ));

    public static final DeferredHolder<Block, FoodBiteBlock> SCULK_CHICKEN_STEW_BLOCK =
            BLOCKS.register("sculk_chicken_stew_block", () -> new FoodBiteBlock(
                    new FoodProperties.Builder()
                            .alwaysEdible()
                            .nutrition(5)
                            .saturationModifier(1.3f)
                            .effect(() -> new MobEffectInstance(WARMTH.getDelegate(), 2400, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(ModEffects.ECHO.getDelegate(), 3600, 0), 1.0f)
                            .build(),
                    3,
                    SHAPE_MEDIUM_WITH_MAT,
                    Items.FLOWER_POT
            ));

    public static final DeferredHolder<Block, FoodBiteBlock> SCULK_LAMB_CHOP_BLOCK =
            BLOCKS.register("sculk_lamb_chop_block", () -> new FoodBiteBlock(
                    new FoodProperties.Builder()
                            .alwaysEdible()
                            .nutrition(4)
                            .saturationModifier(1.2f)
                            .effect(() -> new MobEffectInstance(ModEffects.SCULK_DASH.getDelegate(), 6000, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(ModEffects.SONIC_WAVE.getDelegate(), 2400, 2), 1.0f)
                            .build(),
                    3,
                    PLATE,
                    Items.BOWL
            ));

    public static final DeferredHolder<Block, FoodBiteBlock> SCULK_PORK_RIBS_BLOCK =
            BLOCKS.register("sculk_pork_ribs_block", () -> new FoodBiteBlock(
                    new FoodProperties.Builder()
                            .alwaysEdible()
                            .nutrition(4)
                            .saturationModifier(1.1f)
                            .effect(() -> new MobEffectInstance(ModEffects.SONIC_WAVE.getDelegate(), 3600, 4), 1.0f)
                            .build(),
                    4,
                    PLATE,
                    Items.BOWL
            ));

    public static final DeferredHolder<Block, EchoCropBlock> ECHO_CROP =
            BLOCKS.register("echo_crop", () -> new EchoCropBlock(
                    Block.Properties.of()
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .noOcclusion()
            ));

    public static final DeferredHolder<Block, EchoCropTopBlock> ECHO_CROP_TOP =
            BLOCKS.register("echo_crop_top", () -> new EchoCropTopBlock(
                    Block.Properties.of()
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .noOcclusion()
            ));

    
    public static Holder<MobEffect> getMustardEffect() {
        return MUSTARD.getDelegate();
    }

    
    public static MobEffectInstance createMustardEffect() {
        return new MobEffectInstance(MUSTARD.getDelegate(), MUSTARD_DURATION, 0);
    }
}
