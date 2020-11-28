package com.lothrazar.gardentools.item;

import java.util.List;
import javax.annotation.Nullable;
import com.lothrazar.gardentools.GardenMod;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

public class ItemPlanter extends Item {

  private static final String FORGE_SEEDS = "forge:seeds";

  public ItemPlanter(Properties properties) {
    super(properties);
    CarrotBlock x;
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    super.onItemUse(context);
    World world = context.getWorld();
    PlayerEntity player = context.getPlayer();
    ItemStack seeds = ItemStack.EMPTY;
    Direction face = context.getPlacementHorizontalFacing();
    BlockPos center = context.getPos().up();
    BlockPos blockpos = null;
    int countPlanted = 0;
    for (int dist = 0; dist < GardenMod.config.getPlantingRange(); dist++) {
      //get seed ready if any are left
      if (seeds.isEmpty()) {
        seeds = getSeed(player);
        if (seeds.isEmpty()) {
          break;//for sure done
        }
      }
      //advance position
      blockpos = center.offset(face, dist);
      //manage going up/down by 1 elevation  
      boolean didPlant = false;
      if (world.isAirBlock(blockpos.down())) {
        //air here, went off an edge. try to go down 1
        blockpos = blockpos.down();
        if (world.isAirBlock(blockpos)) {
          if (tryPlantHere(world, seeds, blockpos)) {
            center = center.down();//go down the hill
            didPlant = true;
          }
        }
      }
      else if (world.isAirBlock(blockpos)) {
        //at my elevation
        if (tryPlantHere(world, seeds, blockpos)) {
          didPlant = true;
        }
      }
      else {
        //try going up by 1
        blockpos = blockpos.up();
        if (world.isAirBlock(blockpos.up())) {
          if (tryPlantHere(world, seeds, blockpos)) {
            center = center.up();//go up the hill
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
      context.getItem().damageItem(countPlanted, player, (p) -> {
        p.sendBreakAnimation(context.getHand());
      });
    }
    return ActionResultType.SUCCESS;
  }

  private boolean tryPlantHere(World world, ItemStack seeds, BlockPos blockpos) {
    boolean didPlant = false;
    if (world.getBlockState(blockpos.down()).getBlock() == Blocks.FARMLAND
        && world.isAirBlock(blockpos)) {
      // looks valid. try to plant
      if (world.setBlockState(blockpos, Block.getBlockFromItem(seeds.getItem()).getDefaultState())) {
        didPlant = true;
      }
    }
    return didPlant;
  }

  private ItemStack getSeed(PlayerEntity player) {
    ItemStack seeds = ItemStack.EMPTY;
    for (ItemStack s : player.inventory.mainInventory) {
      if (!s.isEmpty()) {
        //        Item item = s.getItem();
        if (s.getItem().isIn(Tags.Items.SEEDS)) {
          seeds = s;
          break;
        }
        else {
          //
          //
          //LUA
          //
        }
      }
    }
    return seeds;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationTextComponent t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    t.mergeStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }
}
