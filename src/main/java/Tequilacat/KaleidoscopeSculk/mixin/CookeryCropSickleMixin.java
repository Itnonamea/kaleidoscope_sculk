package Tequilacat.KaleidoscopeSculk.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.ChiliCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.TeaTreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.item.SickleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Cookery's crop blocks detect a sickle with the hardcoded {@code itemInHand.is(ModItems.SICKLE.get())}
 * instead of a tag or type check, so any third-party sickle extending {@link SickleItem} (such as this
 * mod's Sculk Bone Sickle) is not recognized: right-clicking a mature crop runs the block's own
 * single-plant harvest and returns SUCCESS, so stack.useOn() is never called and area harvesting fails.
 *
 * Here we hand the "any sickle in hand" case back to useOn at HEAD, matching Cookery's own sickle.
 * require = 0 keeps it a graceful degradation instead of a crash if the upstream structure changes.
 */
@Mixin({BaseCropBlock.class, ChiliCropBlock.class, TeaTreeBlock.class})
public abstract class CookeryCropSickleMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true, require = 0)
    private void kaleidoscope_sculk$letAnySickleHarvest(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                        Player player, InteractionHand hand, BlockHitResult hitResult,
                                                        CallbackInfoReturnable<ItemInteractionResult> cir) {
        if (stack.getItem() instanceof SickleItem) {
            cir.setReturnValue(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        }
    }
}
