package com.coyote.coyotemod;

import com.coyote.coyotemod.config.ModConfig;
import com.coyote.coyotemod.registry.*;
import com.coyote.coyotemod.websocket.WebSocketServerManager;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

@Mod(CoyoteMod.MODID)
public class CoyoteMod {
    public static final String MODID = "coyotemod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CoyoteMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(Type.COMMON, ModConfig.SPEC);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenus.register(modEventBus);
        ModCreativeTab.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            WebSocketServerManager.getInstance().start();
            LOGGER.info("[Coyote] WebSocket server started");
        });
    }

    private void onServerStopping(ServerStoppingEvent event) {
        WebSocketServerManager.getInstance().stop();
    }
}
