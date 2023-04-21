package com.lothrazar.gardentools.block.feeder;

import java.lang.ref.WeakReference;
import java.util.List;
import com.lothrazar.gardentools.GardenConfigManager;
import com.lothrazar.gardentools.GardenMod;
import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.library.util.FakePlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;

public class TileFeeder extends BlockEntity {

  private WeakReference<FakePlayer> fakePlayer;

  public TileFeeder(BlockPos pos, BlockState state) {
    super(GardenRegistry.TE_FEEDER.get(), pos, state);
  }

  public static WeakReference<FakePlayer> setupBeforeTrigger(ServerLevel sw, String name, BlockPos pos) {
    WeakReference<FakePlayer> fakePlayer = FakePlayerUtil.initFakePlayer(sw, name);
    if (fakePlayer == null) {
      GardenMod.LOGGER.error("Fake player failed to init " + name + " ");
      return null;
    }
    //fake player facing the same direction as tile. for throwables
    fakePlayer.get().setPos(pos.getX(), pos.getY(), pos.getZ());
    //seems to help interact() mob drops like milk
    //    fakePlayer.get().rotationYaw = UtilEntity.getYawFromFacing(this.getCurrentFacing());
    return fakePlayer;
  }

  public static <E extends BlockEntity> void serverTick(Level level, BlockPos pos, BlockState blockState, TileFeeder tile) {
    if (level.isClientSide || level.getGameTime() % 20 != 0) {
      return;
    }
    //only fire every 20 ticks
    if (tile.fakePlayer == null && (level instanceof ServerLevel)) {
      tile.fakePlayer = setupBeforeTrigger((ServerLevel) level, "rancher", pos);
    }
    int x = tile.worldPosition.getX();
    int y = tile.worldPosition.getY();
    int z = tile.worldPosition.getZ();
    final int radius = GardenConfigManager.FEEDER_RANGE.get();
    AABB aabb = (new AABB(x, y, z, x + 1, y + 1, z + 1)).inflate(radius).expandTowards(0.0D, level.getMaxBuildHeight(), 0.0D);
    //first find items
    List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, aabb);
    //find entities
    List<Animal> list = level.getEntitiesOfClass(Animal.class, aabb);
    for (Animal entity : list) {
      if (entity == null || tile.fakePlayer == null || tile.fakePlayer.get() == null) {
        continue;
      }
      /*****************************/
      if (!entity.isBaby()) {
        //no feedin the child
        ItemEntity eiBreedingItem = tile.findBreedingItem(itemEntities, entity);
        //        fakePlayer.get().setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.WHEAT));
        if (eiBreedingItem != null) {
          //ok  feed
          tile.fakePlayer.get().setItemInHand(InteractionHand.MAIN_HAND, eiBreedingItem.getItem());
          InteractionResult result = entity.mobInteract(tile.fakePlayer.get(), InteractionHand.MAIN_HAND);
          if (result == InteractionResult.CONSUME || result == InteractionResult.SUCCESS) {
            eiBreedingItem.setItem(tile.fakePlayer.get().getMainHandItem());
          }
        }
      }
    }
  }

  private ItemEntity findBreedingItem(List<ItemEntity> itemEntities, Animal entity) {
    for (ItemEntity ei : itemEntities) {
      //alive stack that matches the item
      if (ei.isAlive() && entity.isFood(ei.getItem())) {
        return ei;
      }
    }
    return null;
  }

  public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileFeeder tileFeeder) {}
}
