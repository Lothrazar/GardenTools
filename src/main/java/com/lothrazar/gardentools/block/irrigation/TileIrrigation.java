package com.lothrazar.gardentools.block.irrigation;

import com.lothrazar.gardentools.GardenConfigManager;
import com.lothrazar.gardentools.GardenRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TileIrrigation extends BlockEntity {

  private AABBTicket farmlandTicket;
  FluidTank tank;
  private final LazyOptional<FluidTank> tankWrapper = LazyOptional.of(() -> tank);

  public TileIrrigation(BlockPos pos, BlockState state) {
    super(GardenRegistry.TE_IRRIGATION_CORE.get(), pos, state);
    tank = new FluidTank(FluidType.BUCKET_VOLUME);
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (GardenConfigManager.WATERSRC.get() && cap == ForgeCapabilities.FLUID_HANDLER) {
      return tankWrapper.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void onLoad() {
    super.onLoad();
    if (!level.isClientSide) {
      AABB box = new AABB(worldPosition);
      box = box.inflate(GardenConfigManager.getIrrigationRange());
      System.setProperty("forge.debugFarmlandWaterManager", "true");
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

  public static <E extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileIrrigation tile) {
    tile.tank.fill(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME), FluidAction.EXECUTE);
    if (tile.farmlandTicket == null) {
      AABB box = new AABB(tile.worldPosition);
      box = box.inflate(GardenConfigManager.getIrrigationRange());
      tile.farmlandTicket = FarmlandWaterManager.addAABBTicket(level, box);
      tile.farmlandTicket.validate();
    }
  }
}
