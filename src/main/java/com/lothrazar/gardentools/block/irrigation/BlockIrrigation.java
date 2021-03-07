package com.lothrazar.gardentools.block.irrigation;

import com.lothrazar.gardentools.ConfigManager;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BlockIrrigation extends Block {

  public BlockIrrigation(Properties properties) {
    super(properties.hardnessAndResistance(1.3F).notSolid());
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileIrrigation();
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    TranslationTextComponent t = new TranslationTextComponent(getTranslationKey() + ".tooltip");
    t.mergeStyle(TextFormatting.GRAY);
    tooltip.add(t);
  }

  @SuppressWarnings("deprecation")
  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    if (ConfigManager.WATERSRC.get()) {
      if (!world.isRemote) {
        TileEntity tankHere = world.getTileEntity(pos);
        if (tankHere != null) {
          IFluidHandler handler = tankHere.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit.getFace()).orElse(null);
          if (handler != null) {
            if (FluidUtil.interactWithFluidHandler(player, hand, handler)) {
              //success so display new amount
              //and also play the fluid sound
              if (player instanceof ServerPlayerEntity) {
                playSoundFromServer((ServerPlayerEntity) player, SoundEvents.ITEM_BUCKET_FILL);
              }
            }
          }
        }
      }
      if (FluidUtil.getFluidHandler(player.getHeldItem(hand)).isPresent()) {
        return ActionResultType.SUCCESS;
      }
    }
    return super.onBlockActivated(state, world, pos, player, hand, hit);
  }

  public static void playSoundFromServer(ServerPlayerEntity entityIn, SoundEvent soundIn) {
    if (soundIn == null || entityIn == null) {
      return;
    }
    entityIn.connection.sendPacket(new SPlaySoundEffectPacket(
        soundIn,
        SoundCategory.BLOCKS,
        entityIn.lastTickPosX, entityIn.lastTickPosY, entityIn.lastTickPosZ,
        1.0f, 1.0f));
  }
}
