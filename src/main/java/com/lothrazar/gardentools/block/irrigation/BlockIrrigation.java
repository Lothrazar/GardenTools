package com.lothrazar.gardentools.block.irrigation;

import com.lothrazar.gardentools.ConfigManager;
import java.util.List;
import javax.annotation.Nullable;

import com.lothrazar.gardentools.GardenRegistry;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockIrrigation extends BaseEntityBlock {

  public BlockIrrigation(Properties properties) {
    super(properties.strength(1.3F).noOcclusion());
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileIrrigation(pos, state);
  }

  @Override
  public RenderShape getRenderShape(BlockState bs) {
    return RenderShape.MODEL;
  }
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, GardenRegistry.IRRIGATIONTILE, world.isClientSide ? null : TileIrrigation::serverTick);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    TranslatableComponent t = new TranslatableComponent(getDescriptionId() + ".tooltip");
    t.withStyle(ChatFormatting.GRAY);
    tooltip.add(t);
  }

  @SuppressWarnings("deprecation")
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (ConfigManager.WATERSRC.get()) {
      if (!world.isClientSide) {
        BlockEntity tankHere = world.getBlockEntity(pos);
        if (tankHere != null) {
          IFluidHandler handler = tankHere.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit.getDirection()).orElse(null);
          if (handler != null) {
            if (FluidUtil.interactWithFluidHandler(player, hand, handler)) {
              //success so display new amount
              //and also play the fluid sound
              if (player instanceof ServerPlayer) {
                playSoundFromServer((ServerPlayer) player, SoundEvents.BUCKET_FILL);
              }
            }
          }
        }
      }
      if (FluidUtil.getFluidHandler(player.getItemInHand(hand)).isPresent()) {
        return InteractionResult.SUCCESS;
      }
    }
    return super.use(state, world, pos, player, hand, hit);
  }

  public static void playSoundFromServer(ServerPlayer entityIn, SoundEvent soundIn) {
    if (soundIn == null || entityIn == null) {
      return;
    }
    entityIn.connection.send(new ClientboundSoundPacket(
        soundIn,
        SoundSource.BLOCKS,
        entityIn.xOld, entityIn.yOld, entityIn.zOld,
        1.0f, 1.0f));
  }
}
