package com.mraof.minestuck.item.artifact;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CruxitePotionItem extends CruxiteArtifactItem
{
	public CruxitePotionItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public int getUseDuration(ItemStack stack)
	{
		return 32;
	}
	
	@Override
	public UseAction getUseAnimation(ItemStack stack)
	{
		return UseAction.DRINK;
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving)
	{
		stack.shrink(1);
		if(entityLiving instanceof ServerPlayerEntity)
			onArtifactActivated((ServerPlayerEntity) entityLiving);
		
		return stack;
	}
	
	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn)
	{
		playerIn.startUsingItem(handIn);
		return ActionResult.success(playerIn.getItemInHand(handIn));
	}
}