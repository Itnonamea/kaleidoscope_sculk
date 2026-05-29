package org.kaleidoscope_sculk.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.Random;

@EventBusSubscriber(modid = Kaleidoscope_sculk.MODID)
public class SculkVeinHandler {

    private static final Random RANDOM = new Random();

    // 掉落几率
    private static final float SCULK_BRANCH_DROP_CHANCE = 0.1f;
    private static final float SCULK_CATERPILLAR_DROP_CHANCE = 0.05f;

    @SubscribeEvent
    public static void onRightClickSculk(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack handItem = event.getItemStack();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        InteractionHand hand = event.getHand();  // 获取手部

        // 判断是否手持厨刀
        boolean isKnife = handItem.is(ModItems.EROSION_KITCHEN_KNIFE.get()) ||
                handItem.is(ModItems.SILENT_KITCHEN_KNIFE.get());

        if (!isKnife) return;

        // 检查右键的方块是否是幽匿块或幽匿脉络
        boolean isSculk = state.is(Blocks.SCULK);
        boolean isSculkVein = state.is(Blocks.SCULK_VEIN);

        if (!isSculk && !isSculkVein) return;

        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;

            // 掉落幽匿真菌
            int fungusCount = isSculkVein ? 1 : (1 + RANDOM.nextInt(3));
            for (int i = 0; i < fungusCount; i++) {
                ItemEntity drop = new ItemEntity(
                        serverLevel,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        ModItems.SCULK_FUNGUS.get().getDefaultInstance()
                );
                drop.setDefaultPickUpDelay();
                serverLevel.addFreshEntity(drop);
            }

            // 掉落幽匿枝
            if (RANDOM.nextFloat() < SCULK_BRANCH_DROP_CHANCE) {
                ItemEntity branchDrop = new ItemEntity(
                        serverLevel,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        ModItems.SCULK_BRANCH.get().getDefaultInstance()
                );
                branchDrop.setDefaultPickUpDelay();
                serverLevel.addFreshEntity(branchDrop);
            }

            // 掉落幽匿猪儿虫（仅幽匿脉络）
            if (isSculkVein && RANDOM.nextFloat() < SCULK_CATERPILLAR_DROP_CHANCE) {
                ItemEntity caterpillarDrop = new ItemEntity(
                        serverLevel,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        ModItems.SCULK_CATERPILLAR.get().getDefaultInstance()
                );
                caterpillarDrop.setDefaultPickUpDelay();
                serverLevel.addFreshEntity(caterpillarDrop);
            }

            serverLevel.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0f, 1.0f);

            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            handItem.hurtAndBreak(2, player, slot);

            serverLevel.destroyBlock(pos, false);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}