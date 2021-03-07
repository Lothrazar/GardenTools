package com.lothrazar.gardentools;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.nio.file.Path;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigManager {

  private static final ForgeConfigSpec.Builder CFG = new ForgeConfigSpec.Builder();
  private static ForgeConfigSpec COMMON_CONFIG;
  private static IntValue TILLING_RANGE;
  private static IntValue IRRIG_RANGE;
  private static IntValue MOISTURE;
  private static IntValue PLANTER_RANGE;
  public static IntValue WATERING_RANGE;
  public static IntValue FERT_POWER;
  public static IntValue FEEDER_RANGE;
  public static IntValue RANCHER_RANGE;
  public static DoubleValue SPEEDCLOSE;
  public static DoubleValue SPEEDFAR;
  public static IntValue MAGNET_RANGE;
  public static BooleanValue WATERSRC;
  static {
    initConfig();
  }

  private static void initConfig() {
    CFG.comment("General settings").push(GardenMod.MODID);
    //cultivator
    TILLING_RANGE = CFG.comment("\r\nRange of cultivator item").defineInRange("cultivator.range", 9, 2, 32);
    //planter
    PLANTER_RANGE = CFG.comment("\r\nRange of planter item").defineInRange("planter.range", 9, 2, 32);
    //    irrigation_core
    IRRIG_RANGE = CFG.comment("\r\nWatering radius of irrigation block").defineInRange("irrigator.radius", 8, 1, 64);
    WATERSRC = CFG.comment("\r\nIs this a water source for buckets and capabilities").define("irrigator.water_source", true);
    //cultivator
    MOISTURE = CFG.comment("\r\nMoisture level set by cultivator").defineInRange("cultivator.moisture", 7, 0, 7);
    //watering range
    WATERING_RANGE = CFG.comment("\r\nWatering can range").defineInRange("watering.range", 4, 1, 32);
    //watering percentage
    //fertilizer count==power
    FERT_POWER = CFG.comment("\r\nThe bonemeal-power of the fertilizer").defineInRange("fertilizer.power", 6, 1, 32);
    //feeder range
    FEEDER_RANGE = CFG.comment("\r\nLivestock Feeder range").defineInRange("feeder.range", 8, 1, 32);
    //rancher range
    RANCHER_RANGE = CFG.comment("\r\nLivestock Rancher range").defineInRange("rancher.range", 8, 1, 32);
    //magnet
    MAGNET_RANGE = CFG.comment("\r\nMagnet max range").defineInRange("magnet.range", 16, 1, 256);
    SPEEDCLOSE = CFG.comment("\r\nMagnet speed when items are close within 3 blocks").defineInRange("magnet.speed.close", 0.07F, 0.01F, 0.5F);
    SPEEDCLOSE = CFG.comment("\r\nMagnet speed when items are farther out than 3 blocks").defineInRange("magnet.speed.far", 0.31F, 0.01F, 0.99F);
    CFG.pop();
    COMMON_CONFIG = CFG.build();
  }

  public ConfigManager(Path path) {
    final CommentedFileConfig configData = CommentedFileConfig.builder(path)
        .sync()
        .autosave()
        .writingMode(WritingMode.REPLACE)
        .build();
    configData.load();
    COMMON_CONFIG.setConfig(configData);
  }

  public int getTillingRange() {
    return TILLING_RANGE.get();
  }

  public int getPlantingRange() {
    return PLANTER_RANGE.get();
  }

  public int getIrrigationRange() {
    return IRRIG_RANGE.get();
  }

  public int getMoisture() {
    return MOISTURE.get();
  }
}
