package com.lothrazar.gardentools.shears;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;
import com.lothrazar.gardentools.GardenMod;
import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.gardentools.UtilFakePlayer;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.FakePlayer;

public class TileShears extends TileEntity implements ITickableTileEntity {

  private WeakReference<FakePlayer> fakePlayer;
  int radius = 8;

  public TileShears() {
    super(GardenRegistry.shearingTile);
  }

  public WeakReference<FakePlayer> setupBeforeTrigger(ServerWorld sw, String name, UUID uuid) {
    WeakReference<FakePlayer> fakePlayer = UtilFakePlayer.initFakePlayer(sw, uuid, name);
    if (fakePlayer == null) {
      GardenMod.LOGGER.error("Fake player failed to init " + name + " " + uuid);
      return null;
    }
    //fake player facing the same direction as tile. for throwables
    fakePlayer.get().setPosition(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());//seems to help interact() mob drops like milk
    //    fakePlayer.get().rotationYaw = UtilEntity.getYawFromFacing(this.getCurrentFacing());
    return fakePlayer;
  }

  @Override
  public void tick() {
    if (world.isRemote || world.getGameTime() % 20 != 0) {
      return;
    }
    //only fire every 20 ticks
    if ((world instanceof ServerWorld) && fakePlayer == null) {
      fakePlayer = setupBeforeTrigger((ServerWorld) world, "rancher", UUID.randomUUID());
    }
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    AxisAlignedBB aabb = (new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)).grow(radius).expand(0.0D, world.getHeight(), 0.0D);
    //first find items
    //    List<ItemEntity> itemEntities = world.getEntitiesWithinAABB(ItemEntity.class, aabb);
    //find entities
    List<AnimalEntity> list = world.getEntitiesWithinAABB(AnimalEntity.class, aabb);
    for (AnimalEntity entity : list) {
      if (entity == null || fakePlayer == null || fakePlayer.get() == null) {
        continue;
      }
      /*****************************/
      if (entity instanceof IForgeShearable) {
        //shear
        IForgeShearable sheep = (IForgeShearable) entity;
        if (sheep.isShearable(fakePlayer.get().getHeldItemMainhand(), world, pos)) {
          fakePlayer.get().setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.SHEARS));
          //
          List<ItemStack> drops = sheep.onSheared(fakePlayer.get(), fakePlayer.get().getHeldItemMainhand(), world, pos, 1);
          drops.forEach(d -> {
            entity.entityDropItem(d, 1.0F);
            //            ent.setMotion(ent.getMotion().add((double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double) (rand.nextFloat() * 0.05F), (double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
          });
        }
      }
    }
  }
}
