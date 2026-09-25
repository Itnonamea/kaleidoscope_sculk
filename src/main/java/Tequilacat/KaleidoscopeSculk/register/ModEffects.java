package Tequilacat.KaleidoscopeSculk.register;

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
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import Tequilacat.KaleidoscopeSculk.Kaleidoscope_sculk;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Kaleidoscope_sculk.MODID);

    public static final DeferredHolder<MobEffect, SonicWave> SONIC_WAVE =
            MOB_EFFECTS.register("sonic_wave", SonicWave::new);

    public static final DeferredHolder<MobEffect, Echo> ECHO =
            MOB_EFFECTS.register("echo", Echo::new);

    public static final DeferredHolder<MobEffect, Abyss> ABYSS =
            MOB_EFFECTS.register("abyss", Abyss::new);

    public static final DeferredHolder<MobEffect, SculkDash> SCULK_DASH =
            MOB_EFFECTS.register("sculk_dash", SculkDash::new);

    public static final DeferredHolder<MobEffect, SoulPower> SOUL_POWER =
            MOB_EFFECTS.register("soul_power", SoulPower::new);

    public static class Abyss extends MobEffect {
        public Abyss() {
            super(MobEffectCategory.BENEFICIAL, 0x1B0C36);
        }
    }

    public static class Echo extends MobEffect {
        public Echo() {
            super(MobEffectCategory.BENEFICIAL, 0x66CCFF);
        }
    }

    public static class SonicWave extends MobEffect {
        public SonicWave() {
            super(MobEffectCategory.BENEFICIAL, 0x5B6E6A);
        }
    }

    /**
     * Sculk Dash: increases movement and attack speed while in a sculk biome.
     */
    public static class SculkDash extends MobEffect {

        public static final double SPEED_BONUS = 0.4D;
        public static final double ATTACK_SPEED_BONUS = 0.5D;

        private static final int CHECK_INTERVAL = 20;

        private static final ResourceLocation SPEED_MODIFIER =
                ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sculk_dash_speed");
        private static final ResourceLocation ATTACK_SPEED_MODIFIER =
                ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sculk_dash_attack_speed");

        private static final TagKey<Biome> SCULK_BIOMES = TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sculk_biomes"));

        public SculkDash() {
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

        private static void removeModifier(LivingEntity entity, Holder<Attribute> attribute,
                                           ResourceLocation modifierId) {
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

    /**
     * Reaped Power: grants a flat attack damage bonus equal to 10% of the reaped target's max health.
     * <p>
     * The value varies per target, so it cannot be expressed as a declarative attribute modifier;
     * instead {@link #ensureModifier(LivingEntity)} keeps a dynamic modifier while the effect is active.
     */
    public static class SoulPower extends MobEffect {

        private static final int SYNC_INTERVAL = 20;

        private static final ResourceLocation ATTACK_DAMAGE_MODIFIER =
                ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "soul_power_attack_damage");

        private static final String BONUS_KEY = "kaleidoscope_sculk:soul_power_bonus";

        public SoulPower() {
            super(MobEffectCategory.BENEFICIAL, 0x7B4FBF);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            return duration % SYNC_INTERVAL == 0;
        }

        @Override
        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
            if (!entity.level().isClientSide) {
                // Modifiers are lost on death/respawn or dimension change; re-apply them periodically.
                ensureModifier(entity);
            }
            return true;
        }

        @Override
        public void removeAttributeModifiers(AttributeMap attributeMap) {
            super.removeAttributeModifiers(attributeMap);
            AttributeInstance instance = attributeMap.getInstance(Attributes.ATTACK_DAMAGE);
            if (instance != null) {
                instance.removeModifier(ATTACK_DAMAGE_MODIFIER);
            }
        }

        public static void setBonus(LivingEntity entity, double bonus) {
            entity.getPersistentData().putDouble(BONUS_KEY, bonus);
        }

        public static void ensureModifier(LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (instance == null) {
                return;
            }

            double bonus = entity.getPersistentData().getDouble(BONUS_KEY);
            AttributeModifier existing = instance.getModifier(ATTACK_DAMAGE_MODIFIER);

            if (bonus <= 0.0D) {
                if (existing != null) {
                    instance.removeModifier(ATTACK_DAMAGE_MODIFIER);
                }
                return;
            }

            if (existing != null && existing.amount() == bonus) {
                return;
            }
            if (existing != null) {
                instance.removeModifier(ATTACK_DAMAGE_MODIFIER);
            }
            instance.addTransientModifier(new AttributeModifier(
                    ATTACK_DAMAGE_MODIFIER, bonus, AttributeModifier.Operation.ADD_VALUE));
        }
    }
}
