package org.kaleidoscope_sculk.item;

import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.DrinkBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.blockentity.brew.DrinkBlockEntity;
import com.github.ysbbbbbb.kaleidoscopetavern.datamap.data.DrinkEffectData;
import com.github.ysbbbbbb.kaleidoscopetavern.datamap.resources.DrinkEffectDataReloadListener;
import com.github.ysbbbbbb.kaleidoscopetavern.item.BottleBlockItem;
import com.github.ysbbbbbb.kaleidoscopetavern.item.IHasContainer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.kaleidoscope_sculk.Kaleidoscope_sculk;

import java.util.List;

/**
 * 自定义酒瓶物品，复制 DrinkBlockItem 功能，但使用原版玻璃瓶
 */
public class SculkBrewItem extends BottleBlockItem implements IHasContainer {
    public SculkBrewItem(Block block) {
        // 使用原版 GLASS_BOTTLE 作为容器
        super(block, new Item.Properties().stacksTo(16).craftRemainder(net.minecraft.world.item.Items.GLASS_BOTTLE));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(pos);
        Block self = this.getBlock();

        // 如果玩家不潜行，尝试饮用
        if (player != null && !player.isShiftKeyDown()) {
            InteractionResult result = this.use(level, player, context.getHand()).getResult();
            return result == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : result;
        }

        // 潜行时放置酒瓶
        if (player != null && this.tryIncreaseCount(self, state, level, pos, stack, player)) {
            return InteractionResult.SUCCESS;
        }

        return this.place(new BlockPlaceContext(context));
    }

    private boolean tryIncreaseCount(Block self, BlockState state, Level level, BlockPos pos, ItemStack stack, Player player) {
        if (self instanceof DrinkBlock drink) {
            if (state.is(self) && drink.tryIncreaseCount(level, pos, state, stack)) {
                SoundType soundType = state.getSoundType(level, pos, player);
                SoundEvent sound = this.getPlaceSound(state, level, pos, player);
                level.playSound(player, pos, sound, SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DrinkBlockEntity) {
            DrinkBlockEntity drinkBe = (DrinkBlockEntity) be;
            if (drinkBe.addItem(stack)) {
                drinkBe.refresh();
            }
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public String getDescriptionId() {
        return "item." + Kaleidoscope_sculk.MODID + "." +
                this.builtInRegistryHolder().key().location().getPath();
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        this.addDrinkEffect(stack, level, entity);

        if (entity instanceof Player player) {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }

        return this.returnContainerToEntity(stack, level, entity);
    }

    protected void addDrinkEffect(ItemStack drink, Level level, LivingEntity entity) {
        DrinkEffectData effectData = DrinkEffectDataReloadListener.INSTANCE.get(drink.getItem());
        if (effectData != null) {
            List<List<DrinkEffectData.Entry>> effects = effectData.effects();
            if (!effects.isEmpty()) {
                int brewLevel = BottleBlockItem.getBrewLevel(drink);
                if (brewLevel >= 1) {
                    brewLevel = Math.min(brewLevel, effects.size());
                    for (DrinkEffectData.Entry entry : effects.get(brewLevel - 1)) {
                        if (!level.isClientSide && level.random.nextFloat() < entry.probability()) {
                            MobEffectInstance instance = new MobEffectInstance(entry.effect(), entry.duration() * 20, entry.amplifier());
                            entity.addEffect(instance);
                        }
                    }
                }
            }
        }
    }

    public void makeThrownPotion(Level level, double x, double y, double z, int brewLevel, @Nullable Entity owner) {
        DrinkEffectData effectData = DrinkEffectDataReloadListener.INSTANCE.get(this);
        if (effectData != null) {
            List<List<DrinkEffectData.Entry>> effects = effectData.effects();
            if (!effects.isEmpty()) {
                brewLevel = BottleBlockItem.clampBrewLevel(brewLevel);
                if (brewLevel >= 1) {
                    brewLevel = Math.min(brewLevel, effects.size());
                    List<MobEffectInstance> instances = new java.util.ArrayList<>();
                    for (DrinkEffectData.Entry entry : effects.get(brewLevel - 1)) {
                        if (level.random.nextFloat() < entry.probability()) {
                            instances.add(new MobEffectInstance(entry.effect(), entry.duration() * 20, entry.amplifier()));
                        }
                    }
                    ThrownPotion potion = new ThrownPotion(level, x, y, z);
                    if (owner instanceof LivingEntity) {
                        potion.setOwner((LivingEntity) owner);
                    }
                    ItemStack stack = new ItemStack(this);
                    PotionContents contents = new PotionContents(java.util.Optional.empty(), java.util.Optional.empty(), instances);
                    stack.set(DataComponents.POTION_CONTENTS, contents);
                    potion.setItem(stack);
                    level.addFreshEntity(potion);
                }
            }
        }
    }

    @Override
    public Item getContainerItem() {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM
                .get(ResourceLocation.parse("kaleidoscope_tavern:empty_bottle"));
    }

    // 实现 IHasContainer 接口
    @Override
    public ItemStack returnContainerToEntity(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack container = new ItemStack(this.getContainerItem());
            if (!player.getInventory().add(container)) {
                player.drop(container, false);
            }
        }
        return stack;
    }


}