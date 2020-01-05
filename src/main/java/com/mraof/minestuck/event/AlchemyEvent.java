package com.mraof.minestuck.event;

import com.mraof.minestuck.item.crafting.alchemy.GristSet;
import com.mraof.minestuck.util.IdentifierHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

@SuppressWarnings("unused")
public class AlchemyEvent extends Event
{
	private final IdentifierHandler.PlayerIdentifier player;
	private final TileEntity alchemiter;
	private final ItemStack dowel;
	private final ItemStack result;
	private final GristSet cost;
	
	public AlchemyEvent(IdentifierHandler.PlayerIdentifier player, TileEntity alchemiter, ItemStack dowel, ItemStack result, GristSet cost)
	{
		this.player = player;
		this.alchemiter = alchemiter;
		this.dowel = dowel;
		this.result = result;
		this.cost = cost.asImmutable();
	}
	
	public IdentifierHandler.PlayerIdentifier getPlayer()
	{
		return player;
	}
	
	/**
	 * Returns the alchemiter tile entity that this is happening on. Either an instance of {@link com.mraof.minestuck.tileentity.AlchemiterTileEntity} or {@link com.mraof.minestuck.tileentity.MiniAlchemiterTileEntity}.
	 */
	public TileEntity getAlchemiter()
	{
		return alchemiter;
	}
	
	public World getWorld()
	{
		return alchemiter.getWorld();
	}
	
	public ItemStack getDowel()
	{
		return dowel.copy();
	}
	
	public ItemStack getItemResult()
	{
		return result.copy();
	}
	
	public GristSet getCost()
	{
		return cost;
	}
}