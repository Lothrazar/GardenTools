package com.lothrazar.gardentools.item;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.lothrazar.gardentools.GardenConfigManager;
import com.lothrazar.gardentools.GardenMod;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTiller extends HoeItem {

  public ItemTiller(Tier tier, Properties builder) {
    super(tier, -4, 0.0F, builder.stacksTo(1).durability(777));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    tooltip.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    if (context.getClickedFace() == Direction.DOWN) {
      return InteractionResult.FAIL;
    }
    //so we got a success from the initial block
    Level world = context.getLevel();
    BlockPos center = context.getClickedPos();
    Direction face = context.getHorizontalDirection();
    BlockPos blockpos = null;
    for (int dist = 0; dist < GardenConfigManager.getTillingRange(); dist++) {
      blockpos = center.relative(face, dist);
      if (world.isEmptyBlock(blockpos)) {
        //air here, went off an edge. try to go down 1
        blockpos = blockpos.below();
        if (world.isEmptyBlock(blockpos.above())) {
          if (hoeBlock(context, blockpos)) {
            center = center.below();
            //go down the hill
          }
        }
      }
      else if (world.isEmptyBlock(blockpos.above())) {
        //at my elevation
        hoeBlock(context, blockpos);
      }
      else {
        //try going up by 1
        blockpos = blockpos.above();
        if (world.isEmptyBlock(blockpos.above())) {
          if (hoeBlock(context, blockpos)) {
            center = center.above();
            //go up the hill
          }
        }
      }
    }
    return InteractionResult.SUCCESS;
  }

  @SuppressWarnings("deprecation")
  private boolean hoeBlock(UseOnContext context, BlockPos blockpos) {
    Level world = context.getLevel();
    Block blockHere = world.getBlockState(blockpos).getBlock();
    Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = HoeItem.TILLABLES.get(blockHere);
    if (pair == null) {
      return false;
    }
    Predicate<UseOnContext> predicate = pair.getFirst();
    Consumer<UseOnContext> consumer = pair.getSecond();
    if (predicate.test(context)) {
      Player player = context.getPlayer();
      player.level().playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
      consumer.accept(context);
      this.moisturize(context.getLevel(), blockpos, context.getLevel().getBlockState(blockpos));
      Player playerentity = context.getPlayer();
      world.playSound(playerentity, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
      if (playerentity != null) {
        context.getItemInHand().hurtAndBreak(1, playerentity, (p) -> {
          p.broadcastBreakEvent(context.getHand());
        });
      }
      return true;
    }
    return false;
  }

  private void moisturize(Level world, BlockPos pos, BlockState blockstate) {
    try {
      if (GardenConfigManager.getMoisture() > 0) {
        world.setBlock(pos, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, GardenConfigManager.getMoisture()), 3);
      }
    }
    catch (Exception e) {
      GardenMod.LOGGER.error("ItemTiller Moisturize error", e);
    }
  }
}
