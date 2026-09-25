package Tequilacat.KaleidoscopeSculk.network;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import Tequilacat.KaleidoscopeSculk.Kaleidoscope_sculk;
import Tequilacat.KaleidoscopeSculk.config.ModConfigs;
import Tequilacat.KaleidoscopeSculk.item.SoulSailItem;
import Tequilacat.KaleidoscopeSculk.register.ModEffects;
import Tequilacat.KaleidoscopeSculk.register.ModItems;
import Tequilacat.KaleidoscopeSculk.register.ModRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Client-to-server skill packets; all validation runs on the server.
 */
public final class ModPackets {

    private ModPackets() {
    }

    public record SonicBoom() implements CustomPacketPayload {

        public static final Type<SonicBoom> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sonic_boom"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SonicBoom> CODEC = StreamCodec.of(
                (buf, packet) -> {},
                buf -> new SonicBoom()
        );

        private static final String COOLDOWN_KEY = "kaleidoscope_sculk:sonic_boom_cooldown";
        private static final String BURST_COUNTER_KEY = "kaleidoscope_sculk:sonic_boom_counter";
        private static final String BURST_TIMESTAMP_KEY = "kaleidoscope_sculk:sonic_boom_timestamp";
        private static final long COOLDOWN_TICKS = 20L;
        private static final long COUNTER_EXPIRE_TICKS = 200L;
        private static final int NORMAL_SHOTS_BEFORE_BURST = 4;
        private static final int BURST_DIRECTIONS = 8;
        private static final int DURABILITY_COST = 50;
        private static final double BASE_DAMAGE = 20.0D;
        private static final double MAX_HEALTH_DAMAGE_RATIO = 0.05D;
        private static final double RANGE = 35.0D;
        private static final double RADIUS = 2.0D;

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public void handle(IPayloadContext context) {
            context.enqueueWork(() -> {
                if (!(context.player() instanceof ServerPlayer player)) {
                    return;
                }

                boolean holdingSilentKnife = player.getMainHandItem().is(ModItems.SILENT_KITCHEN_KNIFE.get());
                if (!holdingSilentKnife && !player.hasEffect(ModEffects.SONIC_WAVE.getDelegate())) {
                    return;
                }

                long now = player.level().getGameTime();
                long lastUse = player.getPersistentData().getLong(COOLDOWN_KEY);
                if (now - lastUse < COOLDOWN_TICKS) {
                    return;
                }
                player.getPersistentData().putLong(COOLDOWN_KEY, now);

                // The streak counter only applies to the Silent Kitchen Knife; Sonic Wave buff attacks stay linear and are not counted.
                boolean radialBurst = false;
                if (holdingSilentKnife) {
                    long lastShot = player.getPersistentData().getLong(BURST_TIMESTAMP_KEY);
                    if (now - lastShot > COUNTER_EXPIRE_TICKS) {
                        player.getPersistentData().putInt(BURST_COUNTER_KEY, 0);
                    }

                    int shotCount = player.getPersistentData().getInt(BURST_COUNTER_KEY) + 1;
                    radialBurst = shotCount > NORMAL_SHOTS_BEFORE_BURST;
                    player.getPersistentData().putInt(BURST_COUNTER_KEY, radialBurst ? 0 : shotCount);
                    player.getPersistentData().putLong(BURST_TIMESTAMP_KEY, now);

                    player.getMainHandItem().hurtAndBreak(DURABILITY_COST, player, EquipmentSlot.MAINHAND);
                    player.swing(InteractionHand.MAIN_HAND, true);
                }

                shootSonicWave(player, holdingSilentKnife, radialBurst);
            });
        }

        /** Clears the sonic streak counter (called by the item-switch event). */
        public static void resetStreak(Player player) {
            player.getPersistentData().remove(BURST_COUNTER_KEY);
            player.getPersistentData().remove(BURST_TIMESTAMP_KEY);
        }

        private static void shootSonicWave(ServerPlayer player, boolean holdingSilentKnife, boolean radialBurst) {
            ServerLevel level = player.serverLevel();

            MobEffectInstance currentEffect = player.getEffect(ModEffects.SONIC_WAVE.getDelegate());

            int currentAmplifier;
            int currentDuration;
            if (holdingSilentKnife) {
                currentAmplifier = currentEffect != null ? currentEffect.getAmplifier() : 0;
                currentDuration = currentEffect != null ? currentEffect.getDuration() : 0;
            } else {
                if (currentEffect == null) return;
                currentAmplifier = currentEffect.getAmplifier();
                currentDuration = currentEffect.getDuration();
            }

            Vec3 startPos = player.getEyePosition();
            // No target locking: normal attacks fire along the crosshair; the radial burst uses fixed directions and does not lock on either.
            double range = RANGE;
            double radius = RADIUS;

            if (radialBurst) {
                fireRadialBurst(player, level, startPos, range, radius);
            } else {
                fireStraightBeam(player, level, startPos, player.getLookAngle(), range, radius);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0f, 1.0f);

            if (!holdingSilentKnife) {
                if (currentAmplifier > 0) {
                    player.removeEffect(ModEffects.SONIC_WAVE.getDelegate());
                    player.addEffect(new MobEffectInstance(ModEffects.SONIC_WAVE.getDelegate(),
                            currentDuration, currentAmplifier - 1));
                } else {
                    player.removeEffect(ModEffects.SONIC_WAVE.getDelegate());
                }
            }
        }

        private static void fireStraightBeam(ServerPlayer player, ServerLevel level, Vec3 startPos,
                                             Vec3 lookVec, double range, double radius) {
            List<EntityHit> hits = rayTraceEntities(player, startPos, lookVec, range, radius);
            int lastParticleStep = -1;

            for (EntityHit hit : hits) {
                Vec3 targetPos = hit.entity().getEyePosition();
                Vec3 direction = targetPos.subtract(startPos).normalize();
                double distance = startPos.distanceTo(targetPos);
                int steps = Mth.floor(distance) + 7;

                double stepX = direction.x;
                double stepY = direction.y;
                double stepZ = direction.z;
                for (int j = 1; j < steps; ++j) {
                    int stepKey = j >> 1;
                    if (stepKey <= lastParticleStep) continue;
                    level.sendParticles(ParticleTypes.SONIC_BOOM,
                            startPos.x + stepX * j, startPos.y + stepY * j, startPos.z + stepZ * j,
                            1, 0.0, 0.0, 0.0, 0.0);
                    lastParticleStep = stepKey;
                }

                damageEntity(player, level, hit.entity());
            }

            if (hits.isEmpty()) {
                drawBeamParticles(level, startPos, lookVec, range);
            }
        }

        /** Fires one beam in each of the 8 fixed directions around the user; each target is damaged only once. */
        private static void fireRadialBurst(ServerPlayer player, ServerLevel level, Vec3 startPos,
                                            double range, double radius) {
            Set<LivingEntity> damaged = new HashSet<>();
            for (int i = 0; i < BURST_DIRECTIONS; i++) {
                double angle = Math.toRadians(i * (360.0 / BURST_DIRECTIONS));
                Vec3 direction = new Vec3(-Math.sin(angle), 0.0, Math.cos(angle));
                drawBeamParticles(level, startPos, direction, range);
                for (EntityHit hit : rayTraceEntities(player, startPos, direction, range, radius)) {
                    if (damaged.add(hit.entity())) {
                        damageEntity(player, level, hit.entity());
                    }
                }
            }
        }

        private static void damageEntity(ServerPlayer player, ServerLevel level, LivingEntity target) {
            float damage = (float) BASE_DAMAGE + target.getMaxHealth() * (float) MAX_HEALTH_DAMAGE_RATIO;
            DamageSource source = level.damageSources().sonicBoom(player);
            if (target.hurt(source, damage)) {
                // Inherit the held item's enchantments (e.g. Fire Aspect, Bane of Arthropods, Thorns) without
                // their damage bonuses; Looting and smelting drops are already handled by the loot table through
                // the player being the attacking entity.
                EnchantmentHelper.doPostAttackEffects(level, target, source);
            }
        }

        private static void drawBeamParticles(ServerLevel level, Vec3 startPos, Vec3 direction, double range) {
            for (int j = 1; j <= (int) range; j++) {
                level.sendParticles(ParticleTypes.SONIC_BOOM,
                        startPos.x + direction.x * j,
                        startPos.y + direction.y * j,
                        startPos.z + direction.z * j,
                        1, 0.0, 0.0, 0.0, 0.0);
            }
        }

        private static List<EntityHit> rayTraceEntities(Player player, Vec3 startPos, Vec3 direction,
                                                        double range, double radius) {
            List<EntityHit> hits = new ArrayList<>();

            AABB searchBox = player.getBoundingBox().inflate(range);
            List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, searchBox,
                    e -> e != player && e.isAlive() && !isTamedPet(e));

            Vec3 endPos = startPos.add(direction.scale(range));

            for (LivingEntity entity : entities) {
                // Both the intersection test and clipping use the inflated box so that radius is respected.
                // (The radial beams are horizontal from eye height; short mobs would be missed with the raw box.)
                AABB hitBox = entity.getBoundingBox().inflate(radius);

                if (hitBox.intersects(startPos, endPos)) {
                    Vec3 intersectPoint = getClosestIntersection(startPos, endPos, hitBox);
                    if (intersectPoint != null) {
                        double distance = startPos.distanceTo(intersectPoint);
                        if (distance <= range) {
                            hits.add(new EntityHit(entity, distance));
                        }
                    }
                }
            }

            hits.sort(Comparator.comparingDouble(EntityHit::distance));
            return hits;
        }

        private static Vec3 getClosestIntersection(Vec3 start, Vec3 end, AABB aabb) {
            Vec3 dir = end.subtract(start);
            double tMin = 0;
            double tMax = 1;

            if (Math.abs(dir.x) < 1e-8) {
                if (start.x < aabb.minX || start.x > aabb.maxX) return null;
            } else {
                double t1 = (aabb.minX - start.x) / dir.x;
                double t2 = (aabb.maxX - start.x) / dir.x;
                if (t1 > t2) {
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }
                if (t1 > tMin) tMin = t1;
                if (t2 < tMax) tMax = t2;
                if (tMin > tMax) return null;
            }

            if (Math.abs(dir.y) < 1e-8) {
                if (start.y < aabb.minY || start.y > aabb.maxY) return null;
            } else {
                double t1 = (aabb.minY - start.y) / dir.y;
                double t2 = (aabb.maxY - start.y) / dir.y;
                if (t1 > t2) {
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }
                if (t1 > tMin) tMin = t1;
                if (t2 < tMax) tMax = t2;
                if (tMin > tMax) return null;
            }

            if (Math.abs(dir.z) < 1e-8) {
                if (start.z < aabb.minZ || start.z > aabb.maxZ) return null;
            } else {
                double t1 = (aabb.minZ - start.z) / dir.z;
                double t2 = (aabb.maxZ - start.z) / dir.z;
                if (t1 > t2) {
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }
                if (t1 > tMin) tMin = t1;
                if (t2 < tMax) tMax = t2;
                if (tMin > tMax) return null;
            }

            double t = tMin;
            if (t < 0) t = 0;
            if (t > 1) return null;
            return start.add(dir.scale(t));
        }

        private record EntityHit(LivingEntity entity, double distance) {
        }

        private static boolean isTamedPet(LivingEntity entity) {
            if (entity instanceof TamableAnimal tamable && tamable.isTame()) {
                return true;
            }
            return entity instanceof OwnableEntity ownable && ownable.getOwnerUUID() != null;
        }
    }

    public record SoulSailAbsorb() implements CustomPacketPayload {

        public static final Type<SoulSailAbsorb> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "soul_sail_absorb"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SoulSailAbsorb> CODEC = StreamCodec.of(
                (buf, packet) -> {},
                buf -> new SoulSailAbsorb()
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public void handle(IPayloadContext context) {
            context.enqueueWork(() -> {
                if (!(context.player() instanceof ServerPlayer player)) {
                    return;
                }

                ServerLevel level = player.serverLevel();

                ItemStack sail = player.getItemBySlot(EquipmentSlot.HEAD);
                if (!sail.is(ModItems.SOUL_SAIL.get())) {
                    return;
                }

                // Cooldown check (the cooldown is bound to the item, not the player)
                long now = level.getGameTime();
                Long readyAt = sail.get(ModRegistries.SOUL_SAIL_COOLDOWN.get());
                if (readyAt != null && now < readyAt) {
                    player.displayClientMessage(Component.translatable(
                            "item.kaleidoscope_sculk.soul_sail.cooldown", (readyAt - now + 19L) / 20L), true);
                    return;
                }

                LivingEntity target = findTarget(player);
                if (target == null) {
                    player.displayClientMessage(Component.translatable(
                            "item.kaleidoscope_sculk.soul_sail.no_target"), true);
                    return;
                }

                // Creative players reap for free: no experience check and no experience cost.
                boolean creative = player.isCreative();
                int cost = SoulSailItem.xpCost(target);
                if (!creative && SoulSailItem.totalXpPoints(player) < cost) {
                    player.displayClientMessage(Component.translatable(
                            "item.kaleidoscope_sculk.soul_sail.not_enough_xp", cost), true);
                    return;
                }

                reap(player, level, target, cost, !creative);
                sail.set(ModRegistries.SOUL_SAIL_COOLDOWN.get(), now + SoulSailItem.cooldownTicks());
            });
        }

        private static LivingEntity findTarget(ServerPlayer player) {
            double range = SoulSailItem.TARGET_RANGE;
            Vec3 start = player.getEyePosition();
            Vec3 end = start.add(player.getLookAngle().scale(range));

            AABB searchBox = player.getBoundingBox().inflate(range + 1.0D);
            LivingEntity best = null;
            double bestDistance = Double.MAX_VALUE;

            for (LivingEntity candidate : player.level().getEntitiesOfClass(LivingEntity.class, searchBox,
                    e -> e != player && !(e instanceof Player) && SoulSailItem.canReap(player, e))) {
                Optional<Vec3> hit = candidate.getBoundingBox().inflate(0.3D).clip(start, end);
                if (hit.isEmpty()) {
                    continue;
                }
                double distance = start.distanceToSqr(hit.get());
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = candidate;
                }
            }

            return best;
        }

        private static void reap(ServerPlayer player, ServerLevel level, LivingEntity target,
                                 int xpCost, boolean chargeXp) {
            float targetMaxHealth = target.getMaxHealth();
            int absorptionAmplifier = SoulSailItem.absorptionAmplifier(targetMaxHealth);

            if (chargeXp) {
                player.giveExperiencePoints(-xpCost);
            }

            // Kill directly and credit the player (keeps drops and XP)
            target.setLastHurtByPlayer(player);
            target.hurt(level.damageSources().playerAttack(player), Float.MAX_VALUE);
            if (target.isAlive()) {
                target.kill();
            }

            // Absorb its power: absorption equal to 40% of the target's max health + a flat damage bonus equal to 10% of its max health
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                    SoulSailItem.BUFF_DURATION_TICKS, absorptionAmplifier));
            player.addEffect(new MobEffectInstance(ModEffects.SOUL_POWER.getDelegate(),
                    SoulSailItem.BUFF_DURATION_TICKS, 0));
            ModEffects.SoulPower.setBonus(player, SoulSailItem.attackDamageBonus(targetMaxHealth));
            ModEffects.SoulPower.ensureModifier(player);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 1.0F, 0.7F);
            level.sendParticles(ParticleTypes.SCULK_SOUL,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                    24, 0.3D, 0.5D, 0.3D, 0.05D);
        }
    }
}
