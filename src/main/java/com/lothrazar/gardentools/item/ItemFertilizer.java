package com.lothrazar.gardentools.item;

import com.lothrazar.gardentools.ConfigManager;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
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
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    TranslatableComponent t = new TranslatableComponent(getDescriptionId() + ".tooltip");
    t.withStyle(ChatFormatting.GRAY);
    tooltip.add(t);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level world = context.getLevel();
    final int count = ConfigManager.FERT_POWER.get();
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
        if (moisture < MOIST_FINAL) {
          world.setBlock(posCurrent, bs.setValue(FarmBlock.MOISTURE, MOIST_FINAL), 3);
          world.addParticle(ParticleTypes.RAIN, posCurrent.getX(), posCurrent.getY(), posCurrent.getZ(), 0.0D, 0.0D, 0.0D);
        }
      }
    });
    return InteractionResult.SUCCESS;
  }
}
