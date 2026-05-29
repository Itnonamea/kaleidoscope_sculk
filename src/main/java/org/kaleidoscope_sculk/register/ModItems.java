package org.kaleidoscope_sculk;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.kaleidoscope_sculk.item.EchoSeedItem;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Kaleidoscope_sculk.MODID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Kaleidoscope_sculk.MODID);

    // 方块物品
    public static final DeferredItem<BlockItem> SCULK_STEW_BLOCK_ITEM = ITEMS.registerItem(
            "sculk_stew_block",
            properties -> new BlockItem(ModBlocks.SCULK_STEW_BLOCK.get(), properties),
            new Item.Properties()
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> KALEIDOSCOPE_SCULK_TAB =
            CREATIVE_TABS.register("kaleidoscope_sculk_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + Kaleidoscope_sculk.MODID))
                    .icon(() -> new ItemStack(ModItems.COOKED_WARDEN_TENDRIL_BOWL.get()))
                    .displayItems((parameters, output) -> {
                        // 原料
                        output.accept(ModItems.WARDEN_TENDRIL.get());
                        output.accept(ModItems.ANCIENT_BONE_FRAGMENT.get());
                        output.accept(ModItems.SCULK_FUNGUS.get());
                        output.accept(ModItems.TARNISH_SHEER_PURITY.get());

                        // 食物
                        output.accept(ModItems.SCULK_FUNGUS_SOUP.get());
                        output.accept(ModItems.COOKED_WARDEN_TENDRIL_BOWL.get());

                        // 方块食物
                        output.accept(ModItems.SCULK_STEW_BLOCK_ITEM.get());

                        //作物
                        output.accept(ModItems.ECHO_SEED.get());
                    })
                    .build());

    //纯粹（很中二是不是）
    public static final DeferredItem<Item> TARNISH_SHEER_PURITY = ITEMS.registerItem(
            "tarnish_sheer_purity",
            Item::new,
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)  // 始终显示附魔光泽
    );
    //回响之种
    public static final DeferredItem<Item> ECHO_SEED = ITEMS.registerItem(
            "echo_seed",
            properties -> new EchoSeedItem(ModBlocks.ECHO_CROP.get(), properties),
            new Item.Properties()
    );

    // 坚守者触须
    public static final DeferredItem<Item> WARDEN_TENDRIL = ITEMS.registerItem(
            "warden_tendril",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(2)
                            .saturationModifier(0.2f)
                            .alwaysEdible()
                            .build())
    );

    // 煮纯粹
    public static final DeferredItem<Item> BOIL_SHEER_PURITY = ITEMS.registerItem(
            "boil_sheer_purity",
            properties -> new Item(properties) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
                    ItemStack result = super.finishUsingItem(stack, level, living);
                    if (living instanceof Player player && !player.getAbilities().instabuild) {
                        if (!player.getInventory().add(Items.BOWL.getDefaultInstance())) {
                            player.drop(Items.BOWL.getDefaultInstance(), false);
                        }
                    }
                    return result;
                }
            },
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationModifier(0.8f)
                            .alwaysEdible()
                            .effect(() -> new MobEffectInstance(ModEffects.ECHO.getDelegate(), 3600, 0), 1.0f)
                            .build())
    );

    // 炒坚守者触须
    public static final DeferredItem<Item> COOKED_WARDEN_TENDRIL_BOWL = ITEMS.registerItem(
            "cooked_warden_tendril_bowl",
            properties -> new Item(properties) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
                    ItemStack result = super.finishUsingItem(stack, level, living);
                    if (living instanceof Player player && !player.getAbilities().instabuild) {
                        if (!player.getInventory().add(Items.BOWL.getDefaultInstance())) {
                            player.drop(Items.BOWL.getDefaultInstance(), false);
                        }
                    }
                    return result;
                }
            },
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationModifier(0.8f)
                            .alwaysEdible()
                            .effect(() -> new MobEffectInstance(ModEffects.SONIC_WAVE.getDelegate(), 6000, 2), 1.0f)
                            .build())
    );

    // 幽匿真菌汤
    public static final DeferredItem<Item> SCULK_FUNGUS_SOUP = ITEMS.registerItem(
            "sculk_fungus_soup",
            properties -> new Item(properties) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
                    ItemStack result = super.finishUsingItem(stack, level, living);
                    if (living instanceof Player player && !player.getAbilities().instabuild) {
                        if (!player.getInventory().add(Items.BOWL.getDefaultInstance())) {
                            player.drop(Items.BOWL.getDefaultInstance(), false);
                        }
                    }
                    return result;
                }
            },
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(5)
                            .saturationModifier(0.2f)
                            .build())
    );

    // 远古骨碎片 - 防火 + 可投掷
    public static final DeferredItem<Item> ANCIENT_BONE_FRAGMENT = ITEMS.registerItem(
            "ancient_bone_fragment",
            properties -> new Item(properties) {
                @Override
                public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                    ItemStack stack = player.getItemInHand(hand);

                    if (!level.isClientSide) {
                        org.kaleidoscope_sculk.entity.AncientBoneFragmentProjectile projectile = new org.kaleidoscope_sculk.entity.AncientBoneFragmentProjectile(
                                level, player
                        );
                        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.5f, 1.0f);
                        level.addFreshEntity(projectile);
                    }

                    player.playSound(net.minecraft.sounds.SoundEvents.SNOWBALL_THROW, 0.5f, 0.8f);

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
                }
            },
            new Item.Properties()
                    .stacksTo(16)
                    .fireResistant()
    );

    // 幽匿真菌
    public static final DeferredItem<Item> SCULK_FUNGUS = ITEMS.registerItem(
            "sculk_fungus",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(1)
                            .saturationModifier(0.2f)
                            .effect(() -> new MobEffectInstance(ModEffects.SCULK.getDelegate(), 200, 0), 1.0f)
                            .build())
    );

    //幽匿猪儿虫
    public static final DeferredItem<Item> SCULK_CATERPILLAR = ITEMS.registerItem(
            "sculk_caterpillar",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(18)
                            .saturationModifier(0.2f)
                            .effect(() -> new MobEffectInstance(ModEffects.SCULK.getDelegate(), 200, 0), 1.0f)
                            .build())
    );
}