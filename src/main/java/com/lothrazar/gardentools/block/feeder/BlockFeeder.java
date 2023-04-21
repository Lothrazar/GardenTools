package com.lothrazar.gardentools.block.feeder;

import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.library.block.BaseEntityBlockFlib;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockFeeder extends BaseEntityBlockFlib {

  public BlockFeeder(Properties properties) {
    super(properties.strength(1.3F).noOcclusion());
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileFeeder(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, GardenRegistry.TE_FEEDER.get(), world.isClientSide ? TileFeeder::clientTick : TileFeeder::serverTick);
  }
}
