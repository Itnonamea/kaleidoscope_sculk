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

    private static Map<Item, List<Component>> multiLineTooltips;

    private static Map<Item, List<Component>> multiLineTooltips() {
        Map<Item, List<Component>> cached = multiLineTooltips;
        if (cached == null) {
            cached = Map.ofEntries(
                    Map.entry(ModItems.SCULK_CATERPILLAR.get(), List.of(
                            Component.empty(),
                            Component.translatable("item.kaleidoscope_sculk.echo")
                                    .withStyle(ChatFormatting.BLUE))),
                    Map.entry(ModItems.ANCIENT_BRITTLE_BONE_FRAGMENTS.get(), List.of(
                            Component.empty(),
                            Component.translatable("item.kaleidoscope_sculk.strengthII1800")
                                    .withStyle(ChatFormatting.BLUE))),
                    Map.entry(ModItems.SOUL_PANCAKE.get(), List.of(
                            Component.translatable("item.kaleidoscope_sculk.soul_pancake.tooltip")
                                    .withStyle(ChatFormatting.DARK_GRAY),
                            Component.empty(),
                            Component.translatable("item.kaleidoscope_sculk.resistance")
                                    .withStyle(ChatFormatting.BLUE),
                            Component.translatable("item.kaleidoscope_sculk.regeneration")
                                    .withStyle(ChatFormatting.BLUE)))
            );
            multiLineTooltips = cached;
        }
        return cached;
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        List<Component> tooltip = event.getToolTip();

        Component simpleLine = simpleTooltips().get(item);
        if (simpleLine != null) {
            tooltip.add(TOOLTIP_INDEX, simpleLine);
        }

        List<Component> extraLines = multiLineTooltips().get(item);
        if (extraLines != null) {
            tooltip.addAll(TOOLTIP_INDEX, extraLines);
        }
    }
}
