package com.lothrazar.gardentools.block.magnet;

import com.lothrazar.gardentools.GardenRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class BlockMagnet extends BaseEntityBlock {

  VoxelShape shape = Block.box(6.0D, 0.0D, 6.0D,
      10.0D, 8.0D, 10.0D);

  public BlockMagnet(Properties properties) {
    super(properties.strength(1.3F).harvestTool(ToolType.PICKAXE).noOcclusion()
        .lightLevel(state -> 7));
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return shape;
  }

  @Override
  public RenderShape getRenderShape(BlockState bs) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileMagnet(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, GardenRegistry.MAGNETTILE, world.isClientSide ? null : TileMagnet::serverTick);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    TranslatableComponent t = new TranslatableComponent(getDescriptionId() + ".tooltip");
    t.withStyle(ChatFormatting.GRAY);
    tooltip.add(t);
  }
}
