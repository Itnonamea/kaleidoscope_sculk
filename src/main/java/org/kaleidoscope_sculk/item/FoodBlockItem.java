package org.kaleidoscope_sculk.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodBlockItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.kaleidoscope_sculk.block.FoodBiteBlock;

import java.util.Optional;

public class FoodBlockItem extends BowlFoodBlockItem {

    public FoodBlockItem(Block block) {
        super(block, getWholeDishFoodProperties(block), getBlockContainer(block));
    }

    private static FoodProperties getBlockFoodProperties(Block block) {
        return block instanceof FoodBiteBlock foodBite
                ? foodBite.getFoodProperties()
                : new FoodProperties.Builder().build();
    }

    private static FoodProperties getWholeDishFoodProperties(Block block) {
        FoodProperties biteFood = getBlockFoodProperties(block);
        if (!(block instanceof FoodBiteBlock foodBite)) {
            return biteFood;
        }
        int maxBites = Math.max(1, foodBite.getMaxBites());
        return new FoodProperties(
                biteFood.nutrition() * maxBites,
                biteFood.saturation(),
                biteFood.canAlwaysEat(),
                biteFood.eatSeconds(),
                Optional.empty(),
                biteFood.effects()
        );
    }

    @Nullable
    private static ItemLike getBlockContainer(Block block) {
        return block instanceof FoodBiteBlock foodBite ? foodBite.getContainer() : null;
    }
}
