package com.lothrazar.gardentools.block.irrigation;

import com.lothrazar.gardentools.GardenMod;
import com.lothrazar.gardentools.GardenRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.AABBTicket;

public class TileIrrigation extends TileEntity {

  private AABBTicket farmlandTicket;

  public TileIrrigation() {
    super(GardenRegistry.IRRIGATIONTILE);
  }

  @Override
  public void onLoad() {
    if (!world.isRemote) {
      AxisAlignedBB box = new AxisAlignedBB(pos);
      box = box.grow(GardenMod.CONFIG.getIrrigationRange());
      //      System.setProperty("forge.debugFarmlandWaterManager", "true");
      farmlandTicket = FarmlandWaterManager.addAABBTicket(world, box);
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
