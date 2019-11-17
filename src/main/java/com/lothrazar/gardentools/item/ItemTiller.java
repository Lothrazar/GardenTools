package com.lothrazar.gardentools.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTiller extends HoeItem {

  public ItemTiller(IItemTier tier, Properties builder) {
    super(tier, 0.2F, builder);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    ActionResultType succ = super.onItemUse(context);
    if (context.getFace() == Direction.DOWN || succ != ActionResultType.SUCCESS) {
      return succ;
    }
    //so we got a success from the initial block
    World world = context.getWorld();
    BlockPos center = context.getPos();
    //    context.getPlayer().getHorizontalFacing()
    Direction face = context.getPlacementHorizontalFacing();
    BlockPos blockpos = null;
    for (int dist = 0; dist < getRange(); dist++) {
      blockpos = center.offset(face, dist);
      if (world.isAirBlock(blockpos.up())) {
        hoeBlock(context, blockpos);
      }
    }
    return succ;
  }

  private int getRange() {
    return 9;
  }

  private void hoeBlock(ItemUseContext context, BlockPos blockpos) {
    World world = context.getWorld();
    Block blockHere = world.getBlockState(blockpos).getBlock();
    BlockState blockstate = HOE_LOOKUP.get(blockHere);
    if (blockstate != null) {
      PlayerEntity playerentity = context.getPlayer();
      world.playSound(playerentity, blockpos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
      if (!world.isRemote) {
        // TODO: some other tool or block
        //        try {
        //          blockstate = blockstate.with(FarmlandBlock.MOISTURE, 7);
        //        }
        //        catch (Exception e) {}
        world.setBlockState(blockpos, blockstate, 11);
        if (playerentity != null) {
          context.getItem().damageItem(1, playerentity, (p_220043_1_) -> {
            p_220043_1_.sendBreakAnimation(context.getHand());
          });
        }
      }
      //           return ActionResultType.SUCCESS;
    }
  }
}
