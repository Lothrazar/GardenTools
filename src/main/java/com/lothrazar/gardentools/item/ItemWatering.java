package com.lothrazar.gardentools.item;

import java.util.stream.Stream;
import com.lothrazar.gardentools.GardenConfigManager;
import com.lothrazar.library.item.ItemFlib;
import com.lothrazar.library.util.HarvestUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

public class ItemWatering extends ItemFlib {

  private static final double PCT_GROW_IF_LESS = 0.1;

  public ItemWatering(Item.Properties builder) {
    super(builder.stacksTo(1), new ItemFlib.Settings().tooltip());
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level world = context.getLevel();
    if (context.getClickedFace() == Direction.DOWN) {
      return InteractionResult.FAIL;
    }
    final int range = GardenConfigManager.WATERING_RANGE.get();
    BlockPos pos = context.getClickedPos();
    Stream<BlockPos> shape = BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range));
    shape.forEach(posCurrent -> {
      //      first, moisturize farmland just like tiller
      BlockState bs = world.getBlockState(posCurrent);
      if (bs.hasProperty(FarmBlock.MOISTURE)) {
        int moisture = bs.getValue(FarmBlock.MOISTURE);
        if (moisture < FarmBlock.MAX_MOISTURE) {
          world.setBlock(posCurrent, bs.setValue(FarmBlock.MOISTURE, FarmBlock.MAX_MOISTURE), 3);
          world.addParticle(ParticleTypes.RAIN, posCurrent.getX(), posCurrent.getY(), posCurrent.getZ(), 0.0D, 0.0D, 0.0D);
        }
      }
      //else
      //encourage growth with ticks, without direct bonemeal shortcut 
      Block plantBlock = bs.getBlock();
      if (HarvestUtil.hasAgeProperty(bs) ||
          plantBlock instanceof BonemealableBlock ||
          plantBlock instanceof IPlantable) {
        //a chance on each block
        if (world.random.nextDouble() < PCT_GROW_IF_LESS) {
          world.addParticle(ParticleTypes.RAIN, posCurrent.getX(), posCurrent.getY(), posCurrent.getZ(), 0.0D, 0.0D, 0.0D);
          if (world instanceof ServerLevel) {
            bs.randomTick((ServerLevel) world, posCurrent, world.random);
          }
          //          world.notifyBlockUpdate(posCurrent, state, state, 3);
        }
      }
    });
    return InteractionResult.SUCCESS;
  }
}
