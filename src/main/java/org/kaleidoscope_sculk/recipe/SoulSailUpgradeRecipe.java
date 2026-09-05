//// recipe/SoulSailUpgradeRecipe.java
//package org.kaleidoscope_sculk.recipe;
//
//import net.minecraft.core.HolderLookup;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.level.Level;
//import org.kaleidoscope_sculk.item.SoulSailItem;
//import org.kaleidoscope_sculk.register.ModItems;
//import org.kaleidoscope_sculk.register.ModRecipes;
//
//public class SoulSailUpgradeRecipe implements CraftingRecipe {
//
//    private final CraftingBookCategory category;
//    public final String type;
//
//    public SoulSailUpgradeRecipe(CraftingBookCategory category, String type) {
//        this.category = category;
//        this.type = type;
//    }
//
//    @Override
//    public boolean matches(CraftingInput input, Level level) {
//        ItemStack sail = null;
//        int soulCount = 0;
//        int sailCount = 0;
//
//        for (int i = 0; i < input.size(); i++) {
//            ItemStack stack = input.getItem(i);
//            if (stack.isEmpty()) continue;
//
//            if (stack.is(ModItems.SOUL.get())) {
//                soulCount += stack.getCount();
//            } else if (stack.is(ModItems.SOUL_SAIL.get())) {
//                sailCount++;
//                sail = stack;
//            } else {
//                return false;
//            }
//        }
//
//        // 必须恰好1个魂幡和8个灵魂
//        if (sailCount != 1 || soulCount != 8) return false;
//        if (sail == null) return false;
//
//        // 获取魂幡信息
//        SoulSailItem.SailType currentType = SoulSailItem.getSailType(sail);
//        int storedXp = SoulSailItem.getStoredXp(sail);
//        int storedLevel = SoulSailItem.xpToLevel(storedXp);
//        int maxXp = SoulSailItem.getMaxXp(sail);
//
//        // 检查是否满级（经验已满）
//        boolean isFullLevel = storedXp >= maxXp;
//
//        return switch (type) {
//            case "to_thousand" ->
//                    currentType == SoulSailItem.SailType.SOUL && isFullLevel;
//            case "to_myriad" ->
//                    currentType == SoulSailItem.SailType.THOUSAND && isFullLevel;
//            default -> false;
//        };
//    }
//
//    @Override
//    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
//        for (int i = 0; i < input.size(); i++) {
//            ItemStack stack = input.getItem(i);
//            if (stack.is(ModItems.SOUL_SAIL.get())) {
//                SoulSailItem.SailType newType = switch (type) {
//                    case "to_thousand" -> SoulSailItem.SailType.THOUSAND;
//                    case "to_myriad" -> SoulSailItem.SailType.MYRIAD;
//                    default -> SoulSailItem.getSailType(stack);
//                };
//
//                ItemStack result = new ItemStack(ModItems.SOUL_SAIL.get());
//                SoulSailItem.setSailData(result, newType,
//                        SoulSailItem.getRotation(stack),
//                        SoulSailItem.getStoredXp(stack));
//                return result;
//            }
//        }
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public boolean canCraftInDimensions(int width, int height) {
//        return width >= 3 && height >= 3;
//    }
//
//    @Override
//    public CraftingBookCategory category() {
//        return category;
//    }
//
//    @Override
//    public RecipeSerializer<?> getSerializer() {
//        return ModRecipes.SOUL_SAIL_UPGRADE_SERIALIZER.get();
//    }
//
//    @Override
//    public ItemStack getResultItem(HolderLookup.Provider registries) {
//        return new ItemStack(ModItems.SOUL_SAIL.get());
//    }
//}