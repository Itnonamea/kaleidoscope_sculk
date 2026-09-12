package org.kaleidoscope_sculk.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.kaleidoscope_sculk.component.SoulSailData;
import org.kaleidoscope_sculk.register.ModDataComponents;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SoulSailItem extends Item implements Equipable {

    public static final int LEVEL_SOUL = 30;
    public static final int LEVEL_THOUSAND = 60;
    public static final int LEVEL_MYRIAD = 100;

    private static final int BAR_SEGMENTS = 20;
    private static final float SOUL_DROP_CHANCE = 0.3f;

    private static Map<SailType, String[]> progressBars;

    private static String[] progressBars(SailType type) {
        Map<SailType, String[]> cache = progressBars;
        if (cache == null) {
            cache = new EnumMap<>(SailType.class);
            for (SailType sailType : SailType.values()) {
                String[] bars = new String[BAR_SEGMENTS + 1];
                for (int filled = 0; filled <= BAR_SEGMENTS; filled++) {
                    StringBuilder builder = new StringBuilder(BAR_SEGMENTS * 3 + 4);
                    builder.append("§7[");
                    for (int i = 0; i < BAR_SEGMENTS; i++) {
                        builder.append(i < filled ? sailType.barColor + "█" : "§8░");
                    }
                    bars[filled] = builder.append("§7]").toString();
                }
                cache.put(sailType, bars);
            }
            progressBars = cache;
        }
        return cache.get(type);
    }

    private final SailType type;
    private final int maxLevel;
    private final int maxXp;

    public SoulSailItem(Properties properties, SailType type) {
        super(properties);
        this.type = type;
        this.maxLevel = type.maxLevel;
        this.maxXp = getTotalXpForLevel(this.maxLevel);
    }

    
    
    

    private static final int MAX_CACHED_LEVEL = 128;

    private static final int[] TOTAL_XP_BY_LEVEL = buildTotalXpTable();

    private static int[] buildTotalXpTable() {
        int[] table = new int[MAX_CACHED_LEVEL + 1];
        int total = 0;
        for (int i = 0; i < MAX_CACHED_LEVEL; i++) {
            total += getXpNeededForLevel(i);
            table[i + 1] = total;
        }
        return table;
    }

    public static int getTotalXpForLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        if (level <= MAX_CACHED_LEVEL) {
            return TOTAL_XP_BY_LEVEL[level];
        }

        int total = TOTAL_XP_BY_LEVEL[MAX_CACHED_LEVEL];
        for (int i = MAX_CACHED_LEVEL; i < level; i++) {
            total += getXpNeededForLevel(i);
        }
        return total;
    }

    public static int getXpNeededForLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        }
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }

    public static int xpToLevel(int xpPoints) {
        if (xpPoints <= 0) {
            return 0;
        }

        final int[] table = TOTAL_XP_BY_LEVEL;
        if (xpPoints >= table[MAX_CACHED_LEVEL]) {
            int level = MAX_CACHED_LEVEL;
            int total = table[MAX_CACHED_LEVEL];
            while (total + getXpNeededForLevel(level) <= xpPoints) {
                total += getXpNeededForLevel(level);
                level++;
            }
            return level;
        }

        int lo = 0;
        int hi = MAX_CACHED_LEVEL;
        while (lo < hi) {
            int mid = (lo + hi + 1) >>> 1;
            if (table[mid] <= xpPoints) {
                lo = mid;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }

    
    
    

    public static SoulSailData getSailData(ItemStack stack) {
        SoulSailData data = stack.get(ModDataComponents.SOUL_SAIL_DATA.get());
        return data != null ? data : SoulSailData.DEFAULT;
    }

    public static void setSailData(ItemStack stack, SoulSailData data) {
        stack.set(ModDataComponents.SOUL_SAIL_DATA.get(), data);
    }

    public static void setSailData(ItemStack stack, SailType type, int rotation, int xp) {
        setSailData(stack, new SoulSailData(type.name, xp, rotation));
    }

    public static int getStoredXp(ItemStack stack) {
        return getSailData(stack).storedXp();
    }

    public static int getStoredLevel(ItemStack stack) {
        return xpToLevel(getStoredXp(stack));
    }

    public static void setStoredXp(ItemStack stack, int xp) {
        setSailData(stack, getSailData(stack).withStoredXp(xp));
    }

    public static int getRotation(ItemStack stack) {
        return getSailData(stack).rotation();
    }

    public static void setRotation(ItemStack stack, int rotation) {
        setSailData(stack, getSailData(stack).withRotation(rotation));
    }

    public static SailType getSailTypeFromItem(ItemStack stack) {
        return stack.getItem() instanceof SoulSailItem sailItem ? sailItem.getType() : SailType.SOUL;
    }

    public static int getMaxXpForItem(ItemStack stack) {
        return stack.getItem() instanceof SoulSailItem sailItem ? sailItem.getMaxXp() : getTotalXpForLevel(LEVEL_SOUL);
    }

    public static int getMaxLevelForItem(ItemStack stack) {
        return stack.getItem() instanceof SoulSailItem sailItem ? sailItem.getMaxLevel() : LEVEL_SOUL;
    }

    public static boolean isFullItem(ItemStack stack) {
        return stack.is(ModItems.SOUL_SAIL_FULL.get())
                || stack.is(ModItems.THOUSAND_SOUL_SAIL_FULL.get())
                || stack.is(ModItems.MYRIAD_SOUL_SAIL_FULL.get());
    }

    public static boolean isNonFullItem(ItemStack stack) {
        return stack.is(ModItems.SOUL_SAIL.get())
                || stack.is(ModItems.THOUSAND_SOUL_SAIL.get())
                || stack.is(ModItems.MYRIAD_SOUL_SAIL.get());
    }

    
    
    

    public Item getFullVersion() {
        return switch (this.type) {
            case SOUL -> ModItems.SOUL_SAIL_FULL.get();
            case THOUSAND -> ModItems.THOUSAND_SOUL_SAIL_FULL.get();
            case MYRIAD -> ModItems.MYRIAD_SOUL_SAIL_FULL.get();
        };
    }

    public Item getNonFullVersion() {
        return switch (this.type) {
            case SOUL -> ModItems.SOUL_SAIL.get();
            case THOUSAND -> ModItems.THOUSAND_SOUL_SAIL.get();
            case MYRIAD -> ModItems.MYRIAD_SOUL_SAIL.get();
        };
    }

    public static ItemStack convertToFullItem(ItemStack stack) {
        if (isFullItem(stack)) {
            return stack;
        }

        if (stack.getItem() instanceof SoulSailItem sailItem) {
            ItemStack newStack = new ItemStack(sailItem.getFullVersion());
            setSailData(newStack, getSailData(stack));
            return newStack;
        }

        return stack;
    }

    public static ItemStack convertToNonFullItem(ItemStack stack) {
        if (!isFullItem(stack)) {
            return stack;
        }

        if (stack.getItem() instanceof SoulSailItem sailItem) {
            ItemStack newStack = new ItemStack(sailItem.getNonFullVersion());
            setSailData(newStack, getSailData(stack));
            return newStack;
        }

        return stack;
    }

    
    public boolean storeXp(ItemStack stack, int xpPoints) {
        if (isFullItem(stack)) {
            return false;
        }

        int currentXp = getStoredXp(stack);
        int remaining = this.maxXp - currentXp;
        if (remaining <= 0) {
            return false;
        }

        int newXp = currentXp + Math.min(xpPoints, remaining);
        setStoredXp(stack, newXp);

        return newXp >= this.maxXp;
    }

    
    
    

    public SailType getType() {
        return this.type;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public int getMaxXp() {
        return this.maxXp;
    }

    public ChatFormatting getColor() {
        return this.type.color;
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (getStoredXp(stack) > 0) {
            return this.useSoulSail(level, player, hand, stack);
        }

        
        if (player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            player.setItemSlot(EquipmentSlot.HEAD, stack.copyWithCount(1));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0f, 1.0f);
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    private InteractionResultHolder<ItemStack> useSoulSail(Level level, Player player,
                                                           InteractionHand hand, ItemStack stack) {
        int storedXp = getStoredXp(stack);
        int storedLevel = xpToLevel(storedXp);
        boolean isFull = isFullItem(stack);

        
        if (player.isShiftKeyDown()) {
            if (storedXp <= 0) {
                player.displayClientMessage(Component.translatable("item.kaleidoscope_sculk.soul_sail.no_xp")
                        .withStyle(ChatFormatting.RED), true);
                return InteractionResultHolder.pass(stack);
            }

            if (level.isClientSide) {
                return InteractionResultHolder.success(stack);
            }

            setStoredXp(stack, 0);
            player.giveExperiencePoints(storedXp);
            stack = swapToNonFull(stack, player, hand, isFull);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
            player.displayClientMessage(
                    Component.translatable("item.kaleidoscope_sculk.soul_sail.withdraw_all")
                            .withStyle(ChatFormatting.GREEN), true);

            return InteractionResultHolder.success(stack);
        }

        
        if (storedXp > 0) {
            if (level.isClientSide) {
                return InteractionResultHolder.success(stack);
            }

            int neededForNextLevel = getXpNeededForLevel(storedLevel);
            int toWithdraw = Math.min(neededForNextLevel, storedXp);
            setStoredXp(stack, storedXp - toWithdraw);
            player.giveExperiencePoints(toWithdraw);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
            player.displayClientMessage(
                    Component.translatable("item.kaleidoscope_sculk.soul_sail.withdraw_amount", toWithdraw)
                            .withStyle(ChatFormatting.GREEN), true);

            stack = swapToNonFull(stack, player, hand, isFull);

            return InteractionResultHolder.success(stack);
        }

        
        if (!level.isClientSide) {
            if (isFull) {
                player.displayClientMessage(
                        Component.translatable("item.kaleidoscope_sculk.soul_sail.full_message", named())
                                .withStyle(ChatFormatting.YELLOW), true);
            } else {
                player.displayClientMessage(
                        Component.translatable("item.kaleidoscope_sculk.soul_sail.storage",
                                named(), storedXp, this.maxXp, storedLevel), true);
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    
    private ItemStack swapToNonFull(ItemStack stack, Player player, InteractionHand hand, boolean isFull) {
        if (!isFull) {
            return stack;
        }

        ItemStack newStack = convertToNonFullItem(stack);
        player.setItemInHand(hand, newStack);
        return newStack;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!state.is(Blocks.SOUL_SAND) && !state.is(Blocks.SOUL_SOIL)) {
            return InteractionResult.PASS;
        }

        return this.convertSoulBlock(level, pos, state, context.getPlayer(), context.getItemInHand());
    }

    private InteractionResult convertSoulBlock(Level level, BlockPos pos, BlockState state,
                                               Player player, ItemStack stack) {
        if (!level.isClientSide) {
            Block newBlock = state.is(Blocks.SOUL_SAND) ? Blocks.SAND : Blocks.DIRT;
            level.setBlock(pos, newBlock.defaultBlockState(), Block.UPDATE_ALL);

            if (level.random.nextFloat() < SOUL_DROP_CHANCE) {
                ItemEntity itemEntity = new ItemEntity(
                        level,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        new ItemStack(ModItems.SOUL.get())
                );
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }

            level.playSound(null, pos, SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    
    
    

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        boolean isFull = isFullItem(stack);
        int storedXp = getStoredXp(stack);
        int storedLevel = xpToLevel(storedXp);

        if (isFull) {
            tooltip.add(Component.translatable("item.kaleidoscope_sculk.soul_sail.full_title", named())
                    .withStyle(ChatFormatting.YELLOW));
        } else {
            tooltip.add(Component.translatable("item.kaleidoscope_sculk.soul_sail.title", named())
                    .withStyle(this.type.color));
            tooltip.add(Component.translatable("item.kaleidoscope_sculk.soul_sail." + this.type.name + ".tooltip")
                    .withStyle(ChatFormatting.GRAY));
        }

        MutableComponent bar = Component.literal(buildProgressBar(storedXp, isFull));
        if (isFull) {
            bar.append(Component.translatable("item.kaleidoscope_sculk.soul_sail.bar_full")
                    .withStyle(ChatFormatting.YELLOW));
        }
        tooltip.add(bar);

        if (!isFull) {
            tooltip.add(Component.translatable("item.kaleidoscope_sculk.soul_sail.xp_level",
                    storedLevel, this.maxLevel).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("item.kaleidoscope_sculk.soul_sail.xp_points",
                    format(storedXp), format(this.maxXp)).withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(Component.empty());
        if (isFull || storedXp > 0) {
            tooltip.add(Component.translatable("item.kaleidoscope_sculk.soul_sail.usage")
                    .withStyle(ChatFormatting.YELLOW));
        }

        tooltip.add(Component.empty());
        if (this.type == SailType.MYRIAD) {
            tooltip.add(Component.translatable("item.kaleidoscope_sculk.soul_sail.final_form")
                    .withStyle(ChatFormatting.GOLD));
        } else {
            if (!isFull) {
                int needLevel = this.maxLevel - storedLevel;
                int needXp = this.maxXp - storedXp;
                tooltip.add(Component.translatable("item.kaleidoscope_sculk.soul_sail.need_more",
                        needLevel, format(needXp)).withStyle(ChatFormatting.GRAY));
            }
            tooltip.add(Component.translatable("item.kaleidoscope_sculk.soul_sail.upgrade_hint",
                    getNextTierComponent()).withStyle(ChatFormatting.GRAY));
        }
    }

    private String buildProgressBar(int storedXp, boolean isFull) {
        int filled = isFull
                ? BAR_SEGMENTS
                : (int) ((float) storedXp / this.maxXp * BAR_SEGMENTS);
        filled = Math.max(0, Math.min(BAR_SEGMENTS, filled));
        return progressBars(this.type)[filled];
    }

    private Component getNextTierComponent() {
        return switch (this.type) {
            case SOUL -> Component.translatable("item.kaleidoscope_sculk.soul_sail.tier.thousand");
            case THOUSAND -> Component.translatable("item.kaleidoscope_sculk.soul_sail.tier.myriad");
            case MYRIAD -> Component.translatable("item.kaleidoscope_sculk.soul_sail.tier.final");
        };
    }

    private Component named() {
        return Component.translatable("item.kaleidoscope_sculk.soul_sail." + this.type.name);
    }

    private static String format(int value) {
        String digits = Integer.toString(value);
        int length = digits.length();
        if (length <= 3) {
            return digits;
        }

        StringBuilder builder = new StringBuilder(length + length / 3);
        int head = length % 3 == 0 ? 3 : length % 3;
        builder.append(digits, 0, head);
        for (int i = head; i < length; i += 3) {
            builder.append(',').append(digits, i, i + 3);
        }
        return builder.toString();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isFullItem(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getDescriptionId(stack)).withStyle(this.type.color);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        return super.getDescriptionId(stack) + "." + this.type.name;
    }

    public enum SailType {
        SOUL("soul", LEVEL_SOUL, ChatFormatting.BLUE, "§b"),
        THOUSAND("thousand", LEVEL_THOUSAND, ChatFormatting.DARK_PURPLE, "§5"),
        MYRIAD("myriad", LEVEL_MYRIAD, ChatFormatting.GOLD, "§6");

        public final String name;
        public final int maxLevel;
        public final ChatFormatting color;
        public final String barColor;

        SailType(String name, int maxLevel, ChatFormatting color, String barColor) {
            this.name = name;
            this.maxLevel = maxLevel;
            this.color = color;
            this.barColor = barColor;
        }

        public static SailType fromName(String name) {
            for (SailType type : values()) {
                if (type.name.equals(name)) {
                    return type;
                }
            }
            return SOUL;
        }
    }
}
