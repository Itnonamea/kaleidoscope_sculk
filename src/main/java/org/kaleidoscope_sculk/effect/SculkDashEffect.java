package org.kaleidoscope_sculk.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;

public class SculkDashEffect extends MobEffect {

    public static final double SPEED_BONUS = 0.4D;
    public static final double ATTACK_SPEED_BONUS = 0.5D;
    private static final int CHECK_INTERVAL = 20;

    private static final ResourceLocation SPEED_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sculk_dash_speed");
    private static final ResourceLocation ATTACK_SPEED_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sculk_dash_attack_speed");

    private static final TagKey<Biome> SCULK_BIOMES =
            TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sculk_biomes"));

    public SculkDashEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x3B5E6A);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % CHECK_INTERVAL == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        if (isInSculkBiome(entity.level(), entity.blockPosition())) {
            applyModifier(entity, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER, SPEED_BONUS);
            applyModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER, ATTACK_SPEED_BONUS);
        } else {
            clearModifiers(entity);
        }
        return true;
    }

    private static void applyModifier(LivingEntity entity, Holder<Attribute> attribute,
                                      ResourceLocation modifierId, double bonus) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        if (instance.getModifier(modifierId) != null) return;
        instance.addTransientModifier(new AttributeModifier(
                modifierId, bonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }

    public static void clearModifiers(LivingEntity entity) {
        if (entity == null) return;
        removeModifier(entity, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER);
        removeModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER);
    }

    private static void removeModifier(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation modifierId) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && instance.getModifier(modifierId) != null) {
            instance.removeModifier(modifierId);
        }
    }

    private static boolean isInSculkBiome(Level level, BlockPos pos) {
        var biome = level.getBiome(pos);
        if (biome.is(SCULK_BIOMES)) return true;
        return biome.is(Biomes.DEEP_DARK);
    }
}
