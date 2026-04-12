package com.lothrazar.gardentools.block.magnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.lothrazar.gardentools.GardenConfigManager;
import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.library.core.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class TileMagnet extends BlockEntity {

  private static final float ITEMSPEEDFAR = 0.8F;
  private static final float ITEMSPEEDCLOSE = 0.08F;

  public TileMagnet(BlockPos pos, BlockState state) {
    super(GardenRegistry.TE_MAGNET.get(), pos, state);
  }

  public static <E extends BlockEntity> void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileMagnet tile) {
    if (level.isClientSide) {
      return;
    }
    BlockPos belowPos = tile.worldPosition.below();
    BlockEntity below = level.getBlockEntity(belowPos);
    Set<Item> filter = new HashSet<>();
    if (below != null) {
      IItemHandler hopper = level.getCapability(Capabilities.ItemHandler.BLOCK, belowPos, null);
      if (hopper != null) {
        filter.addAll(tile.getItemsInItemHandler(hopper));
        if (below instanceof HopperBlockEntity hopperTile) {
          filter.addAll(tile.getConnectedItemHandlerItems(level, hopperTile));
        }
      }
    }
    final int radius = GardenConfigManager.MAGNET_RANGE.get();
    int vradius = 0;
    int x = tile.worldPosition.getX();
    int y = tile.worldPosition.getY();
    int z = tile.worldPosition.getZ();
    AABB axisalignedbb = (new AABB(x, y, z, x + 1, y + 1, z + 1)).inflate(radius, vradius, radius);
    List<ItemEntity> list = level.getEntitiesOfClass(ItemEntity.class, axisalignedbb);
    pullEntityList(x + 0.2, y + 0.5, z + 0.2, true, list, filter);
  }

  private List<Item> getConnectedItemHandlerItems(Level level, HopperBlockEntity hopper) {
    Direction hopperFacing = hopper.getBlockState().getValue(HopperBlock.FACING);
    BlockPos connectedPos = hopper.getBlockPos().relative(hopperFacing);
    IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, connectedPos, hopperFacing.getOpposite());
    if (handler == null) {
      return Collections.emptyList();
    }
    return getItemsInItemHandler(handler);
  }

  private static final double ENTITY_PULL_DIST = 0.4;
  private static final double ENTITY_PULL_SPEED_CUTOFF = 3;

  public static int pullEntityList(double x, double y, double z, boolean towardsPos, List<ItemEntity> all, Set<Item> filter) {
    int moved = 0;
    double hdist, xDist, zDist;
    float speed;
    int direction = (towardsPos) ? 1 : -1;
    for (ItemEntity entity : all) {
      if (entity == null) {
        continue;
      }
      if (filter != null
          && !filter.isEmpty()
          && !filter.contains(entity.getItem().getItem())) {
        continue;
      }
      BlockPos p = entity.blockPosition();
      xDist = Math.abs(x - p.getX());
      zDist = Math.abs(z - p.getZ());
      hdist = Math.sqrt(xDist * xDist + zDist * zDist);
      if (hdist > ENTITY_PULL_DIST) {
        speed = (hdist > ENTITY_PULL_SPEED_CUTOFF) ? ITEMSPEEDFAR : ITEMSPEEDCLOSE;
        setEntityMotionFromVector(entity, x, y, z, direction * speed);
        moved++;
      }
    }
    return moved;
  }

  public static void setEntityMotionFromVector(Entity entity, double x, double y, double z, float modifier) {
    Vector3 originalPosVector = new Vector3(x, y, z);
    Vector3 entityVector = new Vector3(entity);
    Vector3 finalVector = originalPosVector.copy().subtract(entityVector);
    if (finalVector.mag() > 1) {
      finalVector.normalize();
    }
    double motionX = finalVector.x * modifier;
    double motionY = finalVector.y * modifier;
    double motionZ = finalVector.z * modifier;
    entity.setDeltaMovement(motionX, motionY, motionZ);
  }

  private List<Item> getItemsInItemHandler(IItemHandler itemHandler) {
    List<Item> filter = new ArrayList<>();
    if (itemHandler == null) {
      return filter;
    }
    for (int i = 0; i < itemHandler.getSlots(); i++) {
      ItemStack stack = itemHandler.getStackInSlot(i);
      if (!stack.isEmpty()) {
        filter.add(stack.getItem());
      }
    }
    return filter;
  }
}
