package dev.mattrm.schoolsofmagic.common.block;

import dev.mattrm.schoolsofmagic.common.state.properties.TablePart;
import dev.mattrm.schoolsofmagic.common.util.MaterialBuilder;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapeCube;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class StudyTableBlockBase extends HorizontalBlock{
    public static final EnumProperty<TablePart> PART = EnumProperty.create("part", TablePart.class);

    public StudyTableBlockBase() {
        super(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.5F).notSolid().sound(SoundType.WOOD));
        this.setDefaultState(this.getDefaultState()
                .with(PART, TablePart.LEFT)
                .with(HORIZONTAL_FACING, Direction.NORTH));
    }

    /**
     * Called when fire is updating, checks if a block face can catch fire.
     *
     * @param state The current state
     * @param world The current world
     * @param pos   Block position in world
     * @param face  The face that the fire is coming from
     * @return True if the face can be on fire, false otherwise.
     */
    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.create(new AxisAlignedBB(0, 0, 0, 1, 0.625, 1));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getPlacementHorizontalFacing();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(direction.rotateY());
        return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this.getDefaultState().with(HORIZONTAL_FACING, direction.getOpposite()) : null;
    }

    @Override
    protected void fillStateContainer(final StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_FACING, PART);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TablePart part = state.get(PART);
        BlockPos blockpos = pos.offset(getDirectionToOther(part, state.get(HORIZONTAL_FACING)));
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() == this && blockstate.get(PART) != part) {
            world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
            world.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
            if (!world.isRemote && !player.isCreative()) {
                ItemStack itemstack = player.getHeldItemMainhand();
                spawnDrops(state, world, pos, (TileEntity) null, player, itemstack);
                spawnDrops(blockstate, world, blockpos, (TileEntity) null, player, itemstack);
            }

            player.addStat(Stats.BLOCK_MINED.get(this));
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    private static Direction getDirectionToOther(TablePart part, Direction dir) {
        return part == TablePart.LEFT ? dir.rotateYCCW() : dir.rotateY();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.onBlockPlacedBy(world, pos, state, entity, itemStack);

        if (!world.isRemote) {
            BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING).rotateYCCW());
            world.setBlockState(blockpos, state.with(PART, TablePart.RIGHT), 3);
            world.notifyNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, 3);
        }
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
        return true;
    }

    public boolean causesSuffocation(BlockState p_229869_1_, IBlockReader p_229869_2_, BlockPos p_229869_3_) {
        return false;
    }

    public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
        return false;
    }

    public boolean canEntitySpawn(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
        return false;
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (!stateIn.isValidPosition(worldIn, currentPos)) {
            return Blocks.AIR.getDefaultState();
        }

        if (facing == getDirectionToOther(stateIn.get(PART), stateIn.get(HORIZONTAL_FACING))) {
            return facingState.getBlock() == this && facingState.get(PART) != stateIn.get(PART) ? stateIn : Blocks.AIR.getDefaultState();
        } else {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSolid() && world.getBlockState(pos.offset(getDirectionToOther(state.get(PART), state.get(HORIZONTAL_FACING))).down()).isSolid();
    }
}
