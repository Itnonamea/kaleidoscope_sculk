package org.kaleidoscope_sculk.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class WardenDamageHandler {

    private static final Map<UUID, Float> ACCUMULATED_DAMAGE = new ConcurrentHashMap<>();

    
    private static final float DROP_INTERVAL_RATIO = 0.10f;
    
    private static final int MIN_DROPS_PER_INTERVAL = 1;
    private static final int MAX_DROPS_PER_INTERVAL = 2;

    @SubscribeEvent
    public static void onWardenDamage(LivingDamageEvent.Post event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Warden warden)) return;
        if (warden.level().isClientSide) return;

        float damage = event.getNewDamage();
        if (damage <= 0) return;

        UUID uuid = warden.getUUID();
        float maxHealth = warden.getMaxHealth();
        float intervalDamage = maxHealth * DROP_INTERVAL_RATIO;

        float currentDamage = ACCUMULATED_DAMAGE.getOrDefault(uuid, 0f);
        float newDamage = Math.min(currentDamage + damage, maxHealth);

        int totalIntervals = (int) (newDamage / intervalDamage);
        int previousIntervals = (int) (currentDamage / intervalDamage);
        int intervalsToProcess = totalIntervals - previousIntervals;

        for (int i = 0; i < intervalsToProcess; i++) {
            int count = MIN_DROPS_PER_INTERVAL
                    + warden.getRandom().nextInt(MAX_DROPS_PER_INTERVAL - MIN_DROPS_PER_INTERVAL + 1);
            for (int j = 0; j < count; j++) {
                spawnAncientBoneFragment(warden);
            }
        }

        ACCUMULATED_DAMAGE.put(uuid, newDamage);
    }

    private static void spawnAncientBoneFragment(Warden warden) {
        ItemStack fragment = new ItemStack(ModItems.ANCIENT_BONE_FRAGMENT.get());
        ItemEntity drop = new ItemEntity(
                warden.level(),
                warden.getX(),
                warden.getY(),
                warden.getZ(),
                fragment
        );
        drop.setDefaultPickUpDelay();
        warden.level().addFreshEntity(drop);
    }

    @SubscribeEvent
    public static void onWardenDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Warden warden) {
            ACCUMULATED_DAMAGE.remove(warden.getUUID());
        }
    }

    @SubscribeEvent
    public static void onWardenLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Warden warden) {
            ACCUMULATED_DAMAGE.remove(warden.getUUID());
        }
    }
}
