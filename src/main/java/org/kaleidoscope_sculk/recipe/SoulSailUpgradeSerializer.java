//// recipe/SoulSailUpgradeSerializer.java
//package org.kaleidoscope_sculk.recipe;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.MapCodec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.world.item.crafting.CraftingBookCategory;
//import net.minecraft.world.item.crafting.RecipeSerializer;
//
//public class SoulSailUpgradeSerializer implements RecipeSerializer<SoulSailUpgradeRecipe> {
//
//    private static final MapCodec<SoulSailUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
//            inst.group(
//                    CraftingBookCategory.CODEC.fieldOf("category").forGetter(SoulSailUpgradeRecipe::category),
//                    Codec.STRING.fieldOf("type").forGetter(recipe -> recipe.type)
//            ).apply(inst, SoulSailUpgradeRecipe::new)
//    );
//
//    private static final StreamCodec<RegistryFriendlyByteBuf, SoulSailUpgradeRecipe> STREAM_CODEC =
//            StreamCodec.of(SoulSailUpgradeSerializer::toNetwork, SoulSailUpgradeSerializer::fromNetwork);
//
//    @Override
//    public MapCodec<SoulSailUpgradeRecipe> codec() {
//        return CODEC;
//    }
//
//    @Override
//    public StreamCodec<RegistryFriendlyByteBuf, SoulSailUpgradeRecipe> streamCodec() {
//        return STREAM_CODEC;
//    }
//
//    private static SoulSailUpgradeRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
//        CraftingBookCategory category = buffer.readEnum(CraftingBookCategory.class);
//        String type = buffer.readUtf();
//        return new SoulSailUpgradeRecipe(category, type);
//    }
//
//    private static void toNetwork(RegistryFriendlyByteBuf buffer, SoulSailUpgradeRecipe recipe) {
//        buffer.writeEnum(recipe.category());
//        buffer.writeUtf(recipe.type);
//    }
//}