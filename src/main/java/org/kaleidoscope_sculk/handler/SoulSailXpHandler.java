package org.kaleidoscope_sculk.handler;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
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

        if (tryStoreIntoInventory(player, orb.getValue())) {
            event.setCanceled(true);
            orb.discard();
        }
    }

    private static boolean tryStoreIntoInventory(Player player, int xpPoints) {
        Inventory inventory = player.getInventory();
        int size = inventory.getContainerSize();
        int selected = inventory.selected;
        int offhand = size - 1;

        if (storeInSlot(player, inventory, selected, xpPoints)) {
            return true;
        }
        if (offhand != selected && storeInSlot(player, inventory, offhand, xpPoints)) {
            return true;
        }

        for (int i = 0; i < size; i++) {
            if (i == selected || i == offhand) {
                continue;
            }
            if (storeInSlot(player, inventory, i, xpPoints)) {
                return true;
            }
        }
        return false;
    }

    private static boolean storeInSlot(Player player, Inventory inventory, int slot, int xpPoints) {
        ItemStack stack = inventory.getItem(slot);
        if (!SoulSailItem.isNonFullItem(stack) || !(stack.getItem() instanceof SoulSailItem sailItem)) {
            return false;
        }

        int beforeXp = SoulSailItem.getStoredXp(stack);
        boolean becameFull = sailItem.storeXp(stack, xpPoints);

        if (SoulSailItem.getStoredXp(stack) <= beforeXp) {
            return false;
        }

        if (becameFull) {
            inventory.setItem(slot, SoulSailItem.convertToFullItem(stack));
        }

        player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.3f, 1.5f);
        return true;
    }
}
