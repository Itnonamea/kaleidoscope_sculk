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
//public class MyriadSailBakedModel implements BakedModel {
//
//    // 存储原始的普通模型（用于非万魂幡的情况）
//    private final BakedModel originalModel;
//
//    public MyriadSailBakedModel() {
//        this.originalModel = null; // 简化处理
//    }
//
//    public MyriadSailBakedModel(BakedModel original) {
//        this.originalModel = original;
//    }
//
//    @Override
//    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
//        // 使用自定义渲染，不返回 quad
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
//        return true;
//    }
//
//    @Override
//    public boolean usesBlockLight() {
//        return false;
//    }
//
//    @Override
//    public boolean isCustomRenderer() {
//        // 这里无法获取 ItemStack，所以返回 true 让所有魂幡都用自定义渲染
//        // 然后在渲染器中判断类型
//        return true;
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