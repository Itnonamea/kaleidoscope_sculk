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

// 锻造台配方：静匿升级模板 + 侵蚀菜刀 = 静匿菜刀
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
        // ==================== 静匿升级模板配方 ====================
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SILENT_UPGRADE_SMITHING_TEMPLATE.get(), 1)
                .pattern("WBW")
                .define('W', ModItems.WARDEN_TENDRIL.get())
                .define('B', ModItems.ANCIENT_BONE_FRAGMENT.get())
                .unlockedBy("has_warden_tendril", has(ModItems.WARDEN_TENDRIL.get()))
                .unlockedBy("has_ancient_bone_fragment", has(ModItems.ANCIENT_BONE_FRAGMENT.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "silent_upgrade_smithing_template"));

    }


}