package org.kaleidoscope_sculk.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
// 深板岩炉灶合成表（中间为灵魂营火）
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.DEEPSLATE_STOVE.get())
                .pattern("SSS")
                .pattern("SCS")
                .pattern("SSS")
                .define('S', Items.DEEPSLATE)
                .define('C', Items.SOUL_CAMPFIRE)
                .unlockedBy("has_deepslate", has(Items.DEEPSLATE))
                .unlockedBy("has_soul_campfire", has(Items.SOUL_CAMPFIRE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.EROSION_KITCHEN_KNIFE.get())
                .pattern("IB ")
                .pattern("IC ")
                .pattern("   ")
                .define('I', Items.IRON_INGOT)
                .define('B', Blocks.SCULK_CATALYST)
                .define('C', ModItems.SCULK_BRANCH.get())
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_sculk_catalyst", has(Blocks.SCULK_CATALYST))
                .unlockedBy("has_sculk_branch", has(ModItems.SCULK_BRANCH.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SCULK_DOUGH.get())
                .requires(Items.WATER_BUCKET)
                .requires(ModItems.SCULK_DUST.get())
                .requires(ModItems.SOUL.get())
                .unlockedBy("has_sculk_dust", has(ModItems.SCULK_DUST.get()))
                .unlockedBy("has_soul", has(ModItems.SOUL.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sculk_dough"));

// 熔炉烧炼
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.EERIE_MEAT.get()),
                        RecipeCategory.FOOD,
                        ModItems.COOKED_EERIE_MEAT.get(),
                        0.35f,
                        200)
                .unlockedBy("has_eerie_meat", has(ModItems.EERIE_MEAT.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "cooked_eerie_meat_smelting"));

// 烟熏炉配方
        SimpleCookingRecipeBuilder.smoking(
                        Ingredient.of(ModItems.EERIE_MEAT.get()),
                        RecipeCategory.FOOD,
                        ModItems.COOKED_EERIE_MEAT.get(),
                        0.35f,
                        100)
                .unlockedBy("has_eerie_meat", has(ModItems.EERIE_MEAT.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "cooked_eerie_meat_smoking"));

// 营火配方
        SimpleCookingRecipeBuilder.campfireCooking(
                        Ingredient.of(ModItems.EERIE_MEAT.get()),
                        RecipeCategory.FOOD,
                        ModItems.COOKED_EERIE_MEAT.get(),
                        0.35f,
                        600)
                .unlockedBy("has_eerie_meat", has(ModItems.EERIE_MEAT.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "cooked_eerie_meat_campfire"));

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(ModItems.SILENT_UPGRADE_SMITHING_TEMPLATE.get()),
                        Ingredient.of(ModItems.EROSION_KITCHEN_KNIFE.get()),
                        Ingredient.of(Blocks.SCULK_SHRIEKER.asItem()),  // 幽匿尖啸体
                        RecipeCategory.TOOLS,
                        ModItems.SILENT_KITCHEN_KNIFE.get()
                )
                .unlocks("has_silent_upgrade_smithing_template", has(ModItems.SILENT_UPGRADE_SMITHING_TEMPLATE.get()))
                .unlocks("has_erosion_knife", has(ModItems.EROSION_KITCHEN_KNIFE.get()))
                .unlocks("has_sculk_shrieker", has(Blocks.SCULK_SHRIEKER))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "silent_knife_smithing"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SILENT_UPGRADE_SMITHING_TEMPLATE.get(), 1)
                .pattern("WBW")
                .define('W', ModItems.WARDEN_TENDRIL.get())
                .define('B', ModItems.ANCIENT_BONE_FRAGMENT.get())
                .unlockedBy("has_warden_tendril", has(ModItems.WARDEN_TENDRIL.get()))
                .unlockedBy("has_ancient_bone_fragment", has(ModItems.ANCIENT_BONE_FRAGMENT.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "silent_upgrade_smithing_template"));

// 满级魂幡 + 8灵魂 → 千魂幡(非满级)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.THOUSAND_SOUL_SAIL.get())
                .requires(ModItems.SOUL_SAIL_FULL.get())
                .requires(ModItems.SOUL.get(), 8)
                .unlockedBy("has_soul_sail_full", has(ModItems.SOUL_SAIL_FULL.get()))
                .unlockedBy("has_soul", has(ModItems.SOUL.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(
                        Kaleidoscope_sculk.MODID, "soul_sail_full_to_thousand"));

// 满级千魂幡 + 8灵魂 → 万魂幡(非满级)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MYRIAD_SOUL_SAIL.get())
                .requires(ModItems.THOUSAND_SOUL_SAIL_FULL.get())
                .requires(ModItems.SOUL.get(), 8)
                .unlockedBy("has_thousand_soul_sail_full", has(ModItems.THOUSAND_SOUL_SAIL_FULL.get()))
                .unlockedBy("has_soul", has(ModItems.SOUL.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(
                        Kaleidoscope_sculk.MODID, "thousand_soul_sail_full_to_myriad"));

        // 深板岩蛋糕片合成配方
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.DEEPSLATE_CAKE_SLICE.get(), 1)
                .pattern(" S ")
                .pattern(" D ")
                .pattern(" P ")
                .define('S', Items.SUGAR)
                .define('D', ModItems.SCULK_DOUGH.get())
                .define('P', Items.DEEPSLATE_BRICK_SLAB)
                .unlockedBy("has_sugar", has(Items.SUGAR))
                .unlockedBy("has_sculk_dough", has(ModItems.SCULK_DOUGH.get()))
                .unlockedBy("has_deepslate_slab", has(Items.DEEPSLATE_BRICK_SLAB))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "deepslate_cake_slice"));


        // 古城风格生鱼片（合成配方，添加碗）
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.ANCIENT_CITY_STYLE_SASHIMI.get())
                .requires(Items.TROPICAL_FISH, 4)   // 4条热带鱼
                .requires(Items.RED_CANDLE)         // 1个红蜡烛
                .requires(Items.SCULK_SHRIEKER)     // 1个幽匿尖啸体
                .requires(Items.BOWL)               // 1个碗（盛放容器）
                .unlockedBy("has_tropical_fish", has(Items.TROPICAL_FISH))
                .unlockedBy("has_red_candle", has(Items.RED_CANDLE))
                .unlockedBy("has_sculk_shrieker", has(Items.SCULK_SHRIEKER))
                .unlockedBy("has_bowl", has(Items.BOWL))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(
                        Kaleidoscope_sculk.MODID, "ancient_city_style_sashimi"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOUL.get())
                .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
                .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
                .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
                .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
                .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
                .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
                .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
                .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
                .requires(Ingredient.of(Items.SOUL_SAND, Items.SOUL_SOIL))
                .unlockedBy("has_soul_sand", has(Items.SOUL_SAND))
                .unlockedBy("has_soul_soil", has(Items.SOUL_SOIL))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(
                        Kaleidoscope_sculk.MODID, "soul_from_soul_sand_or_soil"));
    }
}