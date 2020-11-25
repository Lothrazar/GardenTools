package com.lothrazar.gardentools.rancher;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;
import com.lothrazar.gardentools.GardenMod;
import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.gardentools.UtilFakePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.FakePlayer;

public class TileRancher extends TileEntity implements ITickableTileEntity {

  private WeakReference<FakePlayer> fakePlayer;

  public TileRancher() {
    super(GardenRegistry.rancherTile);
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
    if ((world instanceof ServerWorld) && fakePlayer == null)
      fakePlayer = setupBeforeTrigger((ServerWorld) world, "miner", UUID.randomUUID());
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    int radius = 8;
    AxisAlignedBB aabb = (new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)).grow(radius).expand(0.0D, world.getHeight(), 0.0D);
    // TODO Auto-generated method stub
    List<LivingEntity> list = world.getEntitiesWithinAABB(AnimalEntity.class, aabb);
    for (LivingEntity entity : list) {
      if (entity == null || fakePlayer == null || fakePlayer.get() == null) {
        continue;
      }
      //wat do
      if (entity instanceof AnimalEntity) {
        //feed? 
        AnimalEntity animal = (AnimalEntity) entity;
        //        fakePlayer.get().setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.WHEAT));
        if (animal.isBreedingItem(fakePlayer.get().getHeldItemMainhand())) {
          //ok  feed
          ActionResultType result = animal.func_230254_b_(fakePlayer.get(), Hand.MAIN_HAND);
          GardenMod.LOGGER.info("result animal feed " + result);
        }
      }
      //full stop
      //      if (entity instanceof SheepEntity) {
      if (entity instanceof IForgeShearable) {
        //shear
        IForgeShearable sheep = (IForgeShearable) entity;
        fakePlayer.get().setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.SHEARS));
        if (sheep.isShearable(fakePlayer.get().getHeldItemMainhand(), world, pos)) {
          //
          List<ItemStack> drops = sheep.onSheared(fakePlayer.get(), fakePlayer.get().getHeldItemMainhand(), world, pos, 1);
          drops.forEach(d -> {
            entity.entityDropItem(d, 1.0F);
            //            ent.setMotion(ent.getMotion().add((double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F), (double) (rand.nextFloat() * 0.05F), (double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
          });
        }
      }
      else if (entity instanceof CowEntity) {
        //milk
        CowEntity cow = (CowEntity) entity;
        //TODO: get real bucket
        //        fakePlayer.get().setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.BUCKET));
        ActionResultType result = cow.func_230254_b_(fakePlayer.get(), Hand.MAIN_HAND);
        if (result == ActionResultType.CONSUME) {
          cow.entityDropItem(fakePlayer.get().getHeldItemMainhand());
          fakePlayer.get().setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
      }
    }
  }
}
