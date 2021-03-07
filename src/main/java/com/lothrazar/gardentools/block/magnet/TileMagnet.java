package com.lothrazar.gardentools.block.magnet;

import com.lothrazar.gardentools.ConfigManager;
import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.gardentools.block.Vector3;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;
import org.apache.commons.lang3.tuple.Pair;

public class TileMagnet extends TileEntity implements ITickableTileEntity {

  private static final float ITEMSPEEDFAR = 0.8F;
  private static final float ITEMSPEEDCLOSE = 0.08F;

  public TileMagnet() {
    super(GardenRegistry.MAGNETTILE);
  }

  @Override
  public void tick() {
    if (world.isRemote) {
      return;
    }
    TileEntity below = world.getTileEntity(pos.down());
    Set<Item> filter = new HashSet<>();
    if (below != null) {
      IItemHandler hopper = below.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
      filter.addAll(getItemsInItemHandler(hopper));
      if (below instanceof HopperTileEntity) {
        HopperTileEntity hopperTileEntity = (HopperTileEntity) below;
        filter.addAll(getConnectedItemHandlerItems(hopperTileEntity));
      }
    }
    final int radius = ConfigManager.MAGNET_RANGE.get();
    int vradius = 0;
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    AxisAlignedBB axisalignedbb = (new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)).grow(radius, vradius, radius);
    List<ItemEntity> list = world.getEntitiesWithinAABB(ItemEntity.class, axisalignedbb);
    pullEntityList(x + 0.2, y + 0.5, z + 0.2, true, list, filter);
  }

  private List<Item> getConnectedItemHandlerItems(HopperTileEntity hopper) {
    Direction hopperFacing = hopper.getBlockState().get(HopperBlock.FACING);
    double x = hopper.getXPos() + hopperFacing.getXOffset();
    double y = hopper.getYPos() + hopperFacing.getYOffset();
    double z = hopper.getZPos() + hopperFacing.getZOffset();
    Optional<Pair<IItemHandler, Object>> itemHandlerPair = VanillaInventoryCodeHooks.getItemHandler(hopper.getWorld(), x, y, z, hopperFacing.getOpposite());
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
      BlockPos p = entity.getPosition();
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
    entity.setMotion(motionX, motionY, motionZ);
  }

  private List<Item> getItemsInItemHandler(IItemHandler itemHandler) {
    List<Item> filter = new ArrayList<>();
    for (int i = 0; i < itemHandler.getSlots(); i++) {
      ItemStack stack = itemHandler.getStackInSlot(i);
      if (!stack.isEmpty()) {
        filter.add(stack.getItem());
      }
    }
    return filter;
  }
}
