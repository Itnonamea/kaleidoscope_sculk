package org.kaleidoscope_sculk.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
public class KeyBindings {
    public static final String KEY_CATEGORY = "key.category.kaleidoscope_sculk";
    public static final String KEY_SONIC_BOOM = "key.kaleidoscope_sculk.sonic_boom";

    public static KeyMapping sonicBoomKey = new KeyMapping(
            KEY_SONIC_BOOM,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KEY_CATEGORY
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(sonicBoomKey);
    }
}