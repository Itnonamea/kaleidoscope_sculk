// org/kaleidoscope_sculk/mixin/BannerRendererMixin.java
package org.kaleidoscope_sculk.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.kaleidoscope_sculk.item.SoulSailItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerRenderer.class)
public class BannerRendererMixin {

//    @Inject(method = "renderPatterns", at = @At("HEAD"), cancellable = true)
//    private static void onRenderPatterns(
//            PoseStack poseStack,
//            MultiBufferSource bufferSource,
//            int packedLight,
//            int packedOverlay,
//            net.minecraft.client.model.geom.ModelPart flag,
//            net.minecraft.client.resources.model.Material baseMaterial,
//            boolean isBanner,
//            net.minecraft.world.item.DyeColor baseColor,
//            BannerPatternLayers patterns,
//            boolean isShield,
//            CallbackInfo ci
//    ) {
//        // 检测是否为魂幡的渲染
//        // 这里需要额外的上下文来判断当前渲染的是不是魂幡
//        // 由于静态方法无法直接获取 ItemStack，需要另想办法
//    }
}