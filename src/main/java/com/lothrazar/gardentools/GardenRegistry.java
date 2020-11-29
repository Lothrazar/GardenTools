package com.lothrazar.gardentools;

import com.lothrazar.gardentools.block.TileMagnet;
import com.lothrazar.gardentools.block.feeder.TileFeeder;
import com.lothrazar.gardentools.block.irrigation.TileIrrigation;
import com.lothrazar.gardentools.block.rancher.TileRancher;
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
  @ObjectHolder(GardenMod.MODID + ":rancher")
  static Block rancher;
  @ObjectHolder(GardenMod.MODID + ":rancher")
  public static TileEntityType<TileRancher> rancherTile;
  @ObjectHolder(GardenMod.MODID + ":feeder")
  static Block feeder;
  @ObjectHolder(GardenMod.MODID + ":feeder")
  public static TileEntityType<TileFeeder> feederTile;
  @ObjectHolder(GardenMod.MODID + ":magnet")
  public static Block magnet;
  @ObjectHolder(GardenMod.MODID + ":magnet")
  public static TileEntityType<TileMagnet> magnetTile;
}
