package com.lothrazar.gardentools.item;

import java.util.stream.Stream;
import com.lothrazar.gardentools.GardenConfigManager;
import com.lothrazar.library.item.ItemFlib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ItemFertilizer extends ItemFlib {

  final int dist = 3;

  public ItemFertilizer(Item.Properties builder) {
    super(builder, new ItemFlib.Settings().tooltip());
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level world = context.getLevel();
    final int count = GardenConfigManager.FERT_POWER.get();
    for (int i = 0; i < count; i++) {
      BoneMealItem.applyBonemeal(context.getItemInHand(), world, context.getClickedPos(), context.getPlayer());
    }
    ///hydrate farmland bonus
    BlockPos pos = context.getClickedPos().below();
    Stream<BlockPos> shape = BlockPos.betweenClosedStream(pos.offset(-dist, -dist, -dist), pos.offset(dist, dist, dist));
    shape.forEach(posCurrent -> {
      BlockState bs = world.getBlockState(posCurrent);
      if (bs.hasProperty(FarmBlock.MOISTURE)) {
        int moisture = bs.getValue(FarmBlock.MOISTURE);
        if (moisture < FarmBlock.MAX_MOISTURE) {
          world.setBlock(posCurrent, bs.setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE), 3);
          world.addParticle(ParticleTypes.RAIN, posCurrent.getX(), posCurrent.getY(), posCurrent.getZ(), 0.0D, 0.0D, 0.0D);
        }
      }
    });
    return InteractionResult.SUCCESS;
  }
}
