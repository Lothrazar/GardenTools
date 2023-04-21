package com.lothrazar.gardentools.block.magnet;

import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.library.block.BaseEntityBlockFlib;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockMagnet extends BaseEntityBlockFlib {

  static final VoxelShape SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 8.0D, 10.0D);

  public BlockMagnet(Properties properties) {
    super(properties.strength(1.3F).noOcclusion().lightLevel(state -> 7));
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileMagnet(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, GardenRegistry.TE_MAGNET.get(), world.isClientSide ? null : TileMagnet::serverTick);
  }
}
