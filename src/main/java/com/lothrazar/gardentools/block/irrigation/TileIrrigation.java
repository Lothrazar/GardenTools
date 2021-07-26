package com.lothrazar.gardentools.block.irrigation;

import com.lothrazar.gardentools.ConfigManager;
import com.lothrazar.gardentools.GardenMod;
import com.lothrazar.gardentools.GardenRegistry;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TileIrrigation extends BlockEntity implements TickableBlockEntity {

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
    if (!level.isClientSide) {
      AABB box = new AABB(worldPosition);
      box = box.inflate(GardenMod.CONFIG.getIrrigationRange());
      //      System.setProperty("forge.debugFarmlandWaterManager", "true");
      farmlandTicket = FarmlandWaterManager.addAABBTicket(level, box);
      farmlandTicket.validate();
    }
  }

  @Override
  public void onChunkUnloaded() {
    if (!level.isClientSide && farmlandTicket != null) {
      farmlandTicket.invalidate();
    }
  }
}
