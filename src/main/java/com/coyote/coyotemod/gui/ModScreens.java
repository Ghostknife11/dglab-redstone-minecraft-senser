package com.coyote.coyotemod.gui;

import com.coyote.coyotemod.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "coyotemod", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreens {
    @SubscribeEvent
    public static void register(RegisterMenuScreensEvent event) {
        event.register(ModMenus.SENSOR.get(), SensorScreen::new);
    }
}
