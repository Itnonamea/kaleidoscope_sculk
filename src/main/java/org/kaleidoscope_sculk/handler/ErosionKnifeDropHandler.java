package org.kaleidoscope_sculk.handler;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    private static final List<Class<?>> CONVERTIBLE_ANIMALS = List.of(
            Pig.class,
            Sheep.class,
            Cow.class,
            Chicken.class
    );

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getSource().getEntity() instanceof Player player ? player : null;

        if (killer == null) return;

        ItemStack mainHand = killer.getMainHandItem();
        if (!mainHand.is(ModItems.EROSION_KITCHEN_KNIFE.get())) return;

        // 检查动物类型是否在转换列表中
        boolean isConvertible = false;
        for (Class<?> animalClass : CONVERTIBLE_ANIMALS) {
            if (animalClass.isInstance(entity)) {
                isConvertible = true;
                break;
            }
        }
        if (!isConvertible) return;

        if (!entity.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) entity.level();
            Vec3 deathPos = entity.position();

            for (int i = 0; i < 25; i++) {
                double offsetX = (serverLevel.random.nextDouble() - 0.5) * 1.5;
                double offsetY = serverLevel.random.nextDouble() * 1.2;
                double offsetZ = (serverLevel.random.nextDouble() - 0.5) * 1.5;

                float roll = serverLevel.random.nextFloat();
                serverLevel.sendParticles(
                        new SculkChargeParticleOptions(roll),
                        deathPos.x + offsetX,
                        deathPos.y + 0.5 + offsetY,
                        deathPos.z + offsetZ,
                        1,
                        0, 0, 0,
                        0.05
                );
            }

            // 2. 灵魂火焰粒子（向上飘散）
            for (int i = 0; i < 20; i++) {
                double offsetX = (serverLevel.random.nextDouble() - 0.5) * 1.2;
                double offsetZ = (serverLevel.random.nextDouble() - 0.5) * 1.2;
                double vy = serverLevel.random.nextDouble() * 0.15;

                serverLevel.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        deathPos.x + offsetX,
                        deathPos.y + 0.2,
                        deathPos.z + offsetZ,
                        1,
                        0, vy, 0,
                        0.02
                );
            }

            for (int i = 0; i < 15; i++) {
                double offsetX = (serverLevel.random.nextDouble() - 0.5) * 1.0;
                double offsetZ = (serverLevel.random.nextDouble() - 0.5) * 1.0;
                double vy = serverLevel.random.nextDouble() * 0.1;

                serverLevel.sendParticles(
                        ParticleTypes.SCULK_SOUL,
                        deathPos.x + offsetX,
                        deathPos.y + 0.3,
                        deathPos.z + offsetZ,
                        1,
                        0, vy, 0,
                        0.01
                );
            }

            for (int i = 0; i < 12; i++) {
                double angle = serverLevel.random.nextDouble() * 2 * Math.PI;
                double radius = 0.6;
                double x = deathPos.x + Math.cos(angle) * radius;
                double z = deathPos.z + Math.sin(angle) * radius;
                double y = deathPos.y + 0.3 + serverLevel.random.nextDouble() * 0.8;

                serverLevel.sendParticles(
                        ParticleTypes.SOUL,
                        x, y, z,
                        1, 0, 0, 0,
                        0.01
                );
            }

            for (int i = 0; i < 15; i++) {
                double angle = serverLevel.random.nextDouble() * 2 * Math.PI;
                double radius = 0.8;
                double x = deathPos.x + Math.cos(angle) * radius;
                double z = deathPos.z + Math.sin(angle) * radius;
                float roll = serverLevel.random.nextFloat();

                serverLevel.sendParticles(
                        new SculkChargeParticleOptions(roll),
                        x,
                        deathPos.y + 0.1,
                        z,
                        1,
                        0, 0.1, 0,
                        0.03
                );
            }

            serverLevel.playSound(null,
                    deathPos.x, deathPos.y, deathPos.z,
                    net.minecraft.sounds.SoundEvents.SCULK_BLOCK_SPREAD,
                    net.minecraft.sounds.SoundSource.PLAYERS,
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

        // 移除原有的肉类掉落物
        for (ItemEntity drop : meatDrops) {
            event.getDrops().remove(drop);
            drop.discard();
        }

        // 添加等量的幽寂肉
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

    /**
     * 判断是否为肉类物品
     */
    private static boolean isMeatItem(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.PORKCHOP) ||
                stack.is(net.minecraft.world.item.Items.COOKED_PORKCHOP) ||
                stack.is(net.minecraft.world.item.Items.MUTTON) ||
                stack.is(net.minecraft.world.item.Items.COOKED_MUTTON) ||
                stack.is(net.minecraft.world.item.Items.BEEF) ||
                stack.is(net.minecraft.world.item.Items.COOKED_BEEF) ||
                stack.is(net.minecraft.world.item.Items.CHICKEN) ||
                stack.is(net.minecraft.world.item.Items.COOKED_CHICKEN) ||
                stack.is(net.minecraft.world.item.Items.RABBIT) ||
                stack.is(net.minecraft.world.item.Items.COOKED_RABBIT) ||
                stack.is(ModItems.EERIE_MEAT.get());
    }
}