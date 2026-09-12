package org.kaleidoscope_sculk;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class ModTooltipHandler {

    private static final int TOOLTIP_INDEX = 1;

    private static Map<Item, Component> simpleTooltips;

    private static Map<Item, Component> simpleTooltips() {
        Map<Item, Component> cached = simpleTooltips;
        if (cached == null) {
            cached = Map.ofEntries(
                    simple(ModItems.WARDEN_TENDRIL, "warden_tendril", ChatFormatting.GRAY),
                    simple(ModItems.ANCIENT_BONE_FRAGMENT, "ancient_bone_fragment", ChatFormatting.GRAY),
                    simple(ModItems.SCULK_FUNGUS, "sculk_fungus", ChatFormatting.GRAY),
                    simple(ModItems.ECHO_SEED, "echo_seed", ChatFormatting.GRAY),
                    simple(ModItems.SCULK_BRANCH, "sculk_branch", ChatFormatting.GRAY),
                    simple(ModItems.SCULK_PINAPPLE, "sculk_pinapple", ChatFormatting.DARK_GRAY),
                    simple(ModItems.SOUL, "soul", ChatFormatting.GRAY)
            );
            simpleTooltips = cached;
        }
        return cached;
    }

    private static Map.Entry<Item, Component> simple(Supplier<? extends Item> item,
                                                     String key, ChatFormatting color) {
        return Map.entry(item.get(),
                Component.translatable("item.kaleidoscope_sculk." + key + ".tooltip").withStyle(color));
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        Component simpleLine = simpleTooltips().get(itemStack.getItem());
        if (simpleLine != null) {
            tooltip.add(TOOLTIP_INDEX, simpleLine);
        }

        if (itemStack.is(ModItems.SCULK_CATERPILLAR.get())) {
            tooltip.add(TOOLTIP_INDEX, Component.empty());
            tooltip.add(TOOLTIP_INDEX + 1, Component.translatable("item.kaleidoscope_sculk.echo")
                    .withStyle(ChatFormatting.BLUE));
        }

        if (itemStack.is(ModItems.ANCIENT_BRITTLE_BONE_FRAGMENTS.get())) {
            tooltip.add(TOOLTIP_INDEX, Component.empty());
            tooltip.add(TOOLTIP_INDEX + 1, Component.translatable("item.kaleidoscope_sculk.strengthII1800")
                    .withStyle(ChatFormatting.BLUE));
        }

        if (itemStack.is(ModItems.SOUL_PANCAKE.get())) {
            tooltip.add(TOOLTIP_INDEX, Component.translatable("item.kaleidoscope_sculk.soul_pancake.tooltip")
                    .withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(TOOLTIP_INDEX + 1, Component.empty());
            tooltip.add(TOOLTIP_INDEX + 2, Component.translatable("item.kaleidoscope_sculk.resistance")
                    .withStyle(ChatFormatting.BLUE));
            tooltip.add(TOOLTIP_INDEX + 3, Component.translatable("item.kaleidoscope_sculk.regeneration")
                    .withStyle(ChatFormatting.BLUE));
        }

    }
}
