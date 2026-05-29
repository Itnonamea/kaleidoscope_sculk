package org.kaleidoscope_sculk.handler;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModItems;
import org.kaleidoscope_sculk.register.ModPotions;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class BrewingRecipeHandler {

    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        var builder = event.getBuilder();

        builder.addRecipe(
                Ingredient.of(createPotionStack(Potions.STRONG_STRENGTH)),
                Ingredient.of(ModItems.SCULK_FUNGUS.get()),
                createPotionStack(ModPotions.ABYSS_POTION)
        );
    }

    private static ItemStack createPotionStack(Holder<Potion> potion) {
        ItemStack stack = new ItemStack(Items.POTION);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
        return stack;
    }
}