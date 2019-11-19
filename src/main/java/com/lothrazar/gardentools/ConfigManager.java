package com.lothrazar.gardentools;

import java.nio.file.Path;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigManager {

  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
  private static ForgeConfigSpec COMMON_CONFIG;
  private static IntValue TILLING_RANGE;
  private static IntValue IRRIG_RANGE;
  private static IntValue MOISTURE;
  private static IntValue PLANTER_RANGE;
  static {
    initConfig();
  }

  private static void initConfig() {
    COMMON_BUILDER.comment("General settings").push(GardenMod.MODID);
    TILLING_RANGE = COMMON_BUILDER.comment("Range of cultivator item").defineInRange("cultivator.range", 9, 2, 32);
    PLANTER_RANGE = COMMON_BUILDER.comment("Range of planter item").defineInRange("planter.range", 9, 2, 32);
    IRRIG_RANGE = COMMON_BUILDER.comment("Watering radius of irrigation block").defineInRange("irrigator.radius", 4, 1, 16);
    MOISTURE = COMMON_BUILDER.comment("Moisture level set by cultivator").defineInRange("cultivator.moisture", 7, 0, 7);
    COMMON_BUILDER.pop();
    COMMON_CONFIG = COMMON_BUILDER.build();
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
