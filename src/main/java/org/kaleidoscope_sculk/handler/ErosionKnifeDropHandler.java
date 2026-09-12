package org.kaleidoscope_sculk.handler;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class ErosionKnifeDropHandler {

    private static boolean isConvertibleAnimal(Entity entity) {
        EntityType<?> type = entity.getType();
        return type == EntityType.PIG
                || type == EntityType.SHEEP
                || type == EntityType.COW
                || type == EntityType.CHICKEN;
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getSource().getEntity() instanceof Player player ? player : null;

        if (killer == null) return;

        ItemStack mainHand = killer.getMainHandItem();
        if (!mainHand.is(ModItems.EROSION_KITCHEN_KNIFE.get())
                && !mainHand.is(ModItems.SILENT_KITCHEN_KNIFE.get())) return;

        
        if (!isConvertibleAnimal(entity)) return;

        if (!entity.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) entity.level();
            Vec3 deathPos = entity.position();

            serverLevel.sendParticles(new SculkChargeParticleOptions(serverLevel.random.nextFloat()),
                    deathPos.x, deathPos.y + 0.5, deathPos.z,
                    25, 1.5, 1.2, 1.5, 0.05);

            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    deathPos.x, deathPos.y + 0.2, deathPos.z,
                    20, 1.2, 0.15, 1.2, 0.02);

            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                    deathPos.x, deathPos.y + 0.3, deathPos.z,
                    15, 1.0, 0.1, 1.0, 0.01);

            serverLevel.sendParticles(ParticleTypes.SOUL,
                    deathPos.x, deathPos.y + 0.6, deathPos.z,
                    12, 1.2, 0.8, 1.2, 0.01);

            serverLevel.sendParticles(new SculkChargeParticleOptions(serverLevel.random.nextFloat()),
                    deathPos.x, deathPos.y + 0.1, deathPos.z,
                    15, 1.6, 0.1, 1.6, 0.03);

            serverLevel.playSound(null,
                    deathPos.x, deathPos.y, deathPos.z,
                    SoundEvents.SCULK_BLOCK_SPREAD,
                    SoundSource.PLAYERS,
                    0.6f,
                    0.8f + serverLevel.random.nextFloat() * 0.4f
            );
        }

        int totalMeatCount = 0;
        List<ItemEntity> meatDrops = new ArrayList<>();

        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (isMeatItem(stack)) {
                totalMeatCount += stack.getCount();
                meatDrops.add(drop);
            }
        }

        
        for (ItemEntity drop : meatDrops) {
            event.getDrops().remove(drop);
            drop.discard();
        }

        
        if (totalMeatCount > 0) {
            ItemStack eerieMeatStack = new ItemStack(ModItems.EERIE_MEAT.get(), totalMeatCount);
            ItemEntity eerieMeatDrop = new ItemEntity(
                    entity.level(),
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    eerieMeatStack
            );
            eerieMeatDrop.setDefaultPickUpDelay();
            event.getDrops().add(eerieMeatDrop);
        }
    }

    
    private static boolean isMeatItem(ItemStack stack) {
        return stack.is(Items.PORKCHOP) ||
                stack.is(Items.COOKED_PORKCHOP) ||
                stack.is(Items.MUTTON) ||
                stack.is(Items.COOKED_MUTTON) ||
                stack.is(Items.BEEF) ||
                stack.is(Items.COOKED_BEEF) ||
                stack.is(Items.CHICKEN) ||
                stack.is(Items.COOKED_CHICKEN) ||
                stack.is(Items.RABBIT) ||
                stack.is(Items.COOKED_RABBIT) ||
                stack.is(ModItems.EERIE_MEAT.get());
    }
}
