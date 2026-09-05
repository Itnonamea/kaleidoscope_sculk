//// org/kaleidoscope_sculk/client/model/MyriadSailModel.java
//package org.kaleidoscope_sculk.client.model;
//
//import net.minecraft.client.renderer.block.model.BakedQuad;
//import net.minecraft.client.renderer.block.model.ItemOverrides;
//import net.minecraft.client.renderer.block.model.ItemTransforms;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.resources.model.BakedModel;
//import net.minecraft.core.Direction;
//import net.minecraft.util.RandomSource;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.state.BlockState;
//import org.kaleidoscope_sculk.item.SoulSailItem;
//import javax.annotation.Nullable;
//import java.util.List;
//
//public class MyriadSailModel implements BakedModel {
//
//    @Override
//    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
//        // 万魂幡使用自定义渲染，不返回任何 quad
//        return List.of();
//    }
//
//    @Override
//    public boolean useAmbientOcclusion() {
//        return true;
//    }
//
//    @Override
//    public boolean isGui3d() {
//        return true; // 在 GUI 中显示为 3D
//    }
//
//    @Override
//    public boolean usesBlockLight() {
//        return false;
//    }
//
//    @Override
//    public boolean isCustomRenderer() {
//        return true; // 关键：使用自定义渲染器
//    }
//
//    @Override
//    public TextureAtlasSprite getParticleIcon() {
//        return null;
//    }
//
//    @Override
//    public ItemTransforms getTransforms() {
//        return ItemTransforms.NO_TRANSFORMS;
//    }
//
//    @Override
//    public ItemOverrides getOverrides() {
//        return ItemOverrides.EMPTY;
//    }
//}