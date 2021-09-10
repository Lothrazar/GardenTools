package com.lothrazar.gardentools.item;

import com.lothrazar.gardentools.ConfigManager;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

public class ItemWatering extends Item {

  private static final double PCT_GROW_IF_LESS = 0.1;

  public ItemWatering(Item.Properties builder) {
    super(builder);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    TranslatableComponent t = new TranslatableComponent(getDescriptionId() + ".tooltip");
    t.withStyle(ChatFormatting.GRAY);
    tooltip.add(t);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level world = context.getLevel();
    if (context.getClickedFace() == Direction.DOWN) {
      return InteractionResult.FAIL;
    }
    final int range = ConfigManager.WATERING_RANGE.get();
    BlockPos pos = context.getClickedPos();
    Stream<BlockPos> shape = BlockPos.betweenClosedStream(pos.offset(-range, -range, -range), pos.offset(range, range, range));
    shape.forEach(posCurrent -> {
      //      first, moisturize farmland just like tiller
      BlockState bs = world.getBlockState(posCurrent);
      if (bs.hasProperty(FarmBlock.MOISTURE)) {
        int moisture = bs.getValue(FarmBlock.MOISTURE);
        if (moisture < ItemFertilizer.MOIST_FINAL) {
          world.setBlock(posCurrent, bs.setValue(FarmBlock.MOISTURE, ItemFertilizer.MOIST_FINAL), 3);
          world.addParticle(ParticleTypes.RAIN, posCurrent.getX(), posCurrent.getY(), posCurrent.getZ(), 0.0D, 0.0D, 0.0D);
        }
      }
      //else
      //encourage growth with ticks, without direct bonemeal shortcut 
      Block plantBlock = bs.getBlock();
      if (this.hasAgeProperty(bs) ||
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

  //different plants have different age ranges, such as 3 for cocoa, or 7 for wheat, like BlockStateProperties.AGE_0_5;
  private boolean hasAgeProperty(BlockState bs) {
    for (Property<?> p : bs.getProperties()) {
      if (p != null && p.getName() != null
          && p instanceof IntegerProperty &&
          p.getName().equalsIgnoreCase("age")) {
        return true;
      }
    }
    return false;
  }
}
