// java/org/kaleidoscope_sculk/handler/SoulSailBannerHandler.java
package org.kaleidoscope_sculk.handler;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.item.SoulSailItem;
import org.kaleidoscope_sculk.register.ModItems;
import org.slf4j.Logger;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class SoulSailBannerHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        BlockState state = level.getBlockState(pos);

        // 检查是否手持灵魂
        if (!stack.is(ModItems.SOUL.get())) {
            return;
        }

        // 检查是否点击了旗帜
        if (state.getBlock() instanceof net.minecraft.world.level.block.BannerBlock) {
            InteractionResult result = convertBannerToSail(level, pos, state, player, stack);
            if (result.consumesAction()) {
                event.setCanceled(true);
                event.setCancellationResult(result);
            }
        }
    }

    public static InteractionResult convertBannerToSail(Level level, BlockPos pos, BlockState state,
                                                        Player player, ItemStack stack) {
        if (!isValidBanner(level, pos)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            int rotation = state.getValue(net.minecraft.world.level.block.BannerBlock.ROTATION);
            level.destroyBlock(pos, false);

            ItemStack sailStack = new ItemStack(ModItems.SOUL_SAIL.get());
            SoulSailItem.setSailData(sailStack, SoulSailItem.SailType.SOUL, rotation, 0);

            ItemEntity itemEntity = new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    sailStack
            );
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);

            level.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD,
                    SoundSource.BLOCKS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private static boolean isValidBanner(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof BannerBlockEntity banner)) {
            return false;
        }

        ItemStack bannerItem = banner.getItem();
        if (bannerItem.isEmpty()) {
            return false;
        }

        // 检查是否为黑色旗帜
        if (!bannerItem.is(net.minecraft.world.item.Items.BLACK_BANNER)) {
            return false;
        }

        // 检查图案 - 使用 BannerBlockEntity 的 getPatterns()
        try {
            BannerPatternLayers patternLayers = banner.getPatterns();
            if (patternLayers != null) {
                for (BannerPatternLayers.Layer layer : patternLayers.layers()) {
                    ResourceLocation patternLocation = layer.pattern().unwrapKey()
                            .map(key -> key.location())
                            .orElse(null);
                    if (patternLocation != null && patternLocation.getPath().contains("skull")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // 忽略
        }

        // 尝试从 DataComponent 获取
        try {
            var patterns = bannerItem.get(DataComponents.BANNER_PATTERNS);
            if (patterns != null) {
                for (var layer : patterns.layers()) {
                    ResourceLocation patternLocation = layer.pattern().unwrapKey()
                            .map(key -> key.location())
                            .orElse(null);
                    if (patternLocation != null && patternLocation.getPath().contains("skull")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // 忽略
        }

        return false;
    }
}