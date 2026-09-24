package org.kaleidoscope_sculk.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModEffects;
import org.kaleidoscope_sculk.register.ModItems;
import org.kaleidoscope_sculk.register.ModRegistries;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID, value = Dist.CLIENT)
public class ClientSetup {

    public static final String KEY_CATEGORY = "key.category.kaleidoscope_sculk";

    public static final KeyMapping SONIC_BOOM_KEY = new KeyMapping(
            "key.kaleidoscope_sculk.sonic_boom",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            KEY_CATEGORY
    );

    public static final KeyMapping SOUL_SAIL_KEY = new KeyMapping(
            "key.kaleidoscope_sculk.soul_sail",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            KEY_CATEGORY
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SONIC_BOOM_KEY);
        event.register(SOUL_SAIL_KEY);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModRegistries.ANCIENT_BONE_FRAGMENT_PROJECTILE.get(),
                ThrownItemRenderer::new);
    }

    /**
     * Draws the Reap effect icon using the Soul item's texture instead of an effect sprite.
     */
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(new IClientMobEffectExtensions() {
            @Override
            public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen,
                                               GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                // Vanilla draws the 18x18 icon at (x, y + 7); center the 16x16 item inside it.
                guiGraphics.renderItem(new ItemStack(ModItems.SOUL.get()), x + 1, y + 8);
                return true;
            }

            @Override
            public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics,
                                         int x, int y, float z, float partialTick) {
                // Vanilla draws the 18x18 icon at (x + 3, y + 3) inside a 24x24 frame; center the item.
                guiGraphics.renderItem(new ItemStack(ModItems.SOUL.get()), x + 4, y + 4);
                return true;
            }
        }, ModEffects.SOUL_POWER);
    }
}
