package com.lothrazar.gardentools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.gardentools.block.BlockIrrigation;
import com.lothrazar.gardentools.block.TileIrrigation;
import com.lothrazar.gardentools.item.ItemFertilizer;
import com.lothrazar.gardentools.item.ItemPlanter;
import com.lothrazar.gardentools.item.ItemTiller;
import com.lothrazar.gardentools.item.ItemWatering;
import com.lothrazar.gardentools.rancher.BlockRancher;
import com.lothrazar.gardentools.rancher.TileRancher;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(GardenMod.MODID)
public class GardenMod {

  public static final String MODID = "gardentools";
  public static final String certificateFingerprint = "@FINGERPRINT@";
  public static final Logger LOGGER = LogManager.getLogger();
  public static ConfigManager config;

  public GardenMod() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    //only for server starting
    MinecraftForge.EVENT_BUS.register(this);
    config = new ConfigManager(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
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
      r.register(new BlockIrrigation(Block.Properties.create(Material.EARTH)).setRegistryName("irrigation_core"));
      r.register(new BlockRancher(Block.Properties.create(Material.IRON)).setRegistryName("rancher"));
    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
      IForgeRegistry<Item> r = event.getRegistry();
      r.register(new BlockItem(GardenRegistry.rancher, new Item.Properties().group(GardenRegistry.itemGroup).maxDamage(0).maxStackSize(64)).setRegistryName("rancher"));
      r.register(new BlockItem(GardenRegistry.irrigation, new Item.Properties().group(GardenRegistry.itemGroup).maxDamage(0).maxStackSize(64)).setRegistryName("irrigation_core"));
      r.register(new ItemTiller(ItemTier.GOLD, new Item.Properties().group(GardenRegistry.itemGroup).maxStackSize(1).maxDamage(777)).setRegistryName("cultivator"));
      r.register(new ItemWatering(new Item.Properties().group(GardenRegistry.itemGroup).maxStackSize(1).maxDamage(777)).setRegistryName("watering"));
      r.register(new ItemFertilizer(new Item.Properties().group(GardenRegistry.itemGroup)).setRegistryName("fertilizer"));
      r.register(new ItemPlanter(new Item.Properties().group(GardenRegistry.itemGroup).maxStackSize(1).maxDamage(777)).setRegistryName("planter"));
      //JOBS
      // cultivate field
      // water field
      // plant
      // ? harvest maybe or maybe not
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
      IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
      r.register(TileEntityType.Builder.create(TileIrrigation::new, GardenRegistry.irrigation).build(null).setRegistryName("irrigation_core"));
      r.register(TileEntityType.Builder.create(TileRancher::new, GardenRegistry.rancher).build(null).setRegistryName("rancher"));
    }
  }
}
