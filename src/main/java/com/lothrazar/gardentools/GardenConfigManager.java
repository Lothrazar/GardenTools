package com.lothrazar.gardentools;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class GardenConfigManager  {

  protected static ModConfigSpec CONFIG;
  private static IntValue TILLING_RANGE;
  private static IntValue IRRIG_RANGE;
  private static IntValue MOISTURE;
  private static IntValue PLANTER_RANGE;
  public static IntValue WATERING_RANGE;
  public static DoubleValue WATERING_POWER;
  public static IntValue FERT_POWER;
  public static IntValue FEEDER_RANGE;
  public static IntValue RANCHER_RANGE;
  public static DoubleValue SPEEDCLOSE;
  public static DoubleValue SPEEDFAR;
  public static IntValue MAGNET_RANGE;
  public static BooleanValue WATERSRC;
  static {
    final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    BUILDER.comment("General settings").push(GardenMod.MODID);
    //cultivator
    TILLING_RANGE = BUILDER.comment("\r\nRange of cultivator item").defineInRange("cultivator.range", 9, 2, 32);
    //planter
    PLANTER_RANGE = BUILDER.comment("\r\nRange of planter item").defineInRange("planter.range", 9, 2, 32);
    //    irrigation_core
    IRRIG_RANGE = BUILDER.comment("\r\nWatering radius of irrigation block").defineInRange("irrigator.radius", 8, 1, 64);
    WATERSRC = BUILDER.comment("\r\nIs this a water source for buckets and capabilities").define("irrigator.water_source", true);
    //cultivator
    MOISTURE = BUILDER.comment("\r\nMoisture level set by cultivator").defineInRange("cultivator.moisture", 7, 0, 7);
    //watering range
    WATERING_RANGE = BUILDER.comment("\r\nWatering can range").defineInRange("watering.range", 4, 1, 32);
    WATERING_POWER = BUILDER.comment("\r\nPower of the watering can: 0.1 is very weak and 0.99 is very strong").defineInRange("watering.power", 0.5F, 0.001F, 1F);
    //watering percentage
    //fertilizer count==power
    FERT_POWER = BUILDER.comment("\r\nThe bonemeal-power of the fertilizer").defineInRange("fertilizer.power", 6, 1, 32);
    //feeder range
    FEEDER_RANGE = BUILDER.comment("\r\nLivestock Feeder range").defineInRange("feeder.range", 8, 1, 32);
    //rancher range
    RANCHER_RANGE = BUILDER.comment("\r\nLivestock Rancher range").defineInRange("rancher.range", 8, 1, 32);
    //magnet
    MAGNET_RANGE = BUILDER.comment("\r\nMagnet max range").defineInRange("magnet.range", 16, 1, 256);
    SPEEDCLOSE = BUILDER.comment("\r\nMagnet speed when items are close within 3 blocks").defineInRange("magnet.speed.close", 0.07F, 0.01F, 0.5F);
    SPEEDFAR = BUILDER.comment("\r\nMagnet speed when items are farther out than 3 blocks").defineInRange("magnet.speed.far", 0.31F, 0.01F, 0.99F);
    BUILDER.pop();
    CONFIG = BUILDER.build();
  }

  public static int getTillingRange() {
    return TILLING_RANGE.get();
  }

  public static int getPlantingRange() {
    return PLANTER_RANGE.get();
  }

  public static int getIrrigationRange() {
    return IRRIG_RANGE.get();
  }

  public static int getMoisture() {
    return MOISTURE.get();
  }
}
