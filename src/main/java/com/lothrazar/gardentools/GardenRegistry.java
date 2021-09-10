package com.lothrazar.gardentools;

import com.lothrazar.gardentools.block.feeder.BlockFeeder;
import com.lothrazar.gardentools.block.feeder.TileFeeder;
import com.lothrazar.gardentools.block.irrigation.BlockIrrigation;
import com.lothrazar.gardentools.block.irrigation.TileIrrigation;
import com.lothrazar.gardentools.block.magnet.BlockMagnet;
import com.lothrazar.gardentools.block.magnet.TileMagnet;
import com.lothrazar.gardentools.block.rancher.BlockRancher;
import com.lothrazar.gardentools.block.rancher.TileRancher;
import com.lothrazar.gardentools.item.ItemFertilizer;
import com.lothrazar.gardentools.item.ItemPlanter;
import com.lothrazar.gardentools.item.ItemTiller;
import com.lothrazar.gardentools.item.ItemWatering;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GardenRegistry {

  public static CreativeModeTab TAB = new CreativeModeTab(GardenMod.MODID) {

    @Override
    public ItemStack makeIcon() {
      return new ItemStack(IRRIGATION);
    }
  };
  @SubscribeEvent
  public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
    // register a new block here
    IForgeRegistry<Block> r = event.getRegistry();
    r.register(new BlockIrrigation(Block.Properties.of(Material.STONE)).setRegistryName("irrigation_core"));
    r.register(new BlockRancher(Block.Properties.of(Material.METAL)).setRegistryName("rancher"));
    r.register(new BlockFeeder(Block.Properties.of(Material.METAL)).setRegistryName("feeder"));
    r.register(new BlockMagnet(Block.Properties.of(Material.METAL)).setRegistryName("magnet"));
  }

  @SubscribeEvent
  public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
    IForgeRegistry<Item> r = event.getRegistry();
    r.register(new BlockItem(GardenRegistry.RANCHER, new Item.Properties().tab(GardenRegistry.TAB)).setRegistryName("rancher"));
    r.register(new BlockItem(GardenRegistry.FEEDER, new Item.Properties().tab(GardenRegistry.TAB)).setRegistryName("feeder"));
    r.register(new BlockItem(GardenRegistry.IRRIGATION, new Item.Properties().tab(GardenRegistry.TAB)).setRegistryName("irrigation_core"));
    r.register(new ItemTiller(Tiers.GOLD, new Item.Properties().tab(GardenRegistry.TAB).stacksTo(1).durability(777)).setRegistryName("cultivator"));
    r.register(new ItemWatering(new Item.Properties().tab(GardenRegistry.TAB).stacksTo(1)).setRegistryName("watering"));
    r.register(new ItemFertilizer(new Item.Properties().tab(GardenRegistry.TAB)).setRegistryName("fertilizer"));
    r.register(new ItemPlanter(new Item.Properties().tab(GardenRegistry.TAB).stacksTo(1).durability(777)).setRegistryName("planter"));
    r.register(new BlockItem(GardenRegistry.MAGNET, new Item.Properties().tab(GardenRegistry.TAB)).setRegistryName("magnet"));
  }

  @SubscribeEvent
  public static void onTileEntityRegistry(RegistryEvent.Register<BlockEntityType<?>> event) {
    IForgeRegistry<BlockEntityType<?>> r = event.getRegistry();
    r.register(BlockEntityType.Builder.of(TileIrrigation::new, GardenRegistry.IRRIGATION).build(null).setRegistryName("irrigation_core"));
    r.register(BlockEntityType.Builder.of(TileRancher::new, GardenRegistry.RANCHER).build(null).setRegistryName("rancher"));
    r.register(BlockEntityType.Builder.of(TileFeeder::new, GardenRegistry.FEEDER).build(null).setRegistryName("feeder"));
    r.register(BlockEntityType.Builder.of(TileMagnet::new, GardenRegistry.MAGNET).build(null).setRegistryName("magnet"));
  }
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
