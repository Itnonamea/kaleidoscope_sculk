package org.kaleidoscope_sculk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;

public class SculkDashEffect extends MobEffect {

    private static final String SPEED_MODIFIER_ID = "sculk_dash_speed";
    private static final TagKey<net.minecraft.world.level.biome.Biome> SCULK_BIOMES =
            TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sculk_biomes"));

    public SculkDashEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x3B5E6A);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        Level level = entity.level();
        BlockPos pos = entity.blockPosition();

        // 检查是否在幽匿相关群系中
        boolean isInSculkBiome = isInSculkBiome(level, pos);

        AttributeInstance attributeInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeInstance == null) return true;

        ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, SPEED_MODIFIER_ID);

        if (isInSculkBiome) {
            double speedBonus = 0.3 * (amplifier + 1);

            attributeInstance.removeModifier(modifierId);
            attributeInstance.addTransientModifier(
                    new AttributeModifier(
                            modifierId,
                            speedBonus,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            );
        } else {
            attributeInstance.removeModifier(modifierId);
        }

        return true;
    }

    /**
     * 判断是否在幽匿相关群系中
     */
    private boolean isInSculkBiome(Level level, BlockPos pos) {
        var biome = level.getBiome(pos);

        if (biome.is(SCULK_BIOMES)) return true;

        return biome.is(Biomes.DEEP_DARK);
    }
}