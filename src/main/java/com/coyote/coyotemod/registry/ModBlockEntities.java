package com.coyote.coyotemod.registry;

import com.coyote.coyotemod.CoyoteMod;
import com.coyote.coyotemod.block.GimbalSensorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CoyoteMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GimbalSensorBlockEntity>>
            GIMBAL_SENSOR = BLOCK_ENTITIES.register("gimbal_sensor",
            () -> BlockEntityType.Builder.of(
                    GimbalSensorBlockEntity::new, ModBlocks.GIMBAL_SENSOR.get()).build(null));

    public static void register(IEventBus bus) { BLOCK_ENTITIES.register(bus); }
}
