package org.kaleidoscope_sculk.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.SickleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.SimpleTier;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.List;

public class SculkBoneSickleItem extends SickleItem {
    private static final Tier SCULK_BONE_SICKLE_TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_STONE_TOOL,
            3000,
            5.0F,
            0.0F,
            12,
            () -> Ingredient.of(ModItems.ANCIENT_BONE_FRAGMENT.get())
    );

    private static final int HARVEST_RADIUS_XZ = 4;
    private static final int HARVEST_HEIGHT = 3;

    public SculkBoneSickleItem() {
        super(SCULK_BONE_SICKLE_TIER,
                new Item.Properties()
                        .durability(3000)
                        .attributes(SwordItem.createAttributes(SCULK_BONE_SICKLE_TIER, 6.0F, -1.6F)));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return super.useOn(context);
        }
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        int breakCount = 0;
        for (int x = -HARVEST_RADIUS_XZ; x <= HARVEST_RADIUS_XZ; x++) {
            for (int y = 0; y <= HARVEST_HEIGHT; y++) {
                for (int z = -HARVEST_RADIUS_XZ; z <= HARVEST_RADIUS_XZ; z++) {
                    if (harvest(pos, x, y, z, level, player, stack)) {
                        breakCount++;
                    }
                }
            }
        }

        serverLevel.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(),
                1.0F, 1.0F);
        player.sweepAttack();
        stack.hurtAndBreak(breakCount, player, EquipmentSlot.MAINHAND);
        player.getCooldowns().addCooldown(this, 7);
        return InteractionResult.SUCCESS;
    }

    private boolean harvest(BlockPos pos, int x, int y, int z, Level level, Player player, ItemStack stack) {
        BlockPos newPos = pos.offset(x, y, z);
        BlockState blockState = level.getBlockState(newPos);
        if (blockState.isAir()) {
            return false;
        }

        Block block = blockState.getBlock();
        if (!(block instanceof BushBlock)) {
            return false;
        }

        if (!level.mayInteract(player, newPos)) {
            return false;
        }
        if (blockState.is(TagMod.SICKLE_HARVEST_BLACKLIST)) {
            return false;
        }

        SickleHarvestEvent event = new SickleHarvestEvent(player, stack, newPos, blockState);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return event.isCostDurability();
        }

        if (block instanceof CropBlock cropBlock) {
            if (block instanceof RiceCropBlock) {
                int position = blockState.getValue(RiceCropBlock.LOCATION);
                newPos = newPos.below(position);
                blockState = level.getBlockState(newPos);
            }
            if (cropBlock.isMaxAge(blockState)) {
                cropBlock.playerDestroy(level, player, newPos, blockState, null, ItemStack.EMPTY);
                BlockState stateForAge = cropBlock.getStateForAge(0);
                BooleanProperty waterlogged = BlockStateProperties.WATERLOGGED;
                if (stateForAge.hasProperty(waterlogged)) {
                    stateForAge = stateForAge.setValue(waterlogged, blockState.getValue(waterlogged));
                }
                level.setBlock(newPos, stateForAge, Block.UPDATE_ALL);
                level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, newPos, Block.getId(blockState));
                return true;
            }
            return false;
        }

        if (block instanceof BushBlock) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.gameMode.destroyBlock(newPos);
                level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, newPos, Block.getId(blockState));
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.kaleidoscope_sculk.sculk_bone_sickle").withStyle(ChatFormatting.GRAY));
    }
}
