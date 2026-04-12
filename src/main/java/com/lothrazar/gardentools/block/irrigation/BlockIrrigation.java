package com.lothrazar.gardentools.block.irrigation;

import com.lothrazar.gardentools.GardenConfigManager;
import com.lothrazar.gardentools.GardenRegistry;
import com.lothrazar.library.block.EntityBlockFlib;
import com.lothrazar.library.util.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.minecraft.world.InteractionHand;

public class BlockIrrigation extends EntityBlockFlib {

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
  public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (GardenConfigManager.WATERSRC.get()) {
      if (!world.isClientSide) {
        IFluidHandler handler = world.getCapability(Capabilities.FluidHandler.BLOCK, pos, hit.getDirection());
        if (handler != null) {
          if (FluidUtil.interactWithFluidHandler(player, hand, handler)) {
            if (player instanceof ServerPlayer sp) {
              SoundUtil.playSoundFromServer(sp, pos, SoundEvents.BUCKET_FILL, 1, 1);
            }
          }
        }
      }
      if (stack.getCapability(Capabilities.FluidHandler.ITEM) != null) {
        return ItemInteractionResult.SUCCESS;
      }
    }
    return super.useItemOn(stack, state, world, pos, player, hand, hit);
  }
}
