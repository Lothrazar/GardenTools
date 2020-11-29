package com.lothrazar.gardentools.block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.lothrazar.gardentools.GardenRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileMagnet extends TileEntity implements ITickableTileEntity {

  public TileMagnet() {
    super(GardenRegistry.magnetTile);
  }

  private final static float ITEMSPEEDFAR = 0.8F;
  private final static float ITEMSPEEDCLOSE = 0.18F;

  @Override
  public void tick() {
    TileEntity below = world.getTileEntity(pos.down());
    Set<Item> filter = new HashSet<>();
    if (below != null) {
      IItemHandler hopper = below.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
      for (int i = 0; i < hopper.getSlots(); i++) {
        ItemStack here = hopper.getStackInSlot(i);
        if (!here.isEmpty()) {
          filter.add(here.getItem());
        }
      }
    }
    //
    int radius = 16;
    int vradius = 0;
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    AxisAlignedBB axisalignedbb = (new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)).grow(radius, vradius, radius);
    List<ItemEntity> list = world.getEntitiesWithinAABB(ItemEntity.class, axisalignedbb);
    pullEntityList(x + 0.5, y + 0.2, z + 0.5, true, list, filter);
  }

  private static final double ENTITY_PULL_DIST = 0.4;//closer than this and nothing happens 
  private static final double ENTITY_PULL_SPEED_CUTOFF = 3;//closer than this and it slows down

  public static int pullEntityList(double x, double y, double z, boolean towardsPos, List<ItemEntity> all, Set<Item> filter) {
    //    ModCyclic.LOGGER.info("Found for magnet " + all.size());
    int moved = 0;
    double hdist, xDist, zDist;
    float speed;
    int direction = (towardsPos) ? 1 : -1;//negative to flip the vector and push it away
    for (ItemEntity entity : all) {
      if (entity == null) {
        continue;
      }
      if (filter != null && !filter.contains(entity.getItem().getItem())) {
        //
        //        GardenMod.LOGGER.info(" ignore bc filter " + entity.getItem());
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
      } //else its basically on it, no point
    }
    return moved;
  }

  public static void setEntityMotionFromVector(Entity entity, double x, double y, double z, float modifier) {
    Vector3 originalPosVector = new Vector3(x, y, z);
    Vector3 entityVector = new Vector3(entity);
    Vector3 finalVector = originalPosVector.copy().subtract(entityVector);
    if (finalVector.mag() > 1)
      finalVector.normalize();
    double motionX = finalVector.x * modifier;
    double motionY = finalVector.y * modifier;
    double motionZ = finalVector.z * modifier;
    entity.setMotion(motionX, motionY, motionZ);
  }
}
