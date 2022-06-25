package com.lothrazar.gardentools.block.magnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import com.lothrazar.gardentools.ConfigManager;
import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.gardentools.block.Vector3;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

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
    BlockEntity below = level.getBlockEntity(tile.worldPosition.below());
    Set<Item> filter = new HashSet<>();
    if (below != null) {
      IItemHandler hopper = below.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
      if (hopper != null) {
        filter.addAll(tile.getItemsInItemHandler(hopper));
        if (below instanceof HopperBlockEntity) {
          HopperBlockEntity hopperTileEntity = (HopperBlockEntity) below;
          filter.addAll(tile.getConnectedItemHandlerItems(hopperTileEntity));
        }
      }
    }
    final int radius = ConfigManager.MAGNET_RANGE.get();
    int vradius = 0;
    int x = tile.worldPosition.getX();
    int y = tile.worldPosition.getY();
    int z = tile.worldPosition.getZ();
    AABB axisalignedbb = (new AABB(x, y, z, x + 1, y + 1, z + 1)).inflate(radius, vradius, radius);
    List<ItemEntity> list = level.getEntitiesOfClass(ItemEntity.class, axisalignedbb);
    pullEntityList(x + 0.2, y + 0.5, z + 0.2, true, list, filter);
  }

  private List<Item> getConnectedItemHandlerItems(HopperBlockEntity hopper) {
    Direction hopperFacing = hopper.getBlockState().getValue(HopperBlock.FACING);
    double x = hopper.getLevelX() + hopperFacing.getStepX();
    double y = hopper.getLevelY() + hopperFacing.getStepY();
    double z = hopper.getLevelZ() + hopperFacing.getStepZ();
    Optional<Pair<IItemHandler, Object>> itemHandlerPair = VanillaInventoryCodeHooks.getItemHandler(hopper.getLevel(), x, y, z, hopperFacing.getOpposite());
    if (!itemHandlerPair.isPresent()) {
      return Collections.emptyList();
    }
    IItemHandler itemHandler = itemHandlerPair.get().getKey();
    return getItemsInItemHandler(itemHandler);
  }

  private static final double ENTITY_PULL_DIST = 0.4; //closer than this and nothing happens
  private static final double ENTITY_PULL_SPEED_CUTOFF = 3; //closer than this and it slows down

  public static int pullEntityList(double x, double y, double z, boolean towardsPos, List<ItemEntity> all, Set<Item> filter) {
    int moved = 0;
    double hdist, xDist, zDist;
    float speed;
    int direction = (towardsPos) ? 1 : -1;
    //negative to flip the vector and push it away
    for (ItemEntity entity : all) {
      if (entity == null) {
        continue;
      }
      if (filter != null
          && !filter.isEmpty()
          && !filter.contains(entity.getItem().getItem())) {
        // filter is not empty AND it one of it items matches /me/ 
        continue;
      }
      //being paranoid
      BlockPos p = entity.blockPosition();
      xDist = Math.abs(x - p.getX());
      zDist = Math.abs(z - p.getZ());
      hdist = Math.sqrt(xDist * xDist + zDist * zDist);
      if (hdist > ENTITY_PULL_DIST) {
        speed = (hdist > ENTITY_PULL_SPEED_CUTOFF) ? ITEMSPEEDFAR : ITEMSPEEDCLOSE;
        setEntityMotionFromVector(entity, x, y, z, direction * speed);
        moved++;
      }
      //else its basically on it, no point
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
