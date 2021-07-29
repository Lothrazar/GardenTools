package com.lothrazar.gardentools;

import com.lothrazar.gardentools.block.feeder.TileFeeder;
import com.lothrazar.gardentools.block.irrigation.TileIrrigation;
import com.lothrazar.gardentools.block.magnet.TileMagnet;
import com.lothrazar.gardentools.block.rancher.TileRancher;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class GardenRegistry {

  public static CreativeModeTab TAB = new CreativeModeTab(GardenMod.MODID) {

    @Override
    public ItemStack makeIcon() {
      return new ItemStack(IRRIGATION);
    }
  };
  @ObjectHolder(GardenMod.MODID + ":irrigation_core")
  static Block IRRIGATION;
  @ObjectHolder(GardenMod.MODID + ":irrigation_core")
  public static BlockEntityType<TileIrrigation> IRRIGATIONTILE;
  @ObjectHolder(GardenMod.MODID + ":rancher")
  static Block RANCHER;
  @ObjectHolder(GardenMod.MODID + ":rancher")
  public static BlockEntityType<TileRancher> RANCHERTILE;
  @ObjectHolder(GardenMod.MODID + ":feeder")
  static Block FEEDER;
  @ObjectHolder(GardenMod.MODID + ":feeder")
  public static BlockEntityType<TileFeeder> FEEDERTILE;
  @ObjectHolder(GardenMod.MODID + ":magnet")
  public static Block MAGNET;
  @ObjectHolder(GardenMod.MODID + ":magnet")
  public static BlockEntityType<TileMagnet> MAGNETTILE;
}
