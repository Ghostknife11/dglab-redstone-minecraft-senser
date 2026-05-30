package com.coyote.coyotemod.registry;

import com.coyote.coyotemod.CoyoteMod;
import com.coyote.coyotemod.block.GimbalSensorBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CoyoteMod.MODID);

    public static final DeferredBlock<GimbalSensorBlock> GIMBAL_SENSOR = BLOCKS.register("gimbal_sensor",
            () -> new GimbalSensorBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    public static void register(IEventBus bus) { BLOCKS.register(bus); }
}
