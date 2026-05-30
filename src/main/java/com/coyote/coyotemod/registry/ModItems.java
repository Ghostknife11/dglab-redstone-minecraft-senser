package com.coyote.coyotemod.registry;

import com.coyote.coyotemod.CoyoteMod;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CoyoteMod.MODID);

    public static final DeferredItem<BlockItem> GIMBAL_SENSOR =
            ITEMS.registerSimpleBlockItem(ModBlocks.GIMBAL_SENSOR);

    public static void register(IEventBus bus) { ITEMS.register(bus); }
}
