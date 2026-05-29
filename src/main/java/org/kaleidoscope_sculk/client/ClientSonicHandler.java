package org.kaleidoscope_sculk.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.kaleidoscope_sculk.network.SonicBoomPacket;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientSonicHandler {

    private static boolean lastPressed = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean pressed = KeyBindings.sonicBoomKey.isDown();

        // 按键按下瞬间触发
        if (pressed && !lastPressed) {
            // 必须空手才能发射
            if (mc.player.getMainHandItem().isEmpty()) {
                PacketDistributor.sendToServer(new SonicBoomPacket());
            }
        }

        lastPressed = pressed;
    }
}