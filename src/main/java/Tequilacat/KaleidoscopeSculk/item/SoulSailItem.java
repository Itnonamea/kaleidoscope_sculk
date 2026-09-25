package Tequilacat.KaleidoscopeSculk.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import Tequilacat.KaleidoscopeSculk.config.ModConfigs;

import java.util.UUID;

/**
 * Soul Sail: equipped in the head slot, grants 4 armor, has no durability and is unbreakable.
 * <p>
 * While equipped, when the mob under the crosshair is below {@link #TARGET_HEALTH_THRESHOLD} health,
 * pressing the "Reap" key spends experience, instantly kills the target and absorbs its power.
 * Bosses and other players' pets are always ignored; extra entities can be excluded through the
 * {@code reaping.blacklist} config option.
 */
public class SoulSailItem extends Item implements Equipable {

    public static final int BUFF_DURATION_TICKS = 12000;
    public static final double TARGET_RANGE = 12.0D;
    public static final double TARGET_HEALTH_THRESHOLD = 0.30D;
    private static final double XP_COST_BASE = 50.0D;
    private static final double XP_COST_PER_HEALTH = 2.0D;
    private static final double ABSORPTION_RATIO = 0.40D;
    private static final double ATTACK_DAMAGE_BONUS_RATIO = 0.10D;

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

    public static long cooldownTicks() {
        return ModConfigs.REAP_COOLDOWN_SECONDS.get() * 20L;
    }

    public static boolean isValidTarget(LivingEntity target) {
        return target.isAlive()
                && target.getHealth() > 0.0F
                && target.getHealth() < target.getMaxHealth() * (float) TARGET_HEALTH_THRESHOLD;
    }

    /**
     * Full reaping check: health threshold, boss / other players' pet exclusion and the configured blacklist.
     */
    public static boolean canReap(Player reaper, LivingEntity target) {
        if (!isValidTarget(target)) {
            return false;
        }
        if (target.getType().is(Tags.EntityTypes.BOSSES)) {
            return false;
        }
        if (target instanceof OwnableEntity ownable) {
            UUID owner = ownable.getOwnerUUID();
            if (owner != null && !owner.equals(reaper.getUUID())) {
                return false;
            }
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        String idString = id.toString();
        for (String entry : ModConfigs.REAP_BLACKLIST.get()) {
            if (idString.equals(entry)) {
                return false;
            }
        }
        return true;
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
     * to {@link #ABSORPTION_RATIO} of the target's max health.
     */
    public static int absorptionAmplifier(float targetMaxHealth) {
        int levels = Math.round(targetMaxHealth * (float) ABSORPTION_RATIO / 4.0F);
        return Mth.clamp(levels - 1, 0, 254);
    }

    public static double attackDamageBonus(float targetMaxHealth) {
        return targetMaxHealth * ATTACK_DAMAGE_BONUS_RATIO;
    }
}
