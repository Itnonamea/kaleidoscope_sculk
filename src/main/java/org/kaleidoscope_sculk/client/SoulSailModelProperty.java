//// java/org/kaleidoscope_sculk/client/SoulSailModelProperty.java
//package org.kaleidoscope_sculk.client;
//
//import net.minecraft.client.renderer.item.ItemProperties;
//import net.minecraft.resources.ResourceLocation;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//import org.kaleidoscope_sculk.Kaleidoscope_sculk;
//import org.kaleidoscope_sculk.item.SoulSailItem;
//import org.kaleidoscope_sculk.register.ModItems;
//
//@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID, value = Dist.CLIENT)
//public class SoulSailModelProperty {
//
//    private static final ResourceLocation SAIL_TYPE_PROPERTY =
//            ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "sail_type");
//
//    @SubscribeEvent
//    public static void onClientSetup(FMLClientSetupEvent event) {
//        event.enqueueWork(() -> {
//            ItemProperties.register(
//                    ModItems.SOUL_SAIL.get(),
//                    SAIL_TYPE_PROPERTY,
//                    (stack, level, entity, seed) -> {
//                        SoulSailItem.SailType type = SoulSailItem.getSailType(stack);
//                        return switch (type) {
//                            case SOUL -> 0.0f;
//                            case THOUSAND -> 1.0f;
//                            case MYRIAD -> 2.0f;
//                        };
//                    }
//            );
//        });
//    }
//}