package com.mraof.minestuck.world.lands;

import com.mraof.minestuck.item.crafting.alchemy.GristType;
import com.mraof.minestuck.item.crafting.alchemy.GristTypes;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;
import net.minecraftforge.registries.ForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class GristTypeLayer
{
	private final LazyArea area;
	
	private GristTypeLayer(LazyArea area)
	{
		this.area = area;
	}
	
	public static GristTypeLayer createLayer(GristType.SpawnCategory category, long seed, int zoomLevel, @Nullable GristType baseType)
	{
		IAreaFactory<LazyArea> layer = new BaseLayer(category, baseType).apply(new LazyAreaLayerContext(25, seed, 250L));
		layer = LayerUtil.repeat(2000L, ZoomLayer.NORMAL, layer, zoomLevel, value -> new LazyAreaLayerContext(25, seed, value));
		
		return new GristTypeLayer(layer.make());
	}
	
	public GristType getTypeAt(int posX, int posZ)
	{
		int gristId = area.getValue(posX, posZ);
		return ((ForgeRegistry<GristType>) GristTypes.getRegistry()).getValue(gristId);
	}
	
	private static class BaseLayer implements IAreaTransformer0
	{
		final List<GristEntry> gristTypes;
		final int weightSum;
		
		final int baseGristType;
		
		public BaseLayer(GristType.SpawnCategory category, @Nullable GristType type)
		{
			this.baseGristType = type == null ? -1 : ((ForgeRegistry<GristType>) GristTypes.getRegistry()).getID(type);
			gristTypes = GristTypes.values().stream().filter(GristType::isUnderlingType)
					.filter(gristType -> gristType.isInCategory(category)).map(GristEntry::new).collect(Collectors.toList());
			weightSum = WeightedRandom.getTotalWeight(gristTypes);
		}
		
		@Override
		public int apply(INoiseRandom context, int x, int z)
		{
			if(baseGristType != -1 && x * x + z * z <= 1)
				return baseGristType;
			
			return WeightedRandom.getRandomItem(gristTypes, context.random(weightSum)).gristId;
		}
		
		private static class GristEntry extends WeightedRandom.Item
		{
			private final int gristId;
			
			public GristEntry(GristType type)
			{
				super(Math.round(type.getRarity() * 100));
				this.gristId = ((ForgeRegistry<GristType>) GristTypes.getRegistry()).getID(type);
			}
		}
	}
}