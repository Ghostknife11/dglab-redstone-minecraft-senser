package com.coyote.coyotemod.block;

import com.coyote.coyotemod.gui.SensorMenu;
import com.coyote.coyotemod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class GimbalSensorBlock extends Block implements EntityBlock, MenuProvider {

    public GimbalSensorBlock(Properties properties) {
        super(properties);
    }

    private BlockPos menuPos;

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            menuPos = pos;
            player.openMenu(this, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        return new SensorMenu(containerId, inv, menuPos != null ? menuPos : player.blockPosition());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.coyotemod.sensor");
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GimbalSensorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                   BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof GimbalSensorBlockEntity sensor) sensor.tick();
        };
    }
}
