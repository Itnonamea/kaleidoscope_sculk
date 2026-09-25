package Tequilacat.KaleidoscopeSculk.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.TeaTreeBlock;
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
import Tequilacat.KaleidoscopeSculk.register.ModItems;

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

        BlockPos clickedPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        int centerX = clickedPos.getX();
        int centerY = clickedPos.getY();
        int centerZ = clickedPos.getZ();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int breakCount = 0;
        for (int x = -HARVEST_RADIUS_XZ; x <= HARVEST_RADIUS_XZ; x++) {
            for (int y = 0; y <= HARVEST_HEIGHT; y++) {
                for (int z = -HARVEST_RADIUS_XZ; z <= HARVEST_RADIUS_XZ; z++) {
                    mutablePos.set(centerX + x, centerY + y, centerZ + z);
                    if (harvest(level, player, stack, mutablePos)) {
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
        if (breakCount > 0) {
            stack.hurtAndBreak(breakCount, player, EquipmentSlot.MAINHAND);
        }
        player.getCooldowns().addCooldown(this, 7);
        return InteractionResult.SUCCESS;
    }

    private boolean harvest(Level level, Player player, ItemStack stack, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        }
        if (!level.mayInteract(player, pos)) {
            return false;
        }
        if (blockState.is(TagMod.SICKLE_HARVEST_BLACKLIST)) {
            return false;
        }

        Block block = blockState.getBlock();
        BlockPos targetPos = pos.immutable();

        SickleHarvestEvent event = new SickleHarvestEvent(player, stack, targetPos, blockState);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return event.isCostDurability();
        }

        if (block instanceof TeaTreeBlock teaTreeBlock) {
            if (teaTreeBlock.isMaxAge(blockState)) {
                teaTreeBlock.playerDestroy(level, player, targetPos, blockState, null, ItemStack.EMPTY);
                level.setBlock(targetPos, teaTreeBlock.getStateForAge(0), Block.UPDATE_ALL);
                level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, targetPos, Block.getId(blockState));
                return true;
            }
            return false;
        }

        if (block instanceof CropBlock cropBlock) {
            if (block instanceof RiceCropBlock) {
                int position = blockState.getValue(RiceCropBlock.LOCATION);
                targetPos = targetPos.below(position);
                blockState = level.getBlockState(targetPos);
            }
            if (cropBlock.isMaxAge(blockState)) {
                cropBlock.playerDestroy(level, player, targetPos, blockState, null, ItemStack.EMPTY);
                BlockState stateForAge = cropBlock.getStateForAge(0);
                BooleanProperty waterlogged = BlockStateProperties.WATERLOGGED;
                if (stateForAge.hasProperty(waterlogged)) {
                    stateForAge = stateForAge.setValue(waterlogged, blockState.getValue(waterlogged));
                }
                level.setBlock(targetPos, stateForAge, Block.UPDATE_ALL);
                level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, targetPos, Block.getId(blockState));
                return true;
            }
            return false;
        }

        if (block instanceof BushBlock && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.gameMode.destroyBlock(targetPos);
            level.levelEvent(null, LevelEvent.PARTICLES_DESTROY_BLOCK, targetPos, Block.getId(blockState));
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.kaleidoscope_sculk.sculk_bone_sickle").withStyle(ChatFormatting.GRAY));
    }
}
