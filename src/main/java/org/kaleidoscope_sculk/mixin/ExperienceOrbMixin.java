//// java/org/kaleidoscope_sculk/mixin/ExperienceOrbMixin.java
//package org.kaleidoscope_sculk.mixin;
//
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.world.entity.ExperienceOrb;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import org.kaleidoscope_sculk.item.SoulSailItem;
//import org.kaleidoscope_sculk.register.ModItems;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(ExperienceOrb.class)
//public class ExperienceOrbMixin {
//
//    @Shadow
//    public int value;
//
//    @Shadow
//    public int age;
//
//    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
//    private void onPlayerTouch(Player player, CallbackInfo ci) {
//        // 如果是刚生成的经验球（小于5tick），跳过
//        if (this.age < 5) {
//            return;
//        }
//
//        // 查找魂幡
//        ItemStack targetSail = findSoulSail(player);
//        if (targetSail == null) return;
//
//        // 检查魂幡是否已满
//        int storedXp = SoulSailItem.getStoredXp(targetSail);
//        int maxXp = SoulSailItem.getSailType(targetSail).maxXp;
//        if (storedXp >= maxXp) return;
//
//        ExperienceOrb orb = (ExperienceOrb) (Object) this;
//        int xpPoints = this.value;
//        int canStore = Math.min(xpPoints, maxXp - storedXp);
//
//        if (canStore > 0) {
//            // 存入经验
//            SoulSailItem.setStoredXp(targetSail, storedXp + canStore);
//
//            // 移除经验球
//            orb.discard();
//
//            // 播放音效
//            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.2f, 1.8f);
//
//            // 取消原方法执行，防止玩家获得经验
//            ci.cancel();
//
//            // 如果有剩余经验，生成新的经验球
//            int remainingXp = xpPoints - canStore;
//            if (remainingXp > 0) {
//                ExperienceOrb newOrb = new ExperienceOrb(
//                        player.level(),
//                        orb.getX(),
//                        orb.getY() + 0.5,
//                        orb.getZ(),
//                        remainingXp
//                );
//                newOrb.age = 5; // 设置年龄避免被立即吸收
//                player.level().addFreshEntity(newOrb);
//            }
//        }
//    }
//
//    private ItemStack findSoulSail(Player player) {
//        ItemStack mainHand = player.getMainHandItem();
//        if (mainHand.is(ModItems.SOUL_SAIL.get())) {
//            return mainHand;
//        }
//
//        ItemStack offhand = player.getOffhandItem();
//        if (offhand.is(ModItems.SOUL_SAIL.get())) {
//            return offhand;
//        }
//
//        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
//            ItemStack stack = player.getInventory().getItem(i);
//            if (stack.is(ModItems.SOUL_SAIL.get())) {
//                return stack;
//            }
//        }
//
//        return null;
//    }
//}