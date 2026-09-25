package Tequilacat.KaleidoscopeSculk.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import Tequilacat.KaleidoscopeSculk.Kaleidoscope_sculk;
import Tequilacat.KaleidoscopeSculk.register.ModRegistries;
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
}
