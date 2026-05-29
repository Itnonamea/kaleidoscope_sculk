package org.kaleidoscope_sculk.dropsHandler;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.ModItems;

import java.util.Random;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class WardenDropsHandler {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Warden)) {
            return;
        }

        Warden warden = (Warden) event.getEntity();
        int dropCount = 1 + RANDOM.nextInt(2); // 1~2个

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