package com.lothrazar.gardentools.item;

import com.lothrazar.gardentools.ConfigManager;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationTextComponent t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    t.mergeStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    World world = context.getWorld();
    if (context.getFace() == Direction.DOWN) {
      return ActionResultType.FAIL;
    }
    final int range = ConfigManager.WATERING_RANGE.get();
    BlockPos pos = context.getPos();
    Stream<BlockPos> shape = BlockPos.getAllInBox(pos.add(-range, -range, -range), pos.add(range, range, range));
    shape.forEach(posCurrent -> {
      //      first, moisturize farmland just like tiller
      BlockState bs = world.getBlockState(posCurrent);
      if (bs.hasProperty(FarmlandBlock.MOISTURE)) {
        int moisture = bs.get(FarmlandBlock.MOISTURE);
        if (moisture < ItemFertilizer.MOIST_FINAL) {
          world.setBlockState(posCurrent, bs.with(FarmlandBlock.MOISTURE, ItemFertilizer.MOIST_FINAL), 3);
          world.addParticle(ParticleTypes.RAIN, posCurrent.getX(), posCurrent.getY(), posCurrent.getZ(), 0.0D, 0.0D, 0.0D);
        }
      }
      //else
      //encourage growth with ticks, without direct bonemeal shortcut 
      Block plantBlock = bs.getBlock();
      if (this.hasAgeProperty(bs) ||
          plantBlock instanceof IGrowable ||
          plantBlock instanceof IPlantable) {
        //a chance on each block
        if (world.rand.nextDouble() < PCT_GROW_IF_LESS) {
          world.addParticle(ParticleTypes.RAIN, posCurrent.getX(), posCurrent.getY(), posCurrent.getZ(), 0.0D, 0.0D, 0.0D);
          if (world instanceof ServerWorld) {
            bs.randomTick((ServerWorld) world, posCurrent, random);
          }
          //          world.notifyBlockUpdate(posCurrent, state, state, 3);
        }
      }
    });
    return ActionResultType.SUCCESS;
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
