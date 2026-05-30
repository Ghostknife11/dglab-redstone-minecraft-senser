package com.coyote.coyotemod.block;

import com.coyote.coyotemod.registry.ModBlockEntities;
import com.coyote.coyotemod.websocket.WebSocketServerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GimbalSensorBlockEntity extends BlockEntity {

    private int signal;
    private int tickCounter;
    private static final int UPDATE_INTERVAL = 4;

    public GimbalSensorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GIMBAL_SENSOR.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        tickCounter++;
        if (tickCounter < UPDATE_INTERVAL) return;
        tickCounter = 0;

        int newSignal = level.getBestNeighborSignal(worldPosition);
        if (newSignal != signal) {
            signal = newSignal;
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }

        float pct = signal / 15f;
        WebSocketServerManager ws = WebSocketServerManager.getInstance();
        if (!ws.isConnected()) return;

        // A strength = pct% of app A max, B strength = pct% of app B max
        int strA = (int) (pct * ws.getAppAMax());
        int strB = (int) (pct * ws.getAppBMax());
        ws.sendPulse(strA, strB);
    }

    public int getSignal() { return signal; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Signal", signal);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        signal = tag.getInt("Signal");
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Signal", signal);
        return tag;
    }
}
