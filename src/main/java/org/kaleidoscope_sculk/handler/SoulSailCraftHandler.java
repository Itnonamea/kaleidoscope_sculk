//// handler/SoulSailCraftHandler.java
//package org.kaleidoscope_sculk.handler;
//
//import net.minecraft.world.Container;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.event.entity.player.PlayerEvent;
//import org.kaleidoscope_sculk.Kaleidoscope_sculk;
//import org.kaleidoscope_sculk.item.SoulSailItem;
//import org.kaleidoscope_sculk.register.ModItems;
//
//@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
//public class SoulSailCraftHandler {
//
//    @SubscribeEvent
//    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
//        Player player = event.getEntity();
//        ItemStack result = event.getCrafting();
//        Container container = event.getInventory();
//
//        // 检查合成结果是否是满级物品
//        boolean isValidResult = result.is(ModItems.SOUL_SAIL_FULL.get()) ||
//                result.is(ModItems.THOUSAND_SOUL_SAIL_FULL.get()) ||
//                result.is(ModItems.MYRIAD_SOUL_SAIL_FULL.get());
//
//        if (!isValidResult) return;
//
//        // 查找魂幡和统计灵魂数量
//        ItemStack sailStack = null;
//        int soulCount = 0;
//
//        for (int i = 0; i < container.getContainerSize(); i++) {
//            ItemStack stack = container.getItem(i);
//            if (stack.isEmpty()) continue;
//
//            if (stack.is(ModItems.SOUL.get())) {
//                soulCount += stack.getCount();
//            } else if (isFullSoulSailItem(stack)) {
//                sailStack = stack.copy();
//            }
//        }
//
//        // 必须有1个满级魂幡和8个灵魂
//        if (sailStack == null || soulCount != 8) return;
//
//        int rotation = SoulSailItem.getRotation(sailStack);
//
//        // 满级魂幡 → 千魂幡(非满级)
//        if (sailStack.is(ModItems.SOUL_SAIL_FULL.get())) {
//            ItemStack newStack = new ItemStack(ModItems.THOUSAND_SOUL_SAIL.get());
//            SoulSailItem.setSailData(newStack, SoulSailItem.SailType.THOUSAND, rotation, 0);
//            // 注意：结果槽索引通常为0，但 Container 不保证有 "结果槽" 的概念
//            // 此处我们不直接修改容器，而是标记为已处理
//            player.displayClientMessage(
//                    net.minecraft.network.chat.Component.literal("§d✦ 满级魂幡已转化为千魂幡！"),
//                    false
//            );
//            return;
//        }
//
//        // 满级千魂幡 → 万魂幡(非满级)
//        if (sailStack.is(ModItems.THOUSAND_SOUL_SAIL_FULL.get())) {
//            ItemStack newStack = new ItemStack(ModItems.MYRIAD_SOUL_SAIL.get());
//            SoulSailItem.setSailData(newStack, SoulSailItem.SailType.MYRIAD, rotation, 0);
//            player.displayClientMessage(
//                    net.minecraft.network.chat.Component.literal("§d✦ 满级千魂幡已转化为万魂幡！"),
//                    false
//            );
//            return;
//        }
//
//        // 满级万魂幡 → 已满级
//        if (sailStack.is(ModItems.MYRIAD_SOUL_SAIL_FULL.get())) {
//            player.displayClientMessage(
//                    net.minecraft.network.chat.Component.literal("§6✦ 万魂幡已到达最高等级！"),
//                    false
//            );
//        }
//    }
//
//    /**
//     * 检查是否为满级魂幡物品
//     */
//    private static boolean isFullSoulSailItem(ItemStack stack) {
//        return stack.is(ModItems.SOUL_SAIL_FULL.get()) ||
//                stack.is(ModItems.THOUSAND_SOUL_SAIL_FULL.get()) ||
//                stack.is(ModItems.MYRIAD_SOUL_SAIL_FULL.get());
//    }
//}