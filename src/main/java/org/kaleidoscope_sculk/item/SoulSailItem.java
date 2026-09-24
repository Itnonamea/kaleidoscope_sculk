package org.kaleidoscope_sculk.item;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Soul Sail: equipped in the head slot, grants 4 armor, has no durability and is unbreakable.
 * <p>
 * While equipped, when the mob under the crosshair is below 30% of its max health,
 * pressing the "Reap" key spends 50 + 2 x the target's current health in experience,
 * instantly kills the target and absorbs its power.
 */
public class SoulSailItem extends Item implements Equipable {

    public static final int BUFF_DURATION_TICKS = 20 * 60 * 10;

    public static final long COOLDOWN_TICKS = 20L * 60L * 10L;

    public static final double TARGET_RANGE = 12.0D;

    public static final double TARGET_HEALTH_THRESHOLD = 0.30D;

    public static final double XP_COST_BASE = 50.0D;

    public static final double XP_COST_PER_HEALTH = 2.0D;

    public static final double ABSORPTION_RATIO = 0.40D;

    public static final double ATTACK_DAMAGE_BONUS_RATIO = 0.10D;

    public SoulSailItem(Properties properties) {
        super(properties);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return this.swapWithEquipmentSlot(this, level, player, hand);
    }

    public static double attackDamageBonus(float targetMaxHealth) {
        return targetMaxHealth * ATTACK_DAMAGE_BONUS_RATIO;
    }

    public static boolean isValidTarget(LivingEntity target) {
        return target.isAlive()
                && target.getHealth() > 0.0F
                && target.getHealth() < target.getMaxHealth() * (float) TARGET_HEALTH_THRESHOLD;
    }

    public static int xpCost(LivingEntity target) {
        return (int) Math.round(XP_COST_BASE + XP_COST_PER_HEALTH * target.getHealth());
    }

    public static int totalXpForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        if (level <= 16) {
            return level * level + 6 * level;
        }
        if (level <= 31) {
            return (int) (2.5D * level * level - 40.5D * level + 360.0D);
        }
        return (int) (4.5D * level * level - 162.5D * level + 2220.0D);
    }

    public static int xpNeededForNextLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        }
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }

    public static int totalXpPoints(Player player) {
        int level = Math.max(0, player.experienceLevel);
        int partial = Math.round(player.experienceProgress * xpNeededForNextLevel(level));
        return totalXpForLevel(level) + Math.max(0, partial);
    }

    /**
     * Absorption level: vanilla Absorption grants 4 points per level, so pick the level closest
     * to 40% of the target's max health.
     */
    public static int absorptionAmplifier(float targetMaxHealth) {
        int levels = Math.round(targetMaxHealth * (float) ABSORPTION_RATIO / 4.0F);
        return Mth.clamp(levels - 1, 0, 254);
    }
}
