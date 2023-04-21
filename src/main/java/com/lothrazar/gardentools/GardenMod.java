package com.lothrazar.gardentools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GardenMod.MODID)
public class GardenMod {

  public static final String MODID = "gardentools";
  public static final Logger LOGGER = LogManager.getLogger();

  public GardenMod() {
    new GardenConfigManager();
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    GardenRegistry.BLOCKS.register(bus);
    GardenRegistry.ITEMS.register(bus);
    GardenRegistry.TILES.register(bus);
  }
}
