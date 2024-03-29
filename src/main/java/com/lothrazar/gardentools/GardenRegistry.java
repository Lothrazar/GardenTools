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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GardenRegistry {

  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GardenMod.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GardenMod.MODID);
  public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GardenMod.MODID);
  private static final ResourceKey<CreativeModeTab> TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(GardenMod.MODID, "tab"));

  @SubscribeEvent
  public static void onCreativeModeTabRegister(RegisterEvent event) {
    event.register(Registries.CREATIVE_MODE_TAB, helper -> {
      helper.register(TAB, CreativeModeTab.builder().icon(() -> new ItemStack(I_IRRIGATION_CORE.get()))
          .title(Component.translatable("itemGroup." + GardenMod.MODID))
          .displayItems((enabledFlags, populator) -> {
            for (RegistryObject<Item> entry : ITEMS.getEntries()) {
              populator.accept(entry.get());
            }
          }).build());
    });
  }

  public static final RegistryObject<Block> IRRIGATION_CORE = BLOCKS.register("irrigation_core", () -> new BlockIrrigation(Block.Properties.of()));
  public static final RegistryObject<Block> RANCHER = BLOCKS.register("rancher", () -> new BlockRancher(Block.Properties.of()));
  public static final RegistryObject<Block> FEEDER = BLOCKS.register("feeder", () -> new BlockFeeder(Block.Properties.of()));
  public static final RegistryObject<Block> MAGNET = BLOCKS.register("magnet", () -> new BlockMagnet(Block.Properties.of()));
  //
  public static final RegistryObject<BlockEntityType<TileIrrigation>> TE_IRRIGATION_CORE = TILES.register("irrigation_core", () -> BlockEntityType.Builder.of(TileIrrigation::new, IRRIGATION_CORE.get()).build(null));
  public static final RegistryObject<BlockEntityType<TileRancher>> TE_RANCHER = TILES.register("rancher", () -> BlockEntityType.Builder.of(TileRancher::new, RANCHER.get()).build(null));
  public static final RegistryObject<BlockEntityType<TileFeeder>> TE_FEEDER = TILES.register("feeder", () -> BlockEntityType.Builder.of(TileFeeder::new, FEEDER.get()).build(null));
  public static final RegistryObject<BlockEntityType<TileMagnet>> TE_MAGNET = TILES.register("magnet", () -> BlockEntityType.Builder.of(TileMagnet::new, MAGNET.get()).build(null));
  //
  public static final RegistryObject<Item> I_IRRIGATION_CORE = ITEMS.register("irrigation_core", () -> new BlockItem(IRRIGATION_CORE.get(), new Item.Properties()));
  public static final RegistryObject<Item> I_RANCHER = ITEMS.register("rancher", () -> new BlockItem(RANCHER.get(), new Item.Properties()));
  public static final RegistryObject<Item> I_FEEDER = ITEMS.register("feeder", () -> new BlockItem(FEEDER.get(), new Item.Properties()));
  public static final RegistryObject<Item> I_MAGNET = ITEMS.register("magnet", () -> new BlockItem(MAGNET.get(), new Item.Properties()));
  // items
  public static final RegistryObject<Item> CULTIVATOR = ITEMS.register("cultivator", () -> new ItemTiller(Tiers.GOLD, new Item.Properties()));
  public static final RegistryObject<Item> WATERING = ITEMS.register("watering", () -> new ItemWatering(new Item.Properties()));
  public static final RegistryObject<Item> FERTILIZER = ITEMS.register("fertilizer", () -> new ItemFertilizer(new Item.Properties()));
  public static final RegistryObject<Item> PLANTER = ITEMS.register("planter", () -> new ItemPlanter(new Item.Properties()));
}
