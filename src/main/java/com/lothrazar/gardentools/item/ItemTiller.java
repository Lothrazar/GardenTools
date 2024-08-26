package com.lothrazar.gardentools.item;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.lothrazar.gardentools.GardenMod;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemTiller extends HoeItem {

  public ItemTiller(Tier tier, Properties builder) {
    super(tier, -4, 0.0F, builder);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
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
    hoeTillLongrange(context);
    return InteractionResult.SUCCESS;
  }

  /**
   * covers the tilling shape
   * 
   * @param context
   */
  private void hoeTillLongrange(UseOnContext context) {
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
  }

  /**
   * Makes a new UseOnContext with the given position to till this portion. Not restricted to vanilla hoes and farmland, it uses the consumer/predicate pair to be compatible
   * 
   * @param contextIn
   * @param blockpos
   * @return
   */
  private boolean hoeBlock(UseOnContext contextIn, BlockPos blockpos) {
    Level world = contextIn.getLevel();
    //for THIS block, does it have any tilable actoins registered
    Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> hoeAction = HoeItem.TILLABLES.get(world.getBlockState(blockpos).getBlock());
    if (hoeAction != null) {
      //yes this block has an action  
      //wrap up a new context for the new relative position. dont reuse incoming context
      UseOnContext newContext = new UseOnContext(contextIn.getPlayer(), contextIn.getHand(),
          new BlockHitResult(contextIn.getPlayer().getUpVector(0), contextIn.getHorizontalDirection(), blockpos, false));
      //does it pass the predicate? can we do tilling
      if (hoeAction.getFirst().test(newContext)) {
        //accept runs the tilling action for the farmland
        hoeAction.getSecond().accept(newContext);
        Player player = contextIn.getPlayer();
        if (player != null) {
          //till sound at this position
          player.level.playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
          //damage the item
          contextIn.getItemInHand().hurtAndBreak(1, player, (p) -> {
            p.broadcastBreakEvent(contextIn.getHand());
          });
        }
        //custom moisture action from this config
        this.moisturize(world, blockpos);
        return true;
      }
    }
    return false;
  }

  private void moisturize(Level world, BlockPos pos) {
    var blockstate = world.getBlockState(pos);
    try {
      //hoe can do things other than farmland
      if (blockstate.hasProperty(FarmBlock.MOISTURE) && GardenMod.CONFIG.getMoisture() > 0) {
        //may or may not be mojang farmland
        world.setBlock(pos, blockstate.setValue(FarmBlock.MOISTURE, GardenMod.CONFIG.getMoisture()), 3);
      }
    }
    catch (Exception e) {
      GardenMod.LOGGER.error("ItemTiller Moisturize error", e);
    }
  }
}
