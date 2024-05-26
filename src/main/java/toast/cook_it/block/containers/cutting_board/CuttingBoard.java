package toast.cook_it.block.containers.cutting_board;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import toast.cook_it.registries.CookItItems;

public class CuttingBoard extends HorizontalFacingBlock implements BlockEntityProvider {

    public CuttingBoard(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.EAST));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);

    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity == null) {
            return ActionResult.FAIL;
        }
        ItemStack heldItem = player.getStackInHand(hand);

        if (blockEntity.isEmpty()) {
            if (heldItem.getItem().equals(CookItItems.FRYER_BASKET)) {
                return ActionResult.FAIL;
            } else if (!heldItem.isEmpty()) {
                blockEntity.setStack(0, heldItem.split(1));
                return ActionResult.SUCCESS;
            }
        } else {
            if (blockEntity.isValidTool(heldItem.getItem())) {
                blockEntity.processRecipe(heldItem.getItem());
                return ActionResult.SUCCESS;
            }
            player.getInventory().insertStack(blockEntity.getStack(0));
            blockEntity.clear();
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        Direction dir = state.get(FACING);
        return switch (dir) {
            case EAST, WEST -> VoxelShapes.cuboid(0.0625f, 0f, -0.125f, 0.9375f, 0.0625f, 1.125f);
            case NORTH, SOUTH -> VoxelShapes.cuboid(-0.125f, 0f, 0.0625f, 1.125f, 0.0625f, 0.9375f);
            default -> VoxelShapes.fullCube();
        };
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardEntity(pos, state);
    }
}