//// java/org/kaleidoscope_sculk/block/SoulSailBlock.java
//package org.kaleidoscope_sculk.block;
//
//import net.minecraft.ChatFormatting;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.TooltipFlag;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.RenderShape;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.StateDefinition;
//import net.minecraft.world.level.block.state.properties.EnumProperty;
//import net.minecraft.world.level.storage.loot.LootParams;
//import net.minecraft.world.phys.shapes.CollisionContext;
//import net.minecraft.world.phys.shapes.VoxelShape;
//import net.minecraft.world.phys.shapes.Shapes;
//import org.kaleidoscope_sculk.item.SoulSailItem;
//import org.kaleidoscope_sculk.register.ModItems;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SoulSailBlock extends Block {
//
//    public static final EnumProperty<SoulSailItem.SailType> TYPE =
//            EnumProperty.create("type", SoulSailItem.SailType.class);
//
//    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
//
//    public SoulSailBlock(Properties properties) {
//        super(properties);
//        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, SoulSailItem.SailType.SOUL));
//    }
//
//    @Override
//    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
//        builder.add(TYPE);
//    }
//
//    @Override
//    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
//        return SHAPE;
//    }
//
//    @Override
//    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
//        return Shapes.empty();
//    }
//
//    @Override
//    public RenderShape getRenderShape(BlockState state) {
//        return RenderShape.MODEL;
//    }
//
//    @Override
//    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
//        return true;
//    }
//
//    @Override
//    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
//        SoulSailItem.SailType type = state.getValue(TYPE);
//        return switch (type) {
//            case SOUL -> 8;
//            case THOUSAND -> 12;
//            case MYRIAD -> 15;
//        };
//    }
//
//    @Override
//    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
//        super.appendHoverText(stack, context, tooltip, flag);
//        SoulSailItem.SailType type = SoulSailItem.getSailType(stack);
//        tooltip.add(Component.translatable("block.kaleidoscope_sculk.soul_sail_block." + type.name().toLowerCase() + ".tooltip")
//                .withStyle(ChatFormatting.GRAY));
//    }
//
//    @Override
//    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
//        List<ItemStack> drops = new ArrayList<>();
//        SoulSailItem.SailType type = state.getValue(TYPE);
//
//        ItemStack dropStack = new ItemStack(ModItems.SOUL_SAIL.get());
//        // 设置对应的类型
//        SoulSailItem.setSailData(dropStack, type, 0, 0);
//        drops.add(dropStack);
//        return drops;
//    }
//
//
//        // 不使用 @Override，因为 Block 的 getName 是 final 的
//    public Component getBlockName(ItemStack stack) {
//        SoulSailItem.SailType type = SoulSailItem.getSailType(stack);
//        String name = switch (type) {
//            case SOUL -> "魂幡";
//            case THOUSAND -> "千魂幡";
//            case MYRIAD -> "万魂幡";
//        };
//        return Component.literal(type.color + name);
//    }
//}