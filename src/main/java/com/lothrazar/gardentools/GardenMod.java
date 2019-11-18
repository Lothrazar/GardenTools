package com.lothrazar.gardentools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.gardentools.block.BlockIrrigation;
import com.lothrazar.gardentools.block.TileIrrigation;
import com.lothrazar.gardentools.item.ItemTiller;
import com.lothrazar.gardentools.setup.ClientProxy;
import com.lothrazar.gardentools.setup.IProxy;
import com.lothrazar.gardentools.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;

// TODO: The value here should match an entry in the META-INF/mods.toml file
// TODO: Also search and replace it in build.gradle
@Mod(GardenMod.MODID)
public class GardenMod {

  public static final String MODID = "gardentools";
  public static final String certificateFingerprint = "@FINGERPRINT@";
  public static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
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
      event.getRegistry().register(new BlockIrrigation(Block.Properties.create(Material.EARTH)).setRegistryName("irrigation_core"));
    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> event) {
      IForgeRegistry<Item> r = event.getRegistry();
      Item.Properties properties = new Item.Properties().group(GardenRegistry.itemGroup);// tab group
      r.register(new BlockItem(GardenRegistry.irrigation, properties.maxDamage(0).maxStackSize(64)).setRegistryName("irrigation_core"));
      r.register(new ItemTiller(ItemTier.GOLD, properties.maxStackSize(1).maxDamage(8888)).setRegistryName("cultivator"));
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
    }
  }

  @SubscribeEvent
  public static void onFingerprintViolation(FMLFingerprintViolationEvent event) {
    // https://tutorials.darkhax.net/tutorials/jar_signing/
    String source = (event.getSource() == null) ? "" : event.getSource().getName() + " ";
    String msg = MODID + "Invalid fingerprint detected! The file " + source + "may have been tampered with. This version will NOT be supported by the author!";
    System.out.println(msg);
  }
}
