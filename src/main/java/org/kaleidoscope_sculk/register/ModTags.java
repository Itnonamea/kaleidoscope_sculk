package org.kaleidoscope_sculk.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;

public class ModTags {

    public static final TagKey<Item> KITCHEN_KNIVES = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "kitchen_knives")
    );
}