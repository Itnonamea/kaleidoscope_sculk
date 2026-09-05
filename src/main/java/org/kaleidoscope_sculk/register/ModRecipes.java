// register/ModRecipes.java
package org.kaleidoscope_sculk.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
//import org.kaleidoscope_sculk.recipe.SoulSailUpgradeSerializer;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Kaleidoscope_sculk.MODID);

//    public static final DeferredHolder<RecipeSerializer<?>, SoulSailUpgradeSerializer> SOUL_SAIL_UPGRADE_SERIALIZER =
//            RECIPE_SERIALIZERS.register("soul_sail_upgrade", SoulSailUpgradeSerializer::new);
}