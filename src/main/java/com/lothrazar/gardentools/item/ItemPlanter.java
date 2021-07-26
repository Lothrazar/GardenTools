package com.lothrazar.gardentools.item;

import com.lothrazar.gardentools.GardenMod;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

import net.minecraft.world.item.Item.Properties;

public class ItemPlanter extends Item {

  public ItemPlanter(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    super.useOn(context);
    Level world = context.getLevel();
    Player player = context.getPlayer();
    ItemStack seeds = ItemStack.EMPTY;
    Direction face = context.getHorizontalDirection();
    BlockPos center = context.getClickedPos().above();
    BlockPos blockpos = null;
    int countPlanted = 0;
    for (int dist = 0; dist < GardenMod.CONFIG.getPlantingRange(); dist++) {
      //get seed ready if any are left
      if (seeds.isEmpty()) {
        seeds = getSeed(player);
        if (seeds.isEmpty()) {
          break;
          //for sure done
        }
      }
      //advance position
      blockpos = center.relative(face, dist);
      //manage going up/down by 1 elevation  
      boolean didPlant = false;
      if (world.isEmptyBlock(blockpos.below())) {
        //air here, went off an edge. try to go down 1
        blockpos = blockpos.below();
        if (world.isEmptyBlock(blockpos)) {
          if (tryPlantHere(world, seeds, blockpos)) {
            center = center.below();
            //go down the hill
            didPlant = true;
          }
        }
      }
      else if (world.isEmptyBlock(blockpos)) {
        //at my elevation
        if (tryPlantHere(world, seeds, blockpos)) {
          didPlant = true;
        }
      }
      else {
        //try going up by 1
        blockpos = blockpos.above();
        if (world.isEmptyBlock(blockpos.above())) {
          if (tryPlantHere(world, seeds, blockpos)) {
            center = center.above();
            //go up the hill
            didPlant = true;
          }
        }
      }
      if (didPlant) {
        countPlanted++;
        seeds.shrink(1);
      }
    }
    //loop is complete
    if (player != null && countPlanted > 0) {
      context.getItemInHand().hurtAndBreak(countPlanted, player, (p) -> {
        p.broadcastBreakEvent(context.getHand());
      });
    }
    return InteractionResult.SUCCESS;
  }

  private boolean tryPlantHere(Level world, ItemStack seeds, BlockPos blockpos) {
    boolean didPlant = false;
    if (world.getBlockState(blockpos.below()).getBlock() == Blocks.FARMLAND
        && world.isEmptyBlock(blockpos)) {
      // looks valid. try to plant
      if (world.setBlockAndUpdate(blockpos, Block.byItem(seeds.getItem()).defaultBlockState())) {
        didPlant = true;
      }
    }
    return didPlant;
  }

  private ItemStack getSeed(Player player) {
    for (ItemStack stack : player.inventory.items) {
      if (!stack.isEmpty()) {
        if (stack.getItem().is(Tags.Items.SEEDS)) {
          return stack;
        }
        else {
          Block block = Block.byItem(stack.getItem());
          if (block instanceof CropBlock) {
            return stack;
          }
          // b instanceof IPlantable
        }
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    TranslatableComponent t = new TranslatableComponent(getDescriptionId() + ".tooltip");
    t.withStyle(ChatFormatting.GRAY);
    tooltip.add(t);
  }
}
