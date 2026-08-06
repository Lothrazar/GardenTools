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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = GardenMod.MODID)
public class GardenRegistry {

  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, GardenMod.MODID);
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, GardenMod.MODID);
  public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GardenMod.MODID);
  public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GardenMod.MODID);

  public static final DeferredHolder<Block, BlockIrrigation> IRRIGATION_CORE = BLOCKS.register("irrigation_core", () -> new BlockIrrigation(Block.Properties.of()));
  public static final DeferredHolder<Block, BlockRancher> RANCHER = BLOCKS.register("rancher", () -> new BlockRancher(Block.Properties.of()));
  public static final DeferredHolder<Block, BlockFeeder> FEEDER = BLOCKS.register("feeder", () -> new BlockFeeder(Block.Properties.of()));
  public static final DeferredHolder<Block, BlockMagnet> MAGNET = BLOCKS.register("magnet", () -> new BlockMagnet(Block.Properties.of()));
  //
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileIrrigation>> TE_IRRIGATION_CORE = TILES.register("irrigation_core", () -> new BlockEntityType<>(TileIrrigation::new, IRRIGATION_CORE.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileRancher>> TE_RANCHER = TILES.register("rancher", () -> new BlockEntityType<>(TileRancher::new, RANCHER.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileFeeder>> TE_FEEDER = TILES.register("feeder", () -> new BlockEntityType<>(TileFeeder::new, FEEDER.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileMagnet>> TE_MAGNET = TILES.register("magnet", () -> new BlockEntityType<>(TileMagnet::new, MAGNET.get()));
  //
  public static final DeferredHolder<Item, BlockItem> I_IRRIGATION_CORE = ITEMS.register("irrigation_core", () -> new BlockItem(IRRIGATION_CORE.get(), new Item.Properties().useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> I_RANCHER = ITEMS.register("rancher", () -> new BlockItem(RANCHER.get(), new Item.Properties().useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> I_FEEDER = ITEMS.register("feeder", () -> new BlockItem(FEEDER.get(), new Item.Properties().useBlockDescriptionPrefix()));
  public static final DeferredHolder<Item, BlockItem> I_MAGNET = ITEMS.register("magnet", () -> new BlockItem(MAGNET.get(), new Item.Properties().useBlockDescriptionPrefix()));
  // items
  public static final DeferredHolder<Item, ItemTiller> CULTIVATOR = ITEMS.register("cultivator", () -> new ItemTiller(new Item.Properties()));
  public static final DeferredHolder<Item, ItemWatering> WATERING = ITEMS.register("watering", () -> new ItemWatering(new Item.Properties()));
  public static final DeferredHolder<Item, ItemFertilizer> FERTILIZER = ITEMS.register("fertilizer", () -> new ItemFertilizer(new Item.Properties()));
  public static final DeferredHolder<Item, ItemPlanter> PLANTER = ITEMS.register("planter", () -> new ItemPlanter(new Item.Properties()));

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register("tab",
      () -> CreativeModeTab.builder()
          .icon(() -> new ItemStack(I_IRRIGATION_CORE.get()))
          .title(Component.translatable("itemGroup." + GardenMod.MODID))
          .displayItems((enabledFlags, populator) -> {
            for (var entry : ITEMS.getEntries()) {
              populator.accept(entry.get());
            }
          }).build());

  @SubscribeEvent
  public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        TE_IRRIGATION_CORE.get(),
        (tile, side) -> {
          IFluidHandler handler = tile.getFluidHandler();
          return handler == null ? null : new com.lothrazar.gardentools.block.irrigation.IFluidHandlerResourceHandler(handler);
        }
    );
  }
}
