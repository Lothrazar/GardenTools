package com.lothrazar.gardentools.item;

import java.util.List;
import javax.annotation.Nullable;
import com.lothrazar.gardentools.GardenMod;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPlanter extends Item {

  private static final String FORGE_SEEDS = "forge:seeds";

  public ItemPlanter(Properties properties) {
    super(properties);
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    super.onItemUse(context);
    World world = context.getWorld();
    PlayerEntity player = context.getPlayer();
    ItemStack seeds = ItemStack.EMPTY;
    if (seeds.isEmpty()) {
      return ActionResultType.FAIL;
    }
    Direction face = context.getPlacementHorizontalFacing();
    BlockPos center = context.getPos().up();
    BlockPos blockpos = null;
    for (int dist = 0; dist < GardenMod.config.getTillingRange(); dist++) {
      blockpos = center.offset(face, dist);
      if (world.getBlockState(blockpos.down()).getBlock() == Blocks.FARMLAND
          && world.isAirBlock(blockpos)) {
        world.setBlockState(blockpos, Block.getBlockFromItem(seeds.getItem()).getDefaultState());
        seeds.shrink(1);
        if (seeds.isEmpty()) {
          seeds = getSeed(player);
          if (seeds.isEmpty()) {
            break;//for sure done
          }
        }
      }
    }
    if (player != null) {
      context.getItem().damageItem(1, player, (p) -> {
        p.sendBreakAnimation(context.getHand());
      });
    }
    return ActionResultType.SUCCESS;
  }

  private ItemStack getSeed(PlayerEntity player) {
    ItemStack seeds = ItemStack.EMPTY;
    for (ItemStack s : player.inventory.mainInventory) {
      if (!s.isEmpty()) {
        Item item = s.getItem();
        for (ResourceLocation st : item.getTags()) {
          if (st.toString().equalsIgnoreCase(FORGE_SEEDS)) {
            seeds = s;
            break;
          }
        }
      }
    }
    return seeds;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationTextComponent t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    t.applyTextStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }
}
