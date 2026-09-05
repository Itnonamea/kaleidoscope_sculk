//package org.kaleidoscope_sculk.client.render;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import com.mojang.math.Axis;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.model.geom.ModelPart;
//import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.resources.model.Material;
//import net.minecraft.client.resources.model.ModelBakery;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemDisplayContext;
//import net.minecraft.world.item.ItemStack;
//import org.kaleidoscope_sculk.Kaleidoscope_sculk;
//import org.kaleidoscope_sculk.item.SoulSailItem;
//
//public class MyriadSailRenderer extends BlockEntityWithoutLevelRenderer {
//
//    private ModelPart flag;
//    private ModelPart pole;
//    private ModelPart bar;
//    private boolean initialized = false;
//
//    public MyriadSailRenderer() {
//        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
//                null); // 先传 null，后面再初始化
//    }
//
//    // 延迟初始化模型
//    private void initModels() {
//        if (initialized) return;
//
//        try {
//            var models = Minecraft.getInstance().getEntityModels();
//            if (models != null) {
//                var model = models.bakeLayer(
//                        net.minecraft.client.model.geom.ModelLayers.BANNER
//                );
//                this.flag = model.getChild("flag");
//                this.pole = model.getChild("pole");
//                this.bar = model.getChild("bar");
//                initialized = true;
//            }
//        } catch (Exception e) {
//            // 模型还没准备好，稍后再试
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext,
//                             PoseStack poseStack, MultiBufferSource bufferSource,
//                             int packedLight, int packedOverlay) {
//
//        // 判断是否是万魂幡
//        SoulSailItem.SailType type = SoulSailItem.getSailType(stack);
//        if (type != SoulSailItem.SailType.MYRIAD) {
//            return;
//        }
//
//        // 延迟初始化模型
//        initModels();
//        if (!initialized) {
//            return; // 模型还没准备好，暂时不渲染
//        }
//
//        poseStack.pushPose();
//
//        // 获取旋转方向
//        int rotation = SoulSailItem.getRotation(stack);
//        float rotDeg = rotation * 22.5f;
//
//        // 缩放（跟旗帜一致）
//        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
//        poseStack.mulPose(Axis.YP.rotationDegrees(rotDeg));
//
//        // 渲染旗杆和横梁（使用原版旗帜纹理）
//        VertexConsumer baseConsumer = ModelBakery.BANNER_BASE.buffer(bufferSource, RenderType::entitySolid);
//        this.pole.render(poseStack, baseConsumer, packedLight, packedOverlay);
//        this.bar.render(poseStack, baseConsumer, packedLight, packedOverlay);
//
//        // 渲染旗面（使用万魂幡自定义纹理）
//        Material sailMaterial = new Material(
//                ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID,
//                        "textures/entity/soul_sail_thousand.png"),
//                ResourceLocation.fromNamespaceAndPath(Kaleidoscope_sculk.MODID, "myriad_sail")
//        );
//        VertexConsumer sailConsumer = sailMaterial.buffer(bufferSource, RenderType::entitySolid);
//        this.flag.render(poseStack, sailConsumer, packedLight, packedOverlay);
//
//        poseStack.popPose();
//    }
//}