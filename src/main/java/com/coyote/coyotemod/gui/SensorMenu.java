package com.coyote.coyotemod.gui;

import com.coyote.coyotemod.block.GimbalSensorBlockEntity;
import com.coyote.coyotemod.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SensorMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    private final Level level; // server level for BE access in broadcastChanges
    public final DataSlot signal = DataSlot.standalone();
    public final DataSlot appAMax = DataSlot.standalone();
    public final DataSlot appBMax = DataSlot.standalone();

    // Client constructor
    public SensorMenu(int containerId, Inventory inventory) {
        super(ModMenus.SENSOR.get(), containerId);
        this.pos = BlockPos.ZERO;
        this.level = null;
        addDataSlot(signal);
        addDataSlot(appAMax);
        addDataSlot(appBMax);
    }

    // Server constructor
    public SensorMenu(int containerId, Inventory inventory, BlockPos pos) {
        super(ModMenus.SENSOR.get(), containerId);
        this.pos = pos;
        this.level = inventory.player.level();
        addDataSlot(signal);
        addDataSlot(appAMax);
        addDataSlot(appBMax);
    }

    @Override
    public void broadcastChanges() {
        if (level != null && !level.isClientSide && pos != BlockPos.ZERO) {
            if (level.getBlockEntity(pos) instanceof GimbalSensorBlockEntity be) {
                signal.set(be.getSignal());
            }
        }
        super.broadcastChanges();
    }

    public BlockPos getBlockPos() { return pos; }

    public GimbalSensorBlockEntity getBlockEntity(Player player) {
        if (pos == BlockPos.ZERO) return null;
        if (player.level().getBlockEntity(pos) instanceof GimbalSensorBlockEntity be) return be;
        return null;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (pos == BlockPos.ZERO) return true;
        return player.level().getBlockEntity(pos) instanceof GimbalSensorBlockEntity;
    }
}
