package com.lothrazar.gardentools.block.irrigation;

import com.lothrazar.gardentools.GardenConfigManager;
import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.library.block.BaseEntityBlockFlib;
import com.lothrazar.library.util.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class BlockIrrigation extends BaseEntityBlockFlib {

  public BlockIrrigation(Properties properties) {
    super(properties.strength(1.3F).noOcclusion());
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileIrrigation(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, GardenRegistry.TE_IRRIGATION_CORE.get(), world.isClientSide ? null : TileIrrigation::serverTick);
  }

  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (GardenConfigManager.WATERSRC.get()) {
      if (!world.isClientSide) {
        BlockEntity tankHere = world.getBlockEntity(pos);
        if (tankHere != null) {
          IFluidHandler handler = tankHere.getCapability(ForgeCapabilities.FLUID_HANDLER, hit.getDirection()).orElse(null);
          if (handler != null) {
            if (FluidUtil.interactWithFluidHandler(player, hand, handler)) {
              //success so display new amount
              //and also play the fluid sound
              if (player instanceof ServerPlayer sp) {
                SoundUtil.playSoundFromServer(sp, pos, SoundEvents.BUCKET_FILL, 1, 1);
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
}
