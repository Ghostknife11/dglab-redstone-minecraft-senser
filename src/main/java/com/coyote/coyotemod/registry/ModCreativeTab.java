package com.coyote.coyotemod.registry;

import com.coyote.coyotemod.CoyoteMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTab {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CoyoteMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> COYOTE_TAB =
            TABS.register("coyote_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.coyotemod"))
                    .icon(() -> ModItems.GIMBAL_SENSOR.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(ModItems.GIMBAL_SENSOR.get());
                    }).build());

    public static void register(IEventBus bus) { TABS.register(bus); }
}
