package org.kaleidoscope_sculk.handler;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.component.SoulSailData;
import org.kaleidoscope_sculk.item.SoulSailItem;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class SoulSailCraftingHandler {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        
        ItemStack crafted = event.getCrafting();

        
        if (!(crafted.getItem() instanceof SoulSailItem)) {
            return;
        }
        if (SoulSailItem.isFullItem(crafted)) {
            return;
        }

        
        Container inventory = event.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack ingredient = inventory.getItem(i);
            if (ingredient.getItem() instanceof SoulSailItem && SoulSailItem.isFullItem(ingredient)) {
                SoulSailData src = SoulSailItem.getSailData(ingredient);
                SoulSailItem.SailType newType = ((SoulSailItem) crafted.getItem()).getType();
                
                SoulSailItem.setSailData(crafted, src.withType(newType.name));
                break;
            }
        }
    }
}
