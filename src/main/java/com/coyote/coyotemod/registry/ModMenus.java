package com.coyote.coyotemod.registry;

import com.coyote.coyotemod.CoyoteMod;
import com.coyote.coyotemod.gui.SensorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, CoyoteMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<SensorMenu>> SENSOR =
            MENUS.register("gimbal_sensor",
                    () -> new MenuType<>(SensorMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus bus) { MENUS.register(bus); }
}
