package org.kaleidoscope_sculk.item;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.component.SoulSailData;
import org.kaleidoscope_sculk.register.ModDataComponents;
import org.kaleidoscope_sculk.register.ModItems;
import org.slf4j.Logger;

import java.util.List;

public class SoulSailItem extends Item implements Equipable {
    private static final Logger LOGGER = LogUtils.getLogger();

    // 经验等级阈值
    public static final int LEVEL_SOUL = 30;
    public static final int LEVEL_THOUSAND = 60;
    public static final int LEVEL_MYRIAD = 100;

    public enum SailType {
        SOUL("soul", LEVEL_SOUL, ChatFormatting.BLUE, "魂幡"),
        THOUSAND("thousand", LEVEL_THOUSAND, ChatFormatting.DARK_PURPLE, "千魂幡"),
        MYRIAD("myriad", LEVEL_MYRIAD, ChatFormatting.GOLD, "万魂幡");

        public final String name;
        public final int maxLevel;
        public final ChatFormatting color;
        public final String displayName;

        SailType(String name, int maxLevel, ChatFormatting color, String displayName) {
            this.name = name;
            this.maxLevel = maxLevel;
            this.color = color;
            this.displayName = displayName;
        }

        public static SailType fromName(String name) {
            for (SailType type : values()) {
                if (type.name.equals(name)) return type;
            }
            return SOUL;
        }
    }

    // ========== 实例字段 ==========
    private final SailType type;
    private final int maxLevel;
    private final int maxXp;

    public SoulSailItem(Properties properties, SailType type) {
        super(properties);
        this.type = type;
        this.maxLevel = type.maxLevel;
        this.maxXp = getTotalXpForLevel(maxLevel);
    }

    // ========== 经验等级计算工具方法（静态） ==========

    public static int getTotalXpForLevel(int level) {
        int total = 0;
        for (int i = 0; i < level; i++) {
            total += getXpNeededForLevel(i);
        }
        return total;
    }

    public static int getXpNeededForLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    public static int xpToLevel(int xpPoints) {
        int level = 0;
        int totalXp = 0;
        while (true) {
            int needed = getXpNeededForLevel(level);
            if (totalXp + needed > xpPoints) {
                break;
            }
            totalXp += needed;
            level++;
        }
        return level;
    }

    // ========== 实例方法（获取类型信息） ==========

    public SailType getType() {
        return type;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getMaxXp() {
        return maxXp;
    }

    public String getTypeName() {
        return type.displayName;
    }

    public ChatFormatting getColor() {
        return type.color;
    }

    // ========== Equipable 接口实现 ==========

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int storedXp = getStoredXp(stack);

        // 如果有经验 → 执行魂幡功能（取出/显示）
        if (storedXp > 0) {
            return useSoulSail(level, player, hand, stack);
        }

        // 无经验 → 尝试装备到头盔槽位
        EquipmentSlot slot = EquipmentSlot.HEAD;
        if (player.getItemBySlot(slot).isEmpty()) {
            player.setItemSlot(slot, stack.copyWithCount(1));
            stack.shrink(1);
            player.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0f, 1.0f);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    /**
     * 魂幡的核心使用逻辑（取出经验/显示状态）
     */
    private InteractionResultHolder<ItemStack> useSoulSail(Level level, Player player, InteractionHand hand, ItemStack stack) {
        int storedXp = getStoredXp(stack);
        int storedLevel = xpToLevel(storedXp);
        boolean isFull = isFullItem(stack);

        // Shift + 右键：全部取出
        if (player.isShiftKeyDown()) {
            if (storedXp > 0) {
                if (!level.isClientSide) {
                    int xpToGive = storedXp;
                    setStoredXp(stack, 0);
                    player.giveExperiencePoints(xpToGive);

                    if (isFull) {
                        ItemStack newStack = convertToNonFullItem(stack);
                        player.setItemInHand(hand, newStack);
                        stack = newStack;
                    }

                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
                    player.displayClientMessage(
                            Component.literal("§a已取出全部经验！"), true
                    );
                    return InteractionResultHolder.success(stack);
                }
                return InteractionResultHolder.success(stack);
            }
            player.displayClientMessage(
                    Component.literal("§c魂幡中没有经验"), true
            );
            return InteractionResultHolder.pass(stack);
        }

        // 右键：取出1级经验
        if (storedXp > 0) {
            if (!level.isClientSide) {
                int neededForNextLevel = getXpNeededForLevel(storedLevel);
                int toWithdraw = Math.min(neededForNextLevel, storedXp);

                setStoredXp(stack, storedXp - toWithdraw);
                player.giveExperiencePoints(toWithdraw);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
                player.displayClientMessage(
                        Component.literal("§a取出了 §e" + toWithdraw + " §a点经验"), true
                );

                if (isFull) {
                    ItemStack newStack = convertToNonFullItem(stack);
                    player.setItemInHand(hand, newStack);
                    stack = newStack;
                }
                return InteractionResultHolder.success(stack);
            }
            return InteractionResultHolder.success(stack);
        }

        // 显示当前存储状态
        if (!level.isClientSide) {
            if (isFull) {
                player.displayClientMessage(
                        Component.literal("§e满级" + type.displayName + " §7经验已满！"), true
                );
            } else {
                player.displayClientMessage(
                        Component.literal("§7" + type.displayName + "存储: §e" + storedXp + " §7/ §e" + maxXp + " §7点经验 (§e" + storedLevel + "§7级)"),
                        true
                );
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    // ========== DataComponent 辅助方法（静态） ==========

    public static SoulSailData getSailData(ItemStack stack) {
        SoulSailData data = stack.get(ModDataComponents.SOUL_SAIL_DATA.get());
        return data != null ? data : SoulSailData.DEFAULT;
    }

    public static void setSailData(ItemStack stack, SoulSailData data) {
        stack.set(ModDataComponents.SOUL_SAIL_DATA.get(), data);
    }

    public static int getStoredXp(ItemStack stack) {
        SoulSailData data = getSailData(stack);
        return data.storedXp();
    }

    public static int getStoredLevel(ItemStack stack) {
        return xpToLevel(getStoredXp(stack));
    }

    public static void setStoredXp(ItemStack stack, int xp) {
        SoulSailData data = getSailData(stack);
        setSailData(stack, data.withStoredXp(xp));
    }

    public static int getRotation(ItemStack stack) {
        SoulSailData data = getSailData(stack);
        return data.rotation();
    }

    public static void setRotation(ItemStack stack, int rotation) {
        SoulSailData data = getSailData(stack);
        setSailData(stack, data.withRotation(rotation));
    }

    public static void setSailData(ItemStack stack, SailType type, int rotation, int xp) {
        setSailData(stack, new SoulSailData(type.name, xp, rotation));
    }

    // ========== 获取物品类型（静态辅助） ==========

    public static SailType getSailTypeFromItem(ItemStack stack) {
        if (stack.getItem() instanceof SoulSailItem sailItem) {
            return sailItem.getType();
        }
        return SailType.SOUL;
    }

    public static int getMaxXpForItem(ItemStack stack) {
        if (stack.getItem() instanceof SoulSailItem sailItem) {
            return sailItem.getMaxXp();
        }
        return getTotalXpForLevel(LEVEL_SOUL);
    }

    public static int getMaxLevelForItem(ItemStack stack) {
        if (stack.getItem() instanceof SoulSailItem sailItem) {
            return sailItem.getMaxLevel();
        }
        return LEVEL_SOUL;
    }

    // ========== 物品类型判断 ==========

    public static boolean isFullItem(ItemStack stack) {
        return stack.is(ModItems.SOUL_SAIL_FULL.get()) ||
                stack.is(ModItems.THOUSAND_SOUL_SAIL_FULL.get()) ||
                stack.is(ModItems.MYRIAD_SOUL_SAIL_FULL.get());
    }

    public static boolean isNonFullItem(ItemStack stack) {
        return stack.is(ModItems.SOUL_SAIL.get()) ||
                stack.is(ModItems.THOUSAND_SOUL_SAIL.get()) ||
                stack.is(ModItems.MYRIAD_SOUL_SAIL.get());
    }

    // ========== 获取当前物品的满级对应物品 ==========

    public Item getFullVersion() {
        return switch (type) {
            case SOUL -> ModItems.SOUL_SAIL_FULL.get();
            case THOUSAND -> ModItems.THOUSAND_SOUL_SAIL_FULL.get();
            case MYRIAD -> ModItems.MYRIAD_SOUL_SAIL_FULL.get();
        };
    }

    public Item getNonFullVersion() {
        return switch (type) {
            case SOUL -> ModItems.SOUL_SAIL.get();
            case THOUSAND -> ModItems.THOUSAND_SOUL_SAIL.get();
            case MYRIAD -> ModItems.MYRIAD_SOUL_SAIL.get();
        };
    }

    // ========== 物品转换方法 ==========

    public static ItemStack convertToFullItem(ItemStack stack) {
        if (isFullItem(stack)) return stack;
        if (!(stack.getItem() instanceof SoulSailItem sailItem)) return stack;

        ItemStack newStack = new ItemStack(sailItem.getFullVersion());
        SoulSailData data = getSailData(stack);
        setSailData(newStack, data);
        return newStack;
    }

    public static ItemStack convertToNonFullItem(ItemStack stack) {
        if (!isFullItem(stack)) return stack;
        if (!(stack.getItem() instanceof SoulSailItem sailItem)) return stack;

        ItemStack newStack = new ItemStack(sailItem.getNonFullVersion());
        SoulSailData data = getSailData(stack);
        setSailData(newStack, data);
        return newStack;
    }

    // ========== 核心存储方法 ==========

    public boolean storeXp(ItemStack stack, int xpPoints) {
        if (isFullItem(stack)) return false;

        int currentXp = getStoredXp(stack);
        if (currentXp >= maxXp) return false;

        int remaining = maxXp - currentXp;
        if (remaining <= 0) return false;

        int absorbed = Math.min(xpPoints, remaining);
        int newXp = currentXp + absorbed;
        setStoredXp(stack, newXp);

        return newXp >= maxXp;
    }

    // ========== 物品方法 ==========

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.SOUL_SAND) || state.is(Blocks.SOUL_SOIL)) {
            return convertSoulBlock(level, pos, state, player, stack);
        }

        return InteractionResult.PASS;
    }

    private InteractionResult convertSoulBlock(Level level, BlockPos pos, BlockState state,
                                               Player player, ItemStack stack) {
        if (!level.isClientSide) {
            Block newBlock = state.is(Blocks.SOUL_SAND) ? Blocks.SAND : Blocks.DIRT;
            level.setBlock(pos, newBlock.defaultBlockState(), Block.UPDATE_ALL);

            if (level.random.nextFloat() < 0.3f) {
                ItemStack soulStack = new ItemStack(ModItems.SOUL.get());
                net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                        level,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        soulStack
                );
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }

            level.playSound(null, pos, SoundEvents.SOUL_ESCAPE.value(),
                    SoundSource.BLOCKS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        boolean isFull = isFullItem(stack);
        int storedXp = getStoredXp(stack);
        int storedLevel = xpToLevel(storedXp);
        int maxXp = this.maxXp;

        // 标题
        if (isFull) {
            tooltip.add(Component.literal(type.color + type.displayName + " §e✦ 满级 ✦"));
        } else {
            tooltip.add(Component.literal(type.color + "✦ " + type.displayName));
            String descKey = "item." + Kaleidoscope_sculk.MODID + ".soul_sail." + type.name + ".tooltip";
            tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
        }

        // 经验条
        float progress = (float) storedXp / maxXp;
        int bars = (int) (progress * 20);
        StringBuilder bar = new StringBuilder("§7[");
        String filledColor = switch (type) {
            case SOUL -> "§b";
            case THOUSAND -> "§5";
            case MYRIAD -> "§6";
        };

        if (isFull) {
            for (int i = 0; i < 20; i++) {
                bar.append("§e█");
            }
            bar.append("§7] §e已满级");
        } else {
            for (int i = 0; i < 20; i++) {
                if (i < bars) {
                    bar.append(filledColor).append("█");
                } else {
                    bar.append("§8░");
                }
            }
            bar.append("§7]");
        }
        tooltip.add(Component.literal(bar.toString()));

        // 经验信息
        if (!isFull) {
            String formattedXp = String.format("%,d", storedXp);
            String formattedMaxXp = String.format("%,d", maxXp);
            tooltip.add(Component.literal("§7经验等级: §e" + storedLevel + " §7/ §e" + maxLevel));
            tooltip.add(Component.literal("§7经验点数: §e" + formattedXp + " §7/ §e" + formattedMaxXp));
        }

        tooltip.add(Component.literal(""));

        // 操作提示
        if (isFull) {
            tooltip.add(Component.literal("§e右键 §7取1级  §eShift+右键 §7全取"));
        } else if (storedXp > 0) {
            tooltip.add(Component.literal("§e右键 §7取1级  §eShift+右键 §7全取"));
        }

        // 升级提示
        if (!isFull) {
            tooltip.add(Component.literal(""));
            int need = maxLevel - storedLevel;
            int needXp = maxXp - storedXp;
            tooltip.add(Component.literal("§7满级还需: §e" + need + " §7级 (§e" + String.format("%,d", needXp) + " §7点)"));
            if (type == SailType.MYRIAD) {
                tooltip.add(Component.literal("§6✦ 已到达最终形态"));
            } else {
                tooltip.add(Component.literal("§7满级后 + 8灵魂 → §e" + getNextTierName()));
            }
        } else {
            if (type == SailType.MYRIAD) {
                tooltip.add(Component.literal(""));
                tooltip.add(Component.literal("§6✦ 已到达最终形态"));
            } else {
                tooltip.add(Component.literal(""));
                tooltip.add(Component.literal("§7满级后 + 8灵魂 → §e" + getNextTierName()));
            }
        }
    }

    private String getNextTierName() {
        return switch (type) {
            case SOUL -> "千魂幡";
            case THOUSAND -> "万魂幡";
            case MYRIAD -> "最终形态";
        };
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isFullItem(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (isFullItem(stack)) {
            return Component.literal(type.color + type.displayName + " §e(满级)");
        }
        return Component.literal(type.color + type.displayName);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return super.getDescriptionId(stack) + "." + type.name;
    }
}