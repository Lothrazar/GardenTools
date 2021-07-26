package com.lothrazar.gardentools.item;

import com.lothrazar.gardentools.GardenMod;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.item.Item.Properties;

public class ItemTiller extends HoeItem {

  public ItemTiller(Tier tier, Properties builder) {
    super(tier, -4, 0.0F, builder);
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
    if (context.getClickedFace() == Direction.DOWN) {
      return InteractionResult.FAIL;
    }
    //so we got a success from the initial block
    Level world = context.getLevel();
    BlockPos center = context.getClickedPos();
    Direction face = context.getHorizontalDirection();
    BlockPos blockpos = null;
    for (int dist = 0; dist < GardenMod.CONFIG.getTillingRange(); dist++) {
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

  private boolean hoeBlock(UseOnContext context, BlockPos blockpos) {
    Level world = context.getLevel();
    Block blockHere = world.getBlockState(blockpos).getBlock();
    BlockState blockstate = TILLABLES.get(blockHere);
    if (blockstate != null) {
      blockstate = this.moisturize(blockstate);
      if (world.setBlock(blockpos, blockstate, 11)) {
        Player playerentity = context.getPlayer();
        world.playSound(playerentity, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (playerentity != null) {
          context.getItemInHand().hurtAndBreak(1, playerentity, (p) -> {
            p.broadcastBreakEvent(context.getHand());
          });
        }
        return true;
      }
    }
    return false;
  }

  private BlockState moisturize(BlockState blockstate) {
    try {
      if (blockstate.getBlock() == Blocks.FARMLAND && GardenMod.CONFIG.getMoisture() > 0) {
        blockstate = blockstate.setValue(FarmBlock.MOISTURE, GardenMod.CONFIG.getMoisture());
      }
    }
    catch (Exception e) {
      GardenMod.LOGGER.error("ItemTiller Moisturize error", e);
    }
    return blockstate;
  }
}
