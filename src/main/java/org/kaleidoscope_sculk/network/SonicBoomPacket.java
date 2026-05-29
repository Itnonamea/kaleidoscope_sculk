//完全由deepseek生成
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModEffects;

import java.util.Comparator;
import java.util.List;

public record SonicBoomPacket() implements CustomPacketPayload {

    public static final Type<SonicBoomPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sonic_boom"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SonicBoomPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {},
            buf -> new SonicBoomPacket()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            // 服务端检查：有声波效果 + 空手
            if (player.hasEffect(ModEffects.SONIC_WAVE.getDelegate()) && player.getMainHandItem().isEmpty()) {
                shootSonicWave(player);
            }
        });
    }

    private static void shootSonicWave(ServerPlayer player) {
        ServerLevel level = player.serverLevel();

        // 获取当前效果等级
        MobEffectInstance currentEffect = player.getEffect(ModEffects.SONIC_WAVE.getDelegate());
        if (currentEffect == null) return;

        int currentAmplifier = currentEffect.getAmplifier();
        int currentDuration = currentEffect.getDuration();

        // 发射声波（伤害随等级提升）
        float baseDamage = 10.0f + (currentAmplifier * 2.0f);
        // 穿透衰减：每穿透一个实体减少的伤害比例
        float damageDecay = 0.3f; // 每个实体减少30%伤害

        Vec3 startPos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        double range = 15.0;
        double radius = 0.5; // 声波射线半径

        // 获取射线路径上的所有实体
        List<EntityHit> hits = rayTraceEntities(player, startPos, lookVec, range, radius);

        // 记录已生成粒子的位置（避免重复）
        java.util.Set<Integer> particlePositions = new java.util.HashSet<>();

        // 按距离排序，依次造成伤害
        hits.sort(Comparator.comparingDouble(h -> h.distance));

        for (int i = 0; i < hits.size(); i++) {
            EntityHit hit = hits.get(i);
            float currentDamage = baseDamage * (float) Math.pow(1.0f - damageDecay, i);

            if (currentDamage <= 0.5f) break; // 伤害太低，停止穿透

            // 生成粒子路径到该实体
            Vec3 targetPos = hit.entity.getEyePosition();
            Vec3 direction = targetPos.subtract(startPos).normalize();
            double distance = startPos.distanceTo(targetPos);
            int steps = Mth.floor(distance) + 7;

            for (int j = 1; j < steps; ++j) {
                int stepKey = (int) (j / 2.0); // 简化位置去重
                if (!particlePositions.contains(stepKey)) {
                    Vec3 particlePos = startPos.add(direction.scale(j));
                    level.sendParticles(ParticleTypes.SONIC_BOOM,
                            particlePos.x, particlePos.y, particlePos.z,
                            1, 0.0, 0.0, 0.0, 0.0);
                    particlePositions.add(stepKey);
                }
            }

            // 造成伤害
            hit.entity.hurt(level.damageSources().sonicBoom(player), currentDamage);
        }

        // 如果没有命中任何实体，生成射线方向的粒子效果
        if (hits.isEmpty()) {
            for (int j = 1; j <= 20; j++) {
                Vec3 particlePos = startPos.add(lookVec.scale(j * (range / 20)));
                level.sendParticles(ParticleTypes.SONIC_BOOM,
                        particlePos.x, particlePos.y, particlePos.z,
                        1, 0.0, 0.0, 0.0, 0.0);
            }
        }

        // 播放音效
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0f, 1.0f);

        // 降低一级效果
        if (currentAmplifier > 0) {
            player.removeEffect(ModEffects.SONIC_WAVE.getDelegate());
            player.addEffect(new MobEffectInstance(ModEffects.SONIC_WAVE.getDelegate(), currentDuration, currentAmplifier - 1));
        } else {
            player.removeEffect(ModEffects.SONIC_WAVE.getDelegate());
        }
    }

    /**
     * 射线检测所有实体（支持穿透）
     * @param player 玩家
     * @param startPos 起始位置
     * @param direction 方向
     * @param range 范围
     * @param radius 射线半径
     * @return 按距离排序的命中实体列表
     */
    private static List<EntityHit> rayTraceEntities(Player player, Vec3 startPos, Vec3 direction, double range, double radius) {
        List<EntityHit> hits = new java.util.ArrayList<>();

        // 获取范围内的所有实体
        AABB searchBox = player.getBoundingBox().inflate(range);
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, searchBox,
                e -> e != player && e.isAlive());

        Vec3 endPos = startPos.add(direction.scale(range));

        for (LivingEntity entity : entities) {
            // 获取实体的边界框
            AABB entityBox = entity.getBoundingBox();

            // 检查射线是否与实体边界框相交（带半径的射线检测）
            if (rayIntersectsAABB(startPos, endPos, entityBox, radius)) {
                // 计算交点到起点的距离
                Vec3 intersectPoint = getClosestIntersection(startPos, endPos, entityBox);
                if (intersectPoint != null) {
                    double distance = startPos.distanceTo(intersectPoint);
                    if (distance <= range) {
                        hits.add(new EntityHit(entity, distance));
                    }
                }
            }
        }

        // 按距离排序
        hits.sort(Comparator.comparingDouble(h -> h.distance));
        return hits;
    }

    /**
     * 检查射线是否与 AABB 相交（考虑半径）
     */
    private static boolean rayIntersectsAABB(Vec3 start, Vec3 end, AABB aabb, double radius) {
        // 将 AABB 向外扩展半径
        AABB expandedAABB = aabb.inflate(radius);
        return expandedAABB.intersects(start, end);
    }

    /**
     * 获取射线与 AABB 的最近交点
     */
    private static Vec3 getClosestIntersection(Vec3 start, Vec3 end, AABB aabb) {
        Vec3 dir = end.subtract(start);
        double tMin = 0;
        double tMax = 1;

        // X 轴
        if (Math.abs(dir.x) < 1e-8) {
            if (start.x < aabb.minX || start.x > aabb.maxX) return null;
        } else {
            double t1 = (aabb.minX - start.x) / dir.x;
            double t2 = (aabb.maxX - start.x) / dir.x;
            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }
            if (t1 > tMin) tMin = t1;
            if (t2 < tMax) tMax = t2;
            if (tMin > tMax) return null;
        }

        // Y 轴
        if (Math.abs(dir.y) < 1e-8) {
            if (start.y < aabb.minY || start.y > aabb.maxY) return null;
        } else {
            double t1 = (aabb.minY - start.y) / dir.y;
            double t2 = (aabb.maxY - start.y) / dir.y;
            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }
            if (t1 > tMin) tMin = t1;
            if (t2 < tMax) tMax = t2;
            if (tMin > tMax) return null;
        }

        // Z 轴
        if (Math.abs(dir.z) < 1e-8) {
            if (start.z < aabb.minZ || start.z > aabb.maxZ) return null;
        } else {
            double t1 = (aabb.minZ - start.z) / dir.z;
            double t2 = (aabb.maxZ - start.z) / dir.z;
            if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }
            if (t1 > tMin) tMin = t1;
            if (t2 < tMax) tMax = t2;
            if (tMin > tMax) return null;
        }

        // 返回交点位置
        double t = tMin;
        if (t < 0) t = 0;
        if (t > 1) return null;
        return start.add(dir.scale(t));
    }

    /**
     * 命中实体记录类
     */
    private static class EntityHit {
        final LivingEntity entity;
        final double distance;

        EntityHit(LivingEntity entity, double distance) {
            this.entity = entity;
            this.distance = distance;
        }
    }
}