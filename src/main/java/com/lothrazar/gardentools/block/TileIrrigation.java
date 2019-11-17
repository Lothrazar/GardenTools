package com.lothrazar.gardentools.block;

import com.lothrazar.gardentools.GardenRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.AABBTicket;

public class TileIrrigation extends TileEntity {

  private static final int RANGE = 4;
  private AABBTicket farmlandTicket;

  public TileIrrigation() {
    super(GardenRegistry.irrigationTile);
  }

  @Override
  public void onLoad() {
    if (!world.isRemote) {
      farmlandTicket = FarmlandWaterManager.addAABBTicket(world, new AxisAlignedBB(pos).grow(RANGE));
      farmlandTicket.validate();
    }
  }

  @Override
  public void onChunkUnloaded() {
    if (!world.isRemote && farmlandTicket != null) {
      farmlandTicket.invalidate();
    }
  }
}
