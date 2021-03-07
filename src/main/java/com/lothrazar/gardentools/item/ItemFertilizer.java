package com.lothrazar.gardentools.item;

import com.lothrazar.gardentools.ConfigManager;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemFertilizer extends Item {

  public static final int MOIST_FINAL = 7;
  final int dist = 3;

  public ItemFertilizer(Item.Properties builder) {
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
    final int count = ConfigManager.FERT_POWER.get();
    for (int i = 0; i < count; i++) {
      BoneMealItem.applyBonemeal(context.getItem(), world, context.getPos(), context.getPlayer());
    }
    ///hydrate farmland bonus
    BlockPos pos = context.getPos().down();
    Stream<BlockPos> shape = BlockPos.getAllInBox(pos.add(-dist, -dist, -dist), pos.add(dist, dist, dist));
    shape.forEach(posCurrent -> {
      BlockState bs = world.getBlockState(posCurrent);
      if (bs.hasProperty(FarmlandBlock.MOISTURE)) {
        int moisture = bs.get(FarmlandBlock.MOISTURE);
        if (moisture < MOIST_FINAL) {
          world.setBlockState(posCurrent, bs.with(FarmlandBlock.MOISTURE, MOIST_FINAL), 3);
          world.addParticle(ParticleTypes.RAIN, posCurrent.getX(), posCurrent.getY(), posCurrent.getZ(), 0.0D, 0.0D, 0.0D);
        }
      }
    });
    return ActionResultType.SUCCESS;
  }
}
