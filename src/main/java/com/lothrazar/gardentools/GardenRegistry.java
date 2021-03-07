package com.lothrazar.gardentools;

import com.lothrazar.gardentools.block.feeder.TileFeeder;
import com.lothrazar.gardentools.block.irrigation.TileIrrigation;
import com.lothrazar.gardentools.block.magnet.TileMagnet;
import com.lothrazar.gardentools.block.rancher.TileRancher;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class GardenRegistry {

  public static ItemGroup TAB = new ItemGroup(GardenMod.MODID) {

    @Override
    public ItemStack createIcon() {
      return new ItemStack(IRRIGATION);
    }
  };
  @ObjectHolder(GardenMod.MODID + ":irrigation_core")
  static Block IRRIGATION;
  @ObjectHolder(GardenMod.MODID + ":irrigation_core")
  public static TileEntityType<TileIrrigation> IRRIGATIONTILE;
  @ObjectHolder(GardenMod.MODID + ":rancher")
  static Block RANCHER;
  @ObjectHolder(GardenMod.MODID + ":rancher")
  public static TileEntityType<TileRancher> RANCHERTILE;
  @ObjectHolder(GardenMod.MODID + ":feeder")
  static Block FEEDER;
  @ObjectHolder(GardenMod.MODID + ":feeder")
  public static TileEntityType<TileFeeder> FEEDERTILE;
  @ObjectHolder(GardenMod.MODID + ":magnet")
  public static Block MAGNET;
  @ObjectHolder(GardenMod.MODID + ":magnet")
  public static TileEntityType<TileMagnet> MAGNETTILE;
}
