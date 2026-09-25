package Tequilacat.KaleidoscopeSculk.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import Tequilacat.KaleidoscopeSculk.Kaleidoscope_sculk;
import Tequilacat.KaleidoscopeSculk.network.ModPackets;

import java.util.function.Supplier;

/**
 * Client-side key dispatch: sends the matching skill packet on the frame the key is pressed.
 */
@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static boolean sonicBoomPressed;
    private static boolean soulSailPressed;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        sonicBoomPressed = sendOnPress(ClientSetup.SONIC_BOOM_KEY, sonicBoomPressed, ModPackets.SonicBoom::new);
        soulSailPressed = sendOnPress(ClientSetup.SOUL_SAIL_KEY, soulSailPressed, ModPackets.SoulSailAbsorb::new);
    }

    /**
     * Only sends on the frame the key goes down; returns the key state for the next frame to compare.
     */
    private static boolean sendOnPress(KeyMapping key, boolean lastPressed,
                                       Supplier<? extends CustomPacketPayload> packet) {
        boolean pressed = key.isDown();
        if (pressed && !lastPressed) {
            PacketDistributor.sendToServer(packet.get());
        }
        return pressed;
    }
}
