package Tequilacat.KaleidoscopeSculk.register;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodOnlyItem;
import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.IBarrel;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.DrinkBlockItem;
import Tequilacat.KaleidoscopeSculk.Kaleidoscope_sculk;
import Tequilacat.KaleidoscopeSculk.entity.AncientBoneFragmentProjectile;
import Tequilacat.KaleidoscopeSculk.item.DeepslateCakeSliceItem;
import Tequilacat.KaleidoscopeSculk.item.SculkBoneSickleItem;
import Tequilacat.KaleidoscopeSculk.item.SimpleItems;
import Tequilacat.KaleidoscopeSculk.item.SoulSailItem;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Kaleidoscope_sculk.MODID);

    /** Items that can act as a kitchen knife (used to scrape sculk blocks). */
    public static final TagKey<Item> KITCHEN_KNIVES = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "kitchen_knives")
    );

    public static ItemStack maxLevelDrink(DeferredItem<? extends Item> item) {
        ItemStack stack = item.get().getDefaultInstance();
        BottleBlockItem.setBrewLevel(stack, IBarrel.BREWING_FINISHED);
        return stack;
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Kaleidoscope_sculk.MODID);


    public static final DeferredItem<SimpleItems.FoodBlock> SCULK_STEW_BLOCK_ITEM = ITEMS.register(
            "sculk_stew_block",
            () -> new SimpleItems.FoodBlock(ModBlocks.SCULK_STEW_BLOCK.get())
    );


    public static final DeferredItem<BlockItem> DEEPSLATE_STOVE = ITEMS.registerItem(
            "deepslate_stove",
            properties -> new BlockItem(ModBlocks.DEEPSLATE_STOVE.get(), properties),
            new Item.Properties()
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> KALEIDOSCOPE_SCULK_TAB =
            CREATIVE_TABS.register("kaleidoscope_sculk_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + Kaleidoscope_sculk.MODID))
                    .icon(() -> new ItemStack(ModItems.COOKED_WARDEN_TENDRIL_BOWL.get()))
                    .displayItems((parameters, output) -> {

                        output.accept(ModItems.DEEPSLATE_STOVE.get());


                        output.accept(ModItems.WARDEN_TENDRIL.get());
                        output.accept(ModItems.ANCIENT_BONE_FRAGMENT.get());
                        output.accept(ModItems.SCULK_FUNGUS.get());
                        output.accept(ModItems.SCULK_PINAPPLE.get());
                        output.accept(ModItems.BOIL_SCULK_PINAPPLE.get());
                        output.accept(ModItems.SCULK_CATERPILLAR.get());
                        output.accept(ModItems.SOUL.get());
                        output.accept(ModItems.SCULK_BRANCH.get());
                        output.accept(ModItems.EERIE_MEAT.get());
                        output.accept(ModItems.SCULK_DUST.get());
                        output.accept(ModItems.SCULK_DOUGH.get());
                        output.accept(ModItems.SILENT_UPGRADE_SMITHING_TEMPLATE.get());

                        output.accept(ModItems.EROSION_KITCHEN_KNIFE.get());
                        output.accept(ModItems.SILENT_KITCHEN_KNIFE.get());
                        output.accept(ModItems.SCULK_BONE_SICKLE.get());


                        output.accept(ModItems.ECHO_SEED.get());


                        output.accept(ModItems.SCULK_FLESH.get());

                        output.accept(ModItems.SCULK_FUNGUS_SOUP.get());
                        output.accept(ModItems.COOKED_WARDEN_TENDRIL_BOWL.get());
                        output.accept(ModItems.ANCIENT_BRITTLE_BONE_FRAGMENTS.get());
                        output.accept(ModItems.PORK_ANCIENT_BONE_SOUP.get());
                        output.accept(ModItems.SOUL_PANCAKE.get());
                        output.accept(ModItems.SCULK_MOONCAKE.get());
                        output.accept(ModItems.COOKED_EERIE_MEAT.get());


                        output.accept(ModItems.SOUL_SAIL.get());


                        output.accept(ModItems.SCULK_STEW_BLOCK_ITEM.get());
                        output.accept(ModItems.SCULK_PORK_RIBS_BLOCK_ITEM.get());
                        output.accept(ModItems.SCULK_CHICKEN_STEW_BLOCK_ITEM.get());
                        output.accept(ModItems.ANCIENT_CITY_STYLE_SASHIMI.get());
                        output.accept(ModItems.SCULK_LAMB_CHOP.get());

                        output.accept(ModItems.DEEPSLATE_CAKE_SLICE.get());
                        output.accept(ModItems.SCULK_JUICE_BUCKET.get());
                        output.accept(ModItems.ECHO_PUER_TEA_BAG.get());
                        Item echoPuer = TeacupRegistry.getItem(ModRegistries.ECHO_PUER);
                        if (echoPuer != Items.AIR) {
                            output.accept(echoPuer);
                        }
                        output.accept(ModItems.maxLevelDrink(ModItems.HUADIAO_WINE));
                        output.accept(ModItems.maxLevelDrink(ModItems.HONGLAN_WINE));


                        output.accept(ModItems.maxLevelDrink(ModItems.SCULK_BREW));
                    })
                    .build());


    public static final DeferredItem<DrinkBlockItem> SCULK_BREW = ITEMS.register(
            "sculk_brew",
            () -> new DrinkBlockItem(ModBlocks.SCULK_BREW_BOTTLE.get())
    );

    public static final DeferredItem<DrinkBlockItem> HUADIAO_WINE = ITEMS.register(
            "huadiao_wine",
            () -> new DrinkBlockItem(ModBlocks.HUADIAO_WINE.get())
    );

    public static final DeferredItem<DrinkBlockItem> HONGLAN_WINE = ITEMS.register(
            "honglan_wine",
            () -> new DrinkBlockItem(ModBlocks.HONGLAN_WINE.get())
    );


    public static final DeferredItem<BucketItem> SCULK_JUICE_BUCKET = ITEMS.register(
            "sculk_juice_bucket",
            () -> new BucketItem(ModRegistries.SCULK_JUICE.get(),
                    new Item.Properties().stacksTo(16).craftRemainder(Items.BUCKET))
    );


    public static final DeferredItem<Item> SOUL_SAIL =
            ITEMS.register("soul_sail", () -> new SoulSailItem(soulSailProperties()));

    /**
     * Soul Sail: 4 armor, no durability, unbreakable, equipped in the head slot.
     */
    private static Item.Properties soulSailProperties() {
        return new Item.Properties()
                .stacksTo(1)
                .component(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                        .add(Attributes.ARMOR,
                                new AttributeModifier(
                                        ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "soul_sail_armor"),
                                        4.0D, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.HEAD)
                        .build());
    }


    public static final DeferredItem<BlockItem> DEEPSLATE_CAKE = ITEMS.registerItem(
            "deepslate_cake",
            properties -> new BlockItem(ModBlocks.DEEPSLATE_CAKE.get(), properties),
            new Item.Properties()
    );

    public static final DeferredItem<DeepslateCakeSliceItem> DEEPSLATE_CAKE_SLICE = ITEMS.registerItem(
            "deepslate_cake_slice",
            DeepslateCakeSliceItem::new,
            new Item.Properties()
    );


    public static final DeferredItem<SimpleItems.FoodBlock> SCULK_LAMB_CHOP = ITEMS.register(
            "sculk_lamb_chop",
            () -> new SimpleItems.FoodBlock(ModBlocks.SCULK_LAMB_CHOP_BLOCK.get())
    );

    public static final DeferredHolder<Item, SimpleItems.FoodBlock> ANCIENT_CITY_STYLE_SASHIMI =
            ITEMS.register("ancient_city_style_sashimi",
                    () -> new SimpleItems.FoodBlock(ModBlocks.ANCIENT_CITY_STYLE_SASHIMI_BLOCK.get())
            );


    public static final DeferredItem<Item> SILENT_UPGRADE_SMITHING_TEMPLATE = ITEMS.registerItem(
            "silent_upgrade_smithing_template",
            properties -> new Item(properties),
            new Item.Properties()
    );


    public static final DeferredItem<SimpleItems.SilentKnife> SILENT_KITCHEN_KNIFE = ITEMS.register(
            "silent_kitchen_knife",
            SimpleItems.SilentKnife::new
    );

    public static final DeferredItem<SculkBoneSickleItem> SCULK_BONE_SICKLE =
            ITEMS.register("sculk_bone_sickle", SculkBoneSickleItem::new);


    public static final DeferredItem<Item> COOKED_EERIE_MEAT = ITEMS.registerItem(
            "cooked_eerie_meat",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationModifier(0.8f)
                            .build())
    );


    public static final DeferredItem<Item> SCULK_PINAPPLE = ITEMS.registerItem(
            "sculk_pinapple",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(0.3f)
                            .build())
    );

    public static final DeferredItem<Item> SCULK_DUST = ITEMS.registerItem(
            "sculk_dust",
            Item::new,
            new Item.Properties()
    );

    public static final DeferredItem<Item> ECHO_PUER_TEA_BAG = ITEMS.registerItem(
            "echo_puer_tea_bag",
            Item::new,
            new Item.Properties()
    );


    public static final DeferredItem<Item> SCULK_DOUGH = ITEMS.registerItem(
            "sculk_dough",
            Item::new,
            new Item.Properties()
    );


    public static final DeferredItem<Item> SOUL = ITEMS.registerItem(
            "soul",
            SimpleItems.Soul::new,
            new Item.Properties()
                    .stacksTo(64)
    );


    public static final DeferredItem<Item> SCULK_BRANCH = ITEMS.registerItem(
            "sculk_branch",
            Item::new,
            new Item.Properties()
                    .stacksTo(64)
    );


    public static final DeferredItem<SimpleItems.ErosionKnife> EROSION_KITCHEN_KNIFE = ITEMS.register(
            "erosion_knife",
            SimpleItems.ErosionKnife::new
    );


    public static final DeferredItem<SimpleItems.FoodBlock> SCULK_CHICKEN_STEW_BLOCK_ITEM = ITEMS.register(
            "sculk_chicken_stew_block",
            () -> new SimpleItems.FoodBlock(ModBlocks.SCULK_CHICKEN_STEW_BLOCK.get())
    );


    public static final DeferredItem<Item> EERIE_MEAT = ITEMS.registerItem(
            "eerie_meat",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(3)
                            .saturationModifier(0.3f)
                            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 200, 0), 1.0f)
                            .build())
    );


    public static final DeferredItem<Item> ECHO_SEED = ITEMS.registerItem(
            "echo_seed",
            properties -> new SimpleItems.EchoSeed(ModBlocks.ECHO_CROP.get(), properties),
            new Item.Properties()
    );


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


    public static final DeferredItem<BowlFoodOnlyItem> BOIL_SCULK_PINAPPLE = ITEMS.register(
            "boil_sculk_pinapple",
            () -> new BowlFoodOnlyItem(new FoodProperties.Builder()
                    .nutrition(6)
                    .saturationModifier(0.667f)
                    .alwaysEdible()
                    .effect(() -> new MobEffectInstance(ModEffects.ECHO.getDelegate(), 3600, 0), 1.0f)
                    .build())
    );


    public static final DeferredItem<BowlFoodOnlyItem> COOKED_WARDEN_TENDRIL_BOWL = ITEMS.register(
            "cooked_warden_tendril_bowl",
            () -> new BowlFoodOnlyItem(new FoodProperties.Builder()
                    .nutrition(8)
                    .saturationModifier(0.667f)
                    .alwaysEdible()
                    .effect(() -> new MobEffectInstance(ModEffects.SONIC_WAVE.getDelegate(), 6000, 2), 1.0f)
                    .effect(() -> new MobEffectInstance(ModEffects.ECHO.getDelegate(), 3600, 0), 1.0f)
                    .build())
    );


    public static final DeferredItem<BowlFoodOnlyItem> PORK_ANCIENT_BONE_SOUP = ITEMS.register(
            "pork_ancient_bone_soup",
            () -> new BowlFoodOnlyItem(new FoodProperties.Builder()
                    .nutrition(6)
                    .saturationModifier(0.667f)
                    .alwaysEdible()
                    .effect(() -> new MobEffectInstance(VIGOR.getDelegate(), 6000, 0), 1.0f)
                    .effect(() -> new MobEffectInstance(ModEffects.SONIC_WAVE.getDelegate(), 6000, 0), 1.0f)
                    .build())
    );


    public static final DeferredItem<BowlFoodOnlyItem> SCULK_FUNGUS_SOUP = ITEMS.register(
            "sculk_fungus_soup",
            () -> new BowlFoodOnlyItem(new FoodProperties.Builder()
                    .nutrition(5)
                    .saturationModifier(0.3f)
                    .alwaysEdible()
                    .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 600, 0), 1.0f)
                    .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0f)
                    .build())
    );


    public static final DeferredItem<Item> ANCIENT_BONE_FRAGMENT = ITEMS.registerItem(
            "ancient_bone_fragment",
            properties -> new Item(properties) {
                @Override
                public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                    ItemStack stack = player.getItemInHand(hand);

                    if (!level.isClientSide) {
                        AncientBoneFragmentProjectile projectile = new AncientBoneFragmentProjectile(level, player);
                        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.5f, 1.0f);
                        level.addFreshEntity(projectile);
                    }

                    player.playSound(SoundEvents.SNOWBALL_THROW, 0.5f, 0.8f);

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


    public static final DeferredItem<Item> SCULK_FUNGUS = ITEMS.registerItem(
            "sculk_fungus",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(2)
                            .saturationModifier(0.2f)
                            .alwaysEdible()
                            .build())
    );


    public static final DeferredItem<Item> SOUL_PANCAKE = ITEMS.registerItem(
            "soul_pancake",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(8)
                            .alwaysEdible()
                            .saturationModifier(0.625f)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 0), 1.0f)
                            .build())
    );


    public static final DeferredItem<Item> ANCIENT_BRITTLE_BONE_FRAGMENTS = ITEMS.registerItem(
            "ancient_brittle_bone_fragments",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .alwaysEdible()
                            .nutrition(6)
                            .saturationModifier(0.8f)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1800, 1), 1.0f)
                            .build())
    );


    public static final DeferredItem<Item> SCULK_CATERPILLAR = ITEMS.registerItem(
            "sculk_caterpillar",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .alwaysEdible()
                            .nutrition(18)
                            .saturationModifier(0.2f)
                            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 600, 0), 1.0f)
                            .effect(() -> new MobEffectInstance(ModEffects.ECHO.getDelegate(), 3600, 0), 1.0f)
                            .build())
    );

    public static final DeferredItem<Item> SCULK_FLESH = ITEMS.registerItem(
            "sculk_flesh",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(0.8f)
                            .effect(() -> new MobEffectInstance(MobEffects.DARKNESS, 200, 0), 1.0f)
                            .build())
    );


    public static final DeferredItem<SimpleItems.FoodBlock> SCULK_PORK_RIBS_BLOCK_ITEM = ITEMS.register(
            "sculk_pork_ribs_block",
            () -> new SimpleItems.FoodBlock(ModBlocks.SCULK_PORK_RIBS_BLOCK.get())
    );


    public static final DeferredItem<Item> SCULK_MOONCAKE = ITEMS.registerItem(
            "sculk_mooncake",
            Item::new,
            new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(14)
                            .saturationModifier(1.2f)
                            .alwaysEdible()
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 2), 1.0f)
                            .build())
    );

    private static final ResourceKey<CreativeModeTab> COOKERY_MAIN_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "cookery_main"));

    private static final ResourceKey<CreativeModeTab> COOKERY_FOOD_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "cookery_food"));

    /**
     * Cookery's creative tabs iterate over all teacup data and add every entry, so Echo Puer is included too.
     * Remove it from Cookery's tabs here (this mod's own tab keeps it).
     */
    public static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> key = event.getTabKey();
        if (!key.equals(COOKERY_MAIN_TAB) && !key.equals(COOKERY_FOOD_TAB)) {
            return;
        }

        Item echoPuer = TeacupRegistry.getItem(ModRegistries.ECHO_PUER);
        if (echoPuer == Items.AIR) {
            return;
        }

        event.remove(new ItemStack(echoPuer), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
