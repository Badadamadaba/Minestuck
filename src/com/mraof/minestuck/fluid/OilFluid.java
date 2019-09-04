package com.mraof.minestuck.fluid;

import com.mraof.minestuck.Minestuck;
import com.mraof.minestuck.block.MinestuckBlocks;
import com.mraof.minestuck.item.MinestuckItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class OilFluid extends FlowingFluid
{
	//Textures are not loaded right now, but that should be solved with https://github.com/MinecraftForge/MinecraftForge/pull/6110
	private static final ResourceLocation STILL_TEXTURE = new ResourceLocation(Minestuck.MOD_ID, "block/oil_still");
	private static final ResourceLocation FLOWING_TEXTURE = new ResourceLocation(Minestuck.MOD_ID, "block/oil_flowing");
	
	@Override
	public Fluid getFlowingFluid()
	{
		return ModFluids.FLOWING_OIL;
	}
	
	@Override
	public Fluid getStillFluid()
	{
		return ModFluids.OIL;
	}
	
	@Override
	protected boolean canSourcesMultiply()
	{
		return false;
	}
	
	@Override
	protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state)
	{
		TileEntity tileentity = state.hasTileEntity() ? worldIn.getTileEntity(pos) : null;
		Block.spawnDrops(state, worldIn.getWorld(), pos, tileentity);
	}
	
	@Override
	protected int getSlopeFindDistance(IWorldReader worldIn)
	{
		return 3;
	}
	
	@Override
	protected int getLevelDecreasePerBlock(IWorldReader worldIn)
	{
		return 1;
	}
	
	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}
	
	@Override
	public Item getFilledBucket()
	{
		return MinestuckItems.OIL_BUCKET;
	}
	
	@Override
	protected boolean func_215665_a(IFluidState state, IBlockReader worldIn, BlockPos pos, Fluid fluid, Direction direction)
	{
		return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
	}
	
	@Override
	public int getTickRate(IWorldReader worldIn)
	{
		return 10;
	}
	
	@Override
	protected float getExplosionResistance()
	{
		return 100F;
	}
	
	@Override
	protected BlockState getBlockState(IFluidState state)
	{
		return MinestuckBlocks.OIL.getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(state));
	}
	
	@Override
	public boolean isEquivalentTo(Fluid otherFluid)
	{
		return otherFluid == ModFluids.FLOWING_OIL || otherFluid == ModFluids.OIL;
	}
	
	@Override
	protected FluidAttributes createAttributes(Fluid fluid)
	{
		return FluidAttributes.builder("oil", STILL_TEXTURE, FLOWING_TEXTURE).build();
	}
	
	public static class Flowing extends OilFluid
	{
		public Flowing()
		{
			setDefaultState(getStateContainer().getBaseState().with(LEVEL_1_8, 7));
		}
		
		@Override
		protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder)
		{
			super.fillStateContainer(builder);
			builder.add(LEVEL_1_8);
		}
		
		@Override
		public boolean isSource(IFluidState state)
		{
			return false;
		}
		
		@Override
		public int getLevel(IFluidState state)
		{
			return state.get(LEVEL_1_8);
		}
	}
	
	public static class Source extends OilFluid
	{
		@Override
		public boolean isSource(IFluidState state)
		{
			return true;
		}
		
		@Override
		public int getLevel(IFluidState state)
		{
			return 8;
		}
	}
}