package com.lothrazar.gardentools;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(GardenMod.MODID)
public class GardenMod {

  public static final String MODID = "gardentools";
  public static final Logger LOGGER = LogManager.getLogger();
  public static ConfigManager CONFIG;

  public GardenMod() {
    CONFIG = new ConfigManager(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
  }

}
