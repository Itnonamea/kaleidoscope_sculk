package org.kaleidoscope_sculk.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.kaleidoscope_sculk.register.ModItems;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class WardenDamageHandler {

    // 记录每个坚守者已累积的伤害（UUID -> 累积伤害）
    private static final Map<UUID, Float> ACCUMULATED_DAMAGE = new HashMap<>();
    private static final int DAMAGE_PER_DROP = 10;  // 每10点伤害掉落一个
    private static final int MAX_DROPS = 5;         // 最多掉落5个

    @SubscribeEvent
    public static void onWardenDamage(LivingDamageEvent.Post event) {
        Entity entity = event.getEntity();

        // 只处理坚守者
        if (!(entity instanceof Warden warden)) return;

        float damage = event.getNewDamage();
        if (damage <= 0) return;

        UUID uuid = warden.getUUID();
        float currentDamage = ACCUMULATED_DAMAGE.getOrDefault(uuid, 0f);
        float newDamage = currentDamage + damage;

        // 计算本次应该掉落的数量
        int totalDrops = (int) (newDamage / DAMAGE_PER_DROP);
        int previousDrops = (int) (currentDamage / DAMAGE_PER_DROP);
        int dropsToAdd = Math.min(totalDrops - previousDrops, MAX_DROPS - previousDrops);

        // 限制最多5个
        if (previousDrops >= MAX_DROPS) {
            ACCUMULATED_DAMAGE.remove(uuid);
            return;
        }

        // 掉落物品
        if (dropsToAdd > 0) {
            for (int i = 0; i < dropsToAdd; i++) {
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
        }

        // 更新累积伤害
        if (totalDrops >= MAX_DROPS) {
            ACCUMULATED_DAMAGE.remove(uuid);
        } else {
            ACCUMULATED_DAMAGE.put(uuid, newDamage);
        }
    }

    // 坚守者死亡时清除记录
    @SubscribeEvent
    public static void onWardenDeath(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        if (event.getEntity() instanceof Warden warden) {
            ACCUMULATED_DAMAGE.remove(warden.getUUID());
        }
    }
}