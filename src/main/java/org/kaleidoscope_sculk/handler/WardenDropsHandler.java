package org.kaleidoscope_sculk.handler;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModItems;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class WardenDropsHandler {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Warden warden)) {
            return;
        }

        int dropCount = 1 + warden.getRandom().nextInt(2); 

        for (int i = 0; i < dropCount; i++) {
            ItemEntity drop = new ItemEntity(
                    warden.level(),
                    warden.getX(),
                    warden.getY(),
                    warden.getZ(),
                    ModItems.WARDEN_TENDRIL.get().getDefaultInstance()
            );
            drop.setDefaultPickUpDelay();
            event.getDrops().add(drop);
        }
    }
}
