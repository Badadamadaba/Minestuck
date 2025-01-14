package com.mraof.minestuck.block;

import com.mraof.minestuck.MinestuckConfig;
import com.mraof.minestuck.tileentity.GateTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GateBlock extends Block
{
	
	protected static final VoxelShape SHAPE = Block.box(0.0D, 7.0D, 0.0D, 16.0D, 9.0D, 16.0D);
	public static BooleanProperty MAIN = MSProperties.MAIN;
	
	public GateBlock(Properties properties)
	{
		super(properties);
		registerDefaultState(defaultBlockState().setValue(MAIN, false));
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}
	
	@Override
	public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager)
	{
		return true;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public BlockRenderType getRenderShape(BlockState state)
	{
		return BlockRenderType.INVISIBLE;
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(MAIN);
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new GateTileEntity();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return state.getValue(MAIN);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
	{
		if(entityIn instanceof ServerPlayerEntity)
		{
			BlockPos mainPos = pos;
			if(!state.getValue(MAIN))
			{
				if(this != MSBlocks.GATE)
					mainPos = this.findMainComponent(pos, worldIn);
				else return;
			}
			
			if(mainPos != null)
			{
				TileEntity te = worldIn.getBlockEntity(mainPos);
				if(te instanceof GateTileEntity)
					((GateTileEntity) te).onCollision((ServerPlayerEntity) entityIn);
			} else worldIn.removeBlock(pos, false);
		}
	}
	
	protected boolean isValid(BlockPos pos, World world, BlockState state)
	{
		if(state.getValue(MAIN))
			return isValid(pos, world);
		else
		{
			BlockPos mainPos = findMainComponent(pos, world);
			if(mainPos != null)
				return isValid(mainPos, world);
			else return false;
		}
	}
	
	protected boolean isValid(BlockPos pos, World world)
	{
		for(int x = -1; x <= 1; x++)
			for(int z = -1; z <= 1; z++)
				if(x != 0 || z != 0)
				{
					BlockState block = world.getBlockState(pos.offset(x, 0, z));
					if(block.getBlock() != this || block.getValue(MAIN))
						return false;
				}
		
		return true;
	}
	
	protected void removePortal(BlockPos pos, World world)
	{
		for(int x = -1; x <= 1; x++)
			for(int z = -1; z <= 1; z++)
				if(world.getBlockState(pos.offset(x, 0, z)).getBlock() == this)
					world.removeBlock(pos.offset(x, 0, z), false);
	}
	
	protected BlockPos findMainComponent(BlockPos pos, World world)
	{
		for(int x = -1; x <= 1; x++)
			for(int z = -1; z <= 1; z++)
				if(x != 0 || z != 0)
				{
					BlockState block = world.getBlockState(pos.offset(x, 0, z));
					if(block.getBlock() == this && block.getValue(MAIN))
						return pos.offset(x, 0, z);
				}
		
		return null;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onRemove(state, worldIn, pos, newState, isMoving);
		if(state.getValue(MAIN))
			removePortal(pos, worldIn);
		else
		{
			BlockPos mainPos = findMainComponent(pos, worldIn);
			if(mainPos != null)
				removePortal(mainPos, worldIn);
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
		if(!this.isValid(pos, worldIn, state))
		{
			BlockPos mainPos = pos;
			if(!state.getValue(MAIN))
				mainPos = findMainComponent(pos, worldIn);
			
			if(mainPos == null)
				worldIn.removeBlock(pos, false);
			else removePortal(mainPos, worldIn);
		}
	}
	
	@Override
	public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion)
	{
		if(this instanceof ReturnNodeBlock || MinestuckConfig.SERVER.canBreakGates.get())
			return super.getExplosionResistance(state, world, pos, explosion);
		else return 3600000.0F;
	}
}