package com.lothrazar.gardentools.block.rancher;

import com.lothrazar.gardentools.ConfigManager;
import com.lothrazar.gardentools.GardenMod;
import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.gardentools.UtilFakePlayer;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.FakePlayer;

public class TileRancher extends BlockEntity implements TickableBlockEntity {

  private WeakReference<FakePlayer> fakePlayer;

  public TileRancher() {
    super(GardenRegistry.RANCHERTILE);
  }

  public WeakReference<FakePlayer> setupBeforeTrigger(ServerLevel sw, String name, UUID uuid) {
    WeakReference<FakePlayer> fakePlayer = UtilFakePlayer.initFakePlayer(sw, uuid, name);
    if (fakePlayer == null) {
      GardenMod.LOGGER.error("Fake player failed to init " + name + " " + uuid);
      return null;
    }
    //fake player facing the same direction as tile. for throwables
    fakePlayer.get().setPos(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
    //seems to help interact() mob drops like milk
    //    fakePlayer.get().rotationYaw = UtilEntity.getYawFromFacing(this.getCurrentFacing());
    return fakePlayer;
  }

  @Override
  public void tick() {
    if (level.isClientSide || level.getGameTime() % 20 != 0) {
      return;
    }
    //only fire every 20 ticks
    if ((level instanceof ServerLevel) && fakePlayer == null) {
      fakePlayer = setupBeforeTrigger((ServerLevel) level, "rancher", UUID.randomUUID());
    }
    int x = worldPosition.getX();
    int y = worldPosition.getY();
    int z = worldPosition.getZ();
    final int radius = ConfigManager.RANCHER_RANGE.get();
    AABB aabb = (new AABB(x, y, z, x + 1, y + 1, z + 1)).inflate(radius).expandTowards(0.0D, level.getMaxBuildHeight(), 0.0D);
    //first find items
    List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, aabb);
    //find entities
    List<Animal> list = level.getEntitiesOfClass(Animal.class, aabb);
    for (Animal entity : list) {
      if (entity == null || fakePlayer == null || fakePlayer.get() == null) {
        continue;
      }
      /*****************************/
      if (entity instanceof IForgeShearable) {
        //shear
        IForgeShearable sheep = (IForgeShearable) entity;
        if (sheep.isShearable(fakePlayer.get().getMainHandItem(), level, worldPosition)) {
          fakePlayer.get().setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SHEARS));
          //
          List<ItemStack> drops = sheep.onSheared(fakePlayer.get(), fakePlayer.get().getMainHandItem(), level, worldPosition, 1);
          drops.forEach(d -> {
            entity.spawnAtLocation(d, 1.0F);
          });
          fakePlayer.get().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
          break;
          //done, one animal per tick
        }
      }
      //miiiiiiiiiiiilk
      if (entity instanceof Cow) {
        //milk
        Cow cow = (Cow) entity;
        ItemEntity eiBucket = this.findExact(itemEntities, Items.BUCKET);
        if (eiBucket != null) {
          boolean doreplace = eiBucket.getItem().getCount() == 1;
          fakePlayer.get().setItemInHand(InteractionHand.MAIN_HAND, eiBucket.getItem());
          InteractionResult result = cow.mobInteract(fakePlayer.get(), InteractionHand.MAIN_HAND);
          if (result == InteractionResult.CONSUME || result == InteractionResult.SUCCESS) {
            if (doreplace) {
              //              GardenMod.LOGGER.info(" copy item into player " + fakePlayer.get().getHeldItemMainhand());
              eiBucket.setItem(fakePlayer.get().getMainHandItem());
              //if we dont replace, then drop it
            }
            else {
              //              GardenMod.LOGGER.info("doreplace is false, drop new milk" + result);
              eiBucket.setItem(fakePlayer.get().getMainHandItem());
              cow.spawnAtLocation(new ItemStack(Items.MILK_BUCKET));
            }
            fakePlayer.get().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            break;
            //done, one animal per tick
          }
        }
      }
    }
  }

  /**
   * find empty bucket
   * 
   * @param itemEntities
   * @param bucket
   * @return
   */
  private ItemEntity findExact(List<ItemEntity> itemEntities, Item bucket) {
    for (ItemEntity ei : itemEntities) {
      //alive stack that matches the item
      if (ei.isAlive() && !ei.getItem().isEmpty() && ei.getItem().getItem() == bucket) {
        return ei;
      }
    }
    return null;
  }
}
