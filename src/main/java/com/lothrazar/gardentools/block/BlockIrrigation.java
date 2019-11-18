package com.lothrazar.gardentools.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class BlockIrrigation extends Block {

  public BlockIrrigation(Properties properties) {
    super(properties.hardnessAndResistance(1.3F).harvestTool(ToolType.PICKAXE));
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileIrrigation();
  }
}
