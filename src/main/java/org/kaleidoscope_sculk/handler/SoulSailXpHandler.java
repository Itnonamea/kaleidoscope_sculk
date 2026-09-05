package org.kaleidoscope_sculk.handler;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.item.SoulSailItem;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class SoulSailXpHandler {

    @SubscribeEvent
    public static void onPlayerXpPickup(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        ExperienceOrb orb = event.getOrb();
        int xpPoints = orb.getValue();

        // 检查主手
        ItemStack mainHand = player.getMainHandItem();
        if (isNonFullSoulSail(mainHand)) {
            if (tryStoreXp(player, mainHand, xpPoints)) {
                event.setCanceled(true);
                orb.discard();
                return;
            }
        }

        // 检查副手
        ItemStack offhand = player.getOffhandItem();
        if (isNonFullSoulSail(offhand)) {
            if (tryStoreXp(player, offhand, xpPoints)) {
                event.setCanceled(true);
                orb.discard();
                return;
            }
        }

        // 检查背包
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isNonFullSoulSail(stack)) {
                if (tryStoreXp(player, stack, xpPoints)) {
                    event.setCanceled(true);
                    orb.discard();
                    return;
                }
            }
        }
    }

    private static boolean isNonFullSoulSail(ItemStack stack) {
        return SoulSailItem.isNonFullItem(stack);
    }

    private static boolean tryStoreXp(Player player, ItemStack stack, int xpPoints) {
        if (SoulSailItem.isFullItem(stack)) {
            return false;
        }

        if (!(stack.getItem() instanceof SoulSailItem sailItem)) {
            return false;
        }

        int beforeXp = SoulSailItem.getStoredXp(stack);

        // 使用实例方法存储经验
        boolean isFull = sailItem.storeXp(stack, xpPoints);

        if (isFull) {
            int slot = findSlot(player, stack);
            if (slot != -1) {
                ItemStack fullStack = SoulSailItem.convertToFullItem(stack);
                player.getInventory().setItem(slot, fullStack);
                player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.3f, 1.5f);
                return true;
            }
        }

        int afterXp = SoulSailItem.getStoredXp(stack);
        if (afterXp > beforeXp) {
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.3f, 1.5f);
            return true;
        }

        return false;
    }

    private static int findSlot(Player player, ItemStack target) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack == target) {
                return i;
            }
        }
        return -1;
    }
}