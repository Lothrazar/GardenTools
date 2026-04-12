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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ItemTiller extends HoeItem {

  public ItemTiller(Tier tier, Properties builder) {
    super(tier, builder.stacksTo(1).durability(777));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
    tooltip.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    if (context.getClickedFace() == Direction.DOWN) {
      return InteractionResult.FAIL;
    }
    hoeTillLongrange(context);
    return InteractionResult.SUCCESS;
  }

  /**
   * covers the tilling shape
   */
  private void hoeTillLongrange(UseOnContext context) {
    Level world = context.getLevel();
    BlockPos center = context.getClickedPos();
    Direction face = context.getHorizontalDirection();
    BlockPos blockpos = null;
    for (int dist = 0; dist < GardenConfigManager.getTillingRange(); dist++) {
      blockpos = center.relative(face, dist);
      if (world.isEmptyBlock(blockpos)) {
        blockpos = blockpos.below();
        if (world.isEmptyBlock(blockpos.above())) {
          if (hoeBlock(context, blockpos)) {
            center = center.below();
          }
        }
      }
      else if (world.isEmptyBlock(blockpos.above())) {
        hoeBlock(context, blockpos);
      }
      else {
        blockpos = blockpos.above();
        if (world.isEmptyBlock(blockpos.above())) {
          if (hoeBlock(context, blockpos)) {
            center = center.above();
          }
        }
      }
    }
  }

  /**
   * Makes a new UseOnContext with the given position to till this portion. Not restricted to vanilla hoes and farmland, it uses the consumer/predicate pair to be compatible
   */
  private boolean hoeBlock(UseOnContext contextIn, BlockPos blockpos) {
    Level world = contextIn.getLevel();
    Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> hoeAction = HoeItem.TILLABLES.get(world.getBlockState(blockpos).getBlock());
    if (hoeAction != null) {
      UseOnContext newContext = new UseOnContext(contextIn.getPlayer(), contextIn.getHand(),
          new BlockHitResult(contextIn.getPlayer().getUpVector(0), contextIn.getHorizontalDirection(), blockpos, false));
      if (hoeAction.getFirst().test(newContext)) {
        hoeAction.getSecond().accept(newContext);
        Player player = contextIn.getPlayer();
        if (player != null) {
          world.playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
          EquipmentSlot slot = contextIn.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
          contextIn.getItemInHand().hurtAndBreak(1, player, slot);
        }
        this.moisturize(world, blockpos);
        return true;
      }
    }
    return false;
  }

  private void moisturize(Level world, BlockPos pos) {
    var blockstate = world.getBlockState(pos);
    try {
      if (blockstate.hasProperty(FarmBlock.MOISTURE) && GardenConfigManager.getMoisture() > 0) {
        world.setBlock(pos, blockstate.setValue(FarmBlock.MOISTURE, GardenConfigManager.getMoisture()), 3);
      }
    }
    catch (Exception e) {
      GardenMod.LOGGER.error("ItemTiller Moisturize error", e);
    }
  }
}
