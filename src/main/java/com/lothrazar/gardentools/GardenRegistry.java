package com.lothrazar.gardentools;

import com.lothrazar.gardentools.block.TileIrrigation;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class GardenRegistry {

  public static ItemGroup itemGroup = new ItemGroup(GardenMod.MODID) {

    @Override
    public ItemStack createIcon() {
      return new ItemStack(irrigation);
    }
  };
  @ObjectHolder(GardenMod.MODID + ":irrigation_core")
  static Block irrigation;
  @ObjectHolder(GardenMod.MODID + ":irrigation_core")
  public static TileEntityType<TileIrrigation> irrigationTile;
}
