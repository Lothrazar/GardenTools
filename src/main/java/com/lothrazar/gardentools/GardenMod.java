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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(GardenMod.MODID)
public class GardenMod {

  public static final String MODID = "gardentools";
  //  public static final String certificateFingerprint = "@FINGERPRINT@";
  public static final Logger LOGGER = LogManager.getLogger();
  public static ConfigManager CONFIG;

  public GardenMod() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    //only for server starting
    MinecraftForge.EVENT_BUS.register(this);
    CONFIG = new ConfigManager(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
  }

  private void setup(final FMLCommonSetupEvent event) {
    //now all blocks/items exist 
  }

  @SubscribeEvent
  public void onServerStarting(FMLServerStartingEvent event) {
    //you probably will not need this
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

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
  }
}
