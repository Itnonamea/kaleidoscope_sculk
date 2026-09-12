package org.kaleidoscope_sculk.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.kaleidoscope_sculk.register.ModItems;

import java.util.List;

public class EchoCropTopBlock extends Block {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    private static final int MATURE_AGE = 7;
    private static final int RESET_AGE = 0;

    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 13.0D, 10.0D, 13.0D);
    private static final VoxelShape EMPTY_SHAPE = Shapes.empty();

    public EchoCropTopBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return EMPTY_SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos,
                                       Player player) {
        return new ItemStack(ModItems.SCULK_PINAPPLE.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of();
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (!level.isClientSide && state.getValue(AGE) >= MATURE_AGE) {
            ServerLevel serverLevel = (ServerLevel) level;
            popExperience(serverLevel, pos, 5);

            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.getBlock() instanceof EchoCropBlock) {
                serverLevel.setBlock(belowPos, belowState.setValue(EchoCropBlock.AGE, RESET_AGE), Block.UPDATE_ALL);
            }
        }
    }

    private static boolean hasCropBelow(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.below()).getBlock() instanceof EchoCropBlock;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!hasCropBelow(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean isMoving) {
        if (!hasCropBelow(level, pos)) {
            level.destroyBlock(pos, true);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }
}
