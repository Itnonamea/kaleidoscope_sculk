package org.kaleidoscope_sculk;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.kaleidoscope_sculk.register.ModBlocks;
import org.kaleidoscope_sculk.register.ModItems;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class ModTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();

        // 监守者触须
        if (itemStack.getItem() == ModItems.WARDEN_TENDRIL.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.warden_tendril.tooltip")
                    .withStyle(ChatFormatting.GRAY));
        }

        // 炒监守者触须
        if (itemStack.getItem() == ModItems.COOKED_WARDEN_TENDRIL_BOWL.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.cooked_warden_tendril_bowl.tooltip")
                    .withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(2, Component.empty());
            event.getToolTip().add(3, Component.translatable("item.kaleidoscope_sculk.sonic_wave")
                    .withStyle(ChatFormatting.BLUE));
        }

        // 远古骨碎片
        if (itemStack.getItem() == ModItems.ANCIENT_BONE_FRAGMENT.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.ancient_bone_fragment.tooltip")
                    .withStyle(ChatFormatting.GRAY));
        }

        // 幽匿真菌汤
        if (itemStack.getItem() == ModItems.SCULK_FUNGUS_SOUP.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.sculk_fungus_soup.tooltip")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        // 幽匿炖菜方块
        if (itemStack.getItem() == ModItems.SCULK_STEW_BLOCK_ITEM.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.sculk_stew.tooltip")
                    .withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(2, Component.empty());
            event.getToolTip().add(3, Component.translatable("item.kaleidoscope_sculk.warmth")
                    .withStyle(ChatFormatting.BLUE));
        }

        // 幽匿真菌
        if (itemStack.getItem() == ModItems.SCULK_FUNGUS.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.sculk_fungus.tooltip")
                    .withStyle(ChatFormatting.GRAY));
        }

        // 回响种子
        if (itemStack.getItem() == ModItems.ECHO_SEED.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.echo_seed.tooltip")
                    .withStyle(ChatFormatting.GRAY));
        }

        // 幽匿枝
        if (itemStack.getItem() == ModItems.SCULK_BRANCH.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.sculk_branch.tooltip")
                    .withStyle(ChatFormatting.GRAY));
        }

        //纯粹
        if (itemStack.getItem() == ModItems.SCULK_PINAPPLE.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.sculk_pinapple.tooltip")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        //菠萝糊糊
        if (itemStack.getItem() == ModItems.BOIL_SCULK_PINAPPLE.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.boil_sculk_pinapple.tooltip")
                    .withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(2, Component.empty());
            event.getToolTip().add(3, Component.translatable("item.kaleidoscope_sculk.echo")
                    .withStyle(ChatFormatting.BLUE));
        }

        //幽匿猪儿虫
        if (itemStack.getItem() == ModItems.SCULK_CATERPILLAR.get()) {
            event.getToolTip().add(1, Component.empty());
            event.getToolTip().add(2, Component.translatable("item.kaleidoscope_sculk.echo")
                    .withStyle(ChatFormatting.BLUE));
        }

        //远古脆片
        if (itemStack.getItem() == ModItems.ANCIENT_BRITTLE_BONE_FRAGMENTS.get()) {
            event.getToolTip().add(1, Component.empty());
            event.getToolTip().add(2, Component.translatable("item.kaleidoscope_sculk.strengthII1800")
                    .withStyle(ChatFormatting.BLUE));
        }

        // 幽匿炖大骨
        if (itemStack.getItem() == ModItems.PORK_ANCIENT_BONE_SOUP.get()) {
            event.getToolTip().add(1, Component.empty());
            event.getToolTip().add(2, Component.translatable("item.kaleidoscope_sculk.vigor")
                    .withStyle(ChatFormatting.BLUE));
            event.getToolTip().add(3, Component.translatable("item.kaleidoscope_sculk.sonic_waveI")
                    .withStyle(ChatFormatting.BLUE));
        }

        //灵魂
        if (itemStack.getItem() == ModItems.SOUL.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.soul.tooltip")
                    .withStyle(ChatFormatting.GRAY));
        }

        // 幽匿炖鸡煲
        if (itemStack.getItem() == ModItems.SCULK_CHICKEN_STEW_BLOCK_ITEM.get()) {
            event.getToolTip().add(1, Component.empty());
            event.getToolTip().add(2, Component.translatable("item.kaleidoscope_sculk.warmth_2min")
                    .withStyle(ChatFormatting.BLUE));
        }

        // 灵魂薄饼
        if (itemStack.getItem() == ModItems.SOUL_PANCAKE.get()) {
            event.getToolTip().add(1, Component.translatable("item.kaleidoscope_sculk.soul_pancake.tooltip")
                    .withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(2, Component.empty());
            event.getToolTip().add(3, Component.translatable("item.kaleidoscope_sculk.resistance")
                    .withStyle(ChatFormatting.BLUE));
            event.getToolTip().add(4, Component.translatable("item.kaleidoscope_sculk.regeneration")
                    .withStyle(ChatFormatting.BLUE));
        }

// 古城风味刺身
        if (itemStack.getItem() == ModItems.ANCIENT_CITY_STYLE_SASHIMI.get()) {
            event.getToolTip().add(1, Component.translatable("block.kaleidoscope_sculk.ancient_city_style_sashimi.tooltip")
                    .withStyle(ChatFormatting.DARK_GRAY));
            event.getToolTip().add(2, Component.empty());
            event.getToolTip().add(3, Component.translatable("block.kaleidoscope_sculk.mustard_effect")
                    .withStyle(ChatFormatting.BLUE));
        }
    }
}