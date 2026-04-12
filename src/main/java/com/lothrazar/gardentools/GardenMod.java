package com.lothrazar.gardentools;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(GardenMod.MODID)
public class GardenMod {

  public static final String MODID = "gardentools";
  public static final Logger LOGGER = LogManager.getLogger();

  public GardenMod(IEventBus bus, ModContainer modContainer) {
    modContainer.registerConfig(ModConfig.Type.COMMON, GardenConfigManager.CONFIG);
    GardenRegistry.BLOCKS.register(bus);
    GardenRegistry.ITEMS.register(bus);
    GardenRegistry.TILES.register(bus);
    GardenRegistry.CREATIVE_TABS.register(bus);
  }
}
