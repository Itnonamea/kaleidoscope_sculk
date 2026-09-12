package org.kaleidoscope_sculk.network;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModEffects;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record SonicBoomPacket() implements CustomPacketPayload {

    public static final Type<SonicBoomPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sonic_boom"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SonicBoomPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {},
            buf -> new SonicBoomPacket()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static final String COOLDOWN_KEY = "kaleidoscope_sculk:sonic_boom_cooldown";
    private static final long COOLDOWN_TICKS = 20L;

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

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
            shootSonicWave(player, holdingSilentKnife);
        });
    }

    private static void shootSonicWave(ServerPlayer player, boolean holdingSilentKnife) {
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

        float baseDamage = 10.0f + (currentAmplifier * 2.0f);
        
        float damageDecay = 0.3f; 

        Vec3 startPos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        double range = 15.0;
        double radius = 0.5; 

        
        List<EntityHit> hits = rayTraceEntities(player, startPos, lookVec, range, radius);

        int lastParticleStep = -1;

        for (int i = 0; i < hits.size(); i++) {
            EntityHit hit = hits.get(i);
            float currentDamage = baseDamage * (float) Math.pow(1.0f - damageDecay, i);

            if (currentDamage <= 0.5f) break; 

            
            Vec3 targetPos = hit.entity().getEyePosition();
            Vec3 direction = targetPos.subtract(startPos).normalize();
            double distance = startPos.distanceTo(targetPos);
            int steps = Mth.floor(distance) + 7;

            for (int j = 1; j < steps; ++j) {
                int stepKey = (int) (j / 2.0); 
                if (stepKey <= lastParticleStep) continue;
                Vec3 particlePos = startPos.add(direction.scale(j));
                level.sendParticles(ParticleTypes.SONIC_BOOM,
                        particlePos.x, particlePos.y, particlePos.z,
                        1, 0.0, 0.0, 0.0, 0.0);
                lastParticleStep = stepKey;
            }

            
            hit.entity().hurt(level.damageSources().sonicBoom(player), currentDamage);
        }

        
        if (hits.isEmpty()) {
            for (int j = 1; j <= 20; j++) {
                Vec3 particlePos = startPos.add(lookVec.scale(j * (range / 20)));
                level.sendParticles(ParticleTypes.SONIC_BOOM,
                        particlePos.x, particlePos.y, particlePos.z,
                        1, 0.0, 0.0, 0.0, 0.0);
            }
        }

        
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0f, 1.0f);

        
        if (!holdingSilentKnife) {
            if (currentAmplifier > 0) {
                player.removeEffect(ModEffects.SONIC_WAVE.getDelegate());
                player.addEffect(new MobEffectInstance(ModEffects.SONIC_WAVE.getDelegate(), currentDuration, currentAmplifier - 1));
            } else {
                player.removeEffect(ModEffects.SONIC_WAVE.getDelegate());
            }
        }
    }

    
    private static List<EntityHit> rayTraceEntities(Player player, Vec3 startPos, Vec3 direction, double range, double radius) {
        List<EntityHit> hits = new ArrayList<>();

        
        AABB searchBox = player.getBoundingBox().inflate(range);
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, searchBox,
                e -> e != player && e.isAlive());

        Vec3 endPos = startPos.add(direction.scale(range));

        for (LivingEntity entity : entities) {
            
            AABB entityBox = entity.getBoundingBox();

            
            if (rayIntersectsAABB(startPos, endPos, entityBox, radius)) {
                
                Vec3 intersectPoint = getClosestIntersection(startPos, endPos, entityBox);
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

    
    private static boolean rayIntersectsAABB(Vec3 start, Vec3 end, AABB aabb, double radius) {
        
        AABB expandedAABB = aabb.inflate(radius);
        return expandedAABB.intersects(start, end);
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
}
