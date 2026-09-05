// java/org/kaleidoscope_sculk/item/SoulSailBlockItem.java
package org.kaleidoscope_sculk.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.kaleidoscope_sculk.component.SoulSailData;
import org.kaleidoscope_sculk.register.ModDataComponents;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.List;

public class SoulSailBlockItem extends BlockItem {

    public SoulSailBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        SoulSailData data = stack.get(ModDataComponents.SOUL_SAIL_DATA.get());
        if (data == null) data = SoulSailData.DEFAULT;

        SoulSailItem.SailType type = SoulSailItem.SailType.fromName(data.type());
        int storedXp = data.storedXp();
        int maxXp = type.maxLevel;
        int storedLevel = SoulSailItem.xpToLevel(storedXp);

        // 经验条
        float progress = (float) storedXp / SoulSailItem.getTotalXpForLevel(maxXp);
        int bars = (int) (progress * 20);
        StringBuilder bar = new StringBuilder("§7[");
        String filledColor = switch (type) {
            case SOUL -> "§b";
            case THOUSAND -> "§5";
            case MYRIAD -> "§6";
        };
        for (int i = 0; i < 20; i++) {
            if (i < bars) {
                bar.append(filledColor).append("█");
            } else {
                bar.append("§8░");
            }
        }
        bar.append("§7]");
        tooltip.add(Component.literal(bar.toString()));
        tooltip.add(Component.literal("§7经验等级: §e" + storedLevel + " §7/ §e" + maxXp));

        String formattedXp = String.format("%,d", storedXp);
        String formattedMaxXp = String.format("%,d", SoulSailItem.getTotalXpForLevel(maxXp));
        tooltip.add(Component.literal("§7经验点数: §e" + formattedXp + " §7/ §e" + formattedMaxXp));
    }

    @Override
    public Component getName(ItemStack stack) {
        SoulSailData data = stack.get(ModDataComponents.SOUL_SAIL_DATA.get());
        if (data == null) data = SoulSailData.DEFAULT;
        SoulSailItem.SailType type = SoulSailItem.SailType.fromName(data.type());
        String name = switch (type) {
            case SOUL -> "魂幡";
            case THOUSAND -> "千魂幡";
            case MYRIAD -> "万魂幡";
        };
        return Component.literal(type.color + name);
    }
}