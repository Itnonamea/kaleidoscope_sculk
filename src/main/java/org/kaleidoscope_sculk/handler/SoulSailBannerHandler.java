package org.kaleidoscope_sculk.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.item.SoulSailItem;
import org.kaleidoscope_sculk.register.ModItems;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class SoulSailBannerHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.SOUL.get())) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BannerBlock)) return;

        InteractionResult result = convertBannerToSail(level, pos, state, event.getEntity(), stack);
        if (result.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    public static InteractionResult convertBannerToSail(Level level, BlockPos pos, BlockState state,
                                                        Player player, ItemStack stack) {
        if (!isValidBanner(level, pos)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            int rotation = state.getValue(BannerBlock.ROTATION);
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

            level.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private static boolean isValidBanner(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof BannerBlockEntity banner)) {
            return false;
        }

        ItemStack bannerItem = banner.getItem();
        if (bannerItem.isEmpty() || !bannerItem.is(Items.BLACK_BANNER)) {
            return false;
        }

        
        if (hasSkullPattern(banner.getPatterns())) {
            return true;
        }

        
        return hasSkullPattern(bannerItem.get(DataComponents.BANNER_PATTERNS));
    }

    private static boolean hasSkullPattern(BannerPatternLayers layers) {
        if (layers == null) {
            return false;
        }

        for (BannerPatternLayers.Layer layer : layers.layers()) {
            ResourceLocation patternLocation = layer.pattern().unwrapKey().map(key -> key.location()).orElse(null);
            if (patternLocation != null && patternLocation.getPath().contains("skull")) {
                return true;
            }
        }

        return false;
    }
}
