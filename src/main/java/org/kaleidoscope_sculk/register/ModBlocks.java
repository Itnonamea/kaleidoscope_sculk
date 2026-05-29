package org.kaleidoscope_sculk.register;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.block.*;

import static org.kaleidoscope_sculk.register.ModItems.ITEMS;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, Kaleidoscope_sculk.MODID);

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

    // 灶台
    public static final DeferredHolder<Block, DeepslateStoveBlock> DEEPSLATE_STOVE =
            BLOCKS.register("deepslate_stove", () -> new DeepslateStoveBlock());

    public static final DeferredHolder<Block, FoodBiteBlock> ANCIENT_CITY_STYLE_SASHIMI_BLOCK =
            BLOCKS.register("ancient_city_style_sashimi", () -> {
                Holder<MobEffect> mustard = getEffectHolder("kaleidoscope_cookery", "mustard");
                return new FoodBiteBlock(
                        new FoodProperties.Builder()
                                .alwaysEdible()
                                .nutrition(2)
                                .saturationModifier(0.75f)
                                .build(),
                        4,
                        PLATE,
                        () -> Items.RED_CANDLE,  // 返还红色蜡烛
                        () -> mustard != null ? new MobEffectInstance(mustard, 6000, 0) : null,  // 芥末效果
                        1.0f,
                        Block.Properties.of()
                                .strength(0.5f)
                                .noOcclusion()
                                .instabreak()
                );
            });

    public static final DeferredHolder<Block, FoodBiteBlock> SCULK_STEW_BLOCK =
            BLOCKS.register("sculk_stew_block", () -> {
                Holder<MobEffect> warmth = getEffectHolder("kaleidoscope_cookery", "warmth");
                return new FoodBiteBlock(
                        new FoodProperties.Builder()
                                .alwaysEdible()
                                .nutrition(6)
                                .saturationModifier(0.2f)
                                .build(),
                        3,
                        SHAPE_MEDIUM_WITH_MAT,
                        () -> warmth != null ? new MobEffectInstance(warmth, 6000, 0) : null,
                        1.0f,
                        Block.Properties.of()
                                .strength(0.5f)
                                .noOcclusion()
                                .instabreak()
                );
            });

    public static final DeferredHolder<Block, FoodBiteBlock> SCULK_CHICKEN_STEW_BLOCK =
            BLOCKS.register("sculk_chicken_stew_block", () -> {
                Holder<MobEffect> warmth = getEffectHolder("kaleidoscope_cookery", "warmth");
                return new FoodBiteBlock(
                        new FoodProperties.Builder()
                                .alwaysEdible()
                                .nutrition(6)
                                .saturationModifier(1.2f)
                                .build(),
                        3,
                        SHAPE_MEDIUM_WITH_MAT,
                        () -> warmth != null ? new MobEffectInstance(warmth, 2400, 0) : null,
                        1.0f,
                        Block.Properties.of()
                                .strength(0.5f)
                                .noOcclusion()
                                .instabreak()
                );
            });

    public static final DeferredHolder<Block, FoodBiteBlock> SCULK_LAMB_CHOP_BLOCK =
            BLOCKS.register("sculk_lamb_chop_block", () -> new FoodBiteBlock(
                    new FoodProperties.Builder()
                            .alwaysEdible()
                            .nutrition(13)
                            .saturationModifier(2.3077f)
                            .build(),
                    3,
                    PLATE,
                    () -> Items.BOWL,
                    () -> new MobEffectInstance(ModEffects.SCULK_DASH.getDelegate(), 6000, 0),  // 幽匿疾行效果
                    1.0f,
                    Block.Properties.of()
                            .strength(0.5f)
                            .noOcclusion()
                            .instabreak()
            ));

    public static final DeferredHolder<Block, FoodBiteBlock> SCULK_PORK_RIBS_BLOCK =
            BLOCKS.register("sculk_pork_ribs_block", () -> new FoodBiteBlock(
                    new FoodProperties.Builder()
                            .alwaysEdible()
                            .nutrition(4)
                            .saturationModifier(0.4f)
                            .build(),
                    4,
                    PLATE,
                    () -> ModItems.ANCIENT_BONE_FRAGMENT.get(),
                    Block.Properties.of()
                            .strength(0.5f)
                            .noOcclusion()
                            .instabreak()
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



    private static Holder<MobEffect> getEffectHolder(String modId, String effectName) {
        ResourceLocation effectId = ResourceLocation.fromNamespaceAndPath(modId, effectName);
        ResourceKey<MobEffect> effectKey = ResourceKey.create(Registries.MOB_EFFECT, effectId);
        return BuiltInRegistries.MOB_EFFECT.getHolder(effectKey).orElse(null);
    }
}