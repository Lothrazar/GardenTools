package com.lothrazar.gardentools.item;

import java.util.List;
import javax.annotation.Nullable;
import com.lothrazar.gardentools.GardenMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTiller extends HoeItem {

  public ItemTiller(IItemTier tier, Properties builder) {
    super(tier, 0.2F, builder);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationTextComponent t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    t.applyTextStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    //    ActionResultType succ = super.onItemUse(context);
    if (context.getFace() == Direction.DOWN) {
      return ActionResultType.FAIL;
    }
    //so we got a success from the initial block
    World world = context.getWorld();
    BlockPos center = context.getPos();
    //    context.getPlayer().getHorizontalFacing()
    Direction face = context.getPlacementHorizontalFacing();
    BlockPos blockpos = null;
    for (int dist = 0; dist < GardenMod.config.getTillingRange(); dist++) {
      blockpos = center.offset(face, dist);
      if (world.isAirBlock(blockpos)) {
        //air here, went off an edge. try to go down 1
        blockpos = blockpos.down();
        if (world.isAirBlock(blockpos.up())) {
          if (hoeBlock(context, blockpos)) {
            center = center.down();//go down the hill
          }
        }
      }
      else if (world.isAirBlock(blockpos.up())) {
        //at my elevation
        hoeBlock(context, blockpos);
      }
      else {
        //try going up by 1
        blockpos = blockpos.up();
        if (world.isAirBlock(blockpos.up())) {
          if (hoeBlock(context, blockpos)) {
            center = center.up();//go up the hill
          }
        }
      }
    }
    return ActionResultType.SUCCESS;
  }

  private boolean hoeBlock(ItemUseContext context, BlockPos blockpos) {
    World world = context.getWorld();
    Block blockHere = world.getBlockState(blockpos).getBlock();
    BlockState blockstate = HOE_LOOKUP.get(blockHere);
    if (blockstate != null) {
      blockstate = this.moisturize(blockstate);
      if (world.setBlockState(blockpos, blockstate, 11)) {
        PlayerEntity playerentity = context.getPlayer();
        world.playSound(playerentity, blockpos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (playerentity != null) {
          context.getItem().damageItem(1, playerentity, (p) -> {
            p.sendBreakAnimation(context.getHand());
          });
        }
        return true;
      }
    }
    return false;
  }

  private BlockState moisturize(BlockState blockstate) {
    try {
      if (blockstate.getBlock() == Blocks.FARMLAND && GardenMod.config.getMoisture() > 0)
        blockstate = blockstate.with(FarmlandBlock.MOISTURE, GardenMod.config.getMoisture());
    }
    catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
    }
    return blockstate;
  }
}
