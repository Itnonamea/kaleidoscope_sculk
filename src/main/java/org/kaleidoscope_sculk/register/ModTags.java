package org.kaleidoscope_sculk.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

    public static final TagKey<Item> KITCHEN_KNIVES = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "kitchen_knife")
    );
}