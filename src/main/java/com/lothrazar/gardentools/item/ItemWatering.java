package com.lothrazar.gardentools.item;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
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
  private static final int MOIST_FINAL = 7;
  final int dist = 3;

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
    //    ActionResultType succ = super.onItemUse(context);
    World world = context.getWorld();
    if (context.getFace() == Direction.DOWN) {
      return ActionResultType.FAIL;
    }
    BlockPos pos = context.getPos();
    Stream<BlockPos> shape = BlockPos.getAllInBox(pos.add(-dist, -dist, -dist), pos.add(dist, dist, dist));
    shape.forEach(posCurrent -> {
      BlockState bs = world.getBlockState(posCurrent);
      //first, moisturize farmland just like tiller
      if (bs.hasProperty(FarmlandBlock.MOISTURE)) {
        int moisture = bs.get(FarmlandBlock.MOISTURE);
        if (moisture < MOIST_FINAL) {
          world.setBlockState(posCurrent, bs.with(FarmlandBlock.MOISTURE, MOIST_FINAL), 3);
          world.addParticle(ParticleTypes.RAIN, posCurrent.getX(), posCurrent.getY(), posCurrent.getZ(), 0.0D, 0.0D, 0.0D);
        }
      }
      //else
      if (world.rand.nextDouble() < PCT_GROW_IF_LESS) {
        //encourage growth with ticks, without direct bonemeal shortcut
        BlockState state = world.getBlockState(posCurrent);
        Block plantBlock = state.getBlock();
        ChorusFlowerBlock y;//has age prop but not the interface
        if (plantBlock instanceof IGrowable || plantBlock instanceof IPlantable || plantBlock == Blocks.CHORUS_FLOWER) {
          if (world instanceof ServerWorld)
            state.randomTick((ServerWorld) world, posCurrent, random);
          world.notifyBlockUpdate(posCurrent, state, state, 3);
          world.addParticle(ParticleTypes.RAIN, posCurrent.getX(), posCurrent.getY(), posCurrent.getZ(), 0.0D, 0.0D, 0.0D);
        }
      }
    });
    return ActionResultType.SUCCESS;
  }
}
