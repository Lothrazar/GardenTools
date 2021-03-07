package com.lothrazar.gardentools.block.irrigation;

import com.lothrazar.gardentools.ConfigManager;
import com.lothrazar.gardentools.GardenMod;
import com.lothrazar.gardentools.GardenRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TileIrrigation extends TileEntity implements ITickableTileEntity {

  private AABBTicket farmlandTicket;
  FluidTank tank;
  private final LazyOptional<FluidTank> tankWrapper = LazyOptional.of(() -> tank);

  public TileIrrigation() {
    super(GardenRegistry.IRRIGATIONTILE);
    tank = new FluidTank(FluidAttributes.BUCKET_VOLUME);
  }

  @Override
  public void tick() {
    tank.fill(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME), FluidAction.EXECUTE);
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (ConfigManager.WATERSRC.get()
        && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return tankWrapper.cast();
    }
    return super.getCapability(cap, side);
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
