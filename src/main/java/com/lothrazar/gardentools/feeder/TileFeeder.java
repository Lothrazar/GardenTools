package com.lothrazar.gardentools.feeder;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;
import com.lothrazar.gardentools.GardenMod;
import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.gardentools.UtilFakePlayer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

public class TileFeeder extends TileEntity implements ITickableTileEntity {

  private WeakReference<FakePlayer> fakePlayer;
  int radius = 8;

  public TileFeeder() {
    super(GardenRegistry.feederTile);
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
    List<ItemEntity> itemEntities = world.getEntitiesWithinAABB(ItemEntity.class, aabb);
    //find entities
    List<AnimalEntity> list = world.getEntitiesWithinAABB(AnimalEntity.class, aabb);
    for (AnimalEntity entity : list) {
      if (entity == null || fakePlayer == null || fakePlayer.get() == null) {
        continue;
      }
      /*****************************/
      if (!entity.isChild()) {
        //no feedin the child
        ItemEntity eiBreedingItem = this.findBreedingItem(itemEntities, entity);
        //        fakePlayer.get().setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.WHEAT));
        if (eiBreedingItem != null) {
          //ok  feed
          fakePlayer.get().setHeldItem(Hand.MAIN_HAND, eiBreedingItem.getItem());
          ActionResultType result = entity.func_230254_b_(fakePlayer.get(), Hand.MAIN_HAND);
          GardenMod.LOGGER.info("result animal feed " + result);
          if (result == ActionResultType.CONSUME || result == ActionResultType.SUCCESS) {
            eiBreedingItem.setItem(fakePlayer.get().getHeldItemMainhand());
          }
        }
      }
    }
  }

  private ItemEntity findBreedingItem(List<ItemEntity> itemEntities, AnimalEntity entity) {
    for (ItemEntity ei : itemEntities) {
      //alive stack that matches the item
      if (ei.isAlive() && entity.isBreedingItem(ei.getItem())) {
        return ei;
      }
    }
    return null;
  }
}
