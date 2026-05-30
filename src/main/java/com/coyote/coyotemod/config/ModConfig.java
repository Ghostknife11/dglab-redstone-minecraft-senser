package com.coyote.coyotemod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue WS_PORT;
    public static final ModConfigSpec.ConfigValue<String> WS_HOST;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("websocket");
        WS_PORT = builder.comment("WebSocket server port").defineInRange("port", 8877, 1024, 65535);
        WS_HOST = builder.comment("WebSocket host (0.0.0.0 = all interfaces)").define("host", "0.0.0.0");
        builder.pop();
        SPEC = builder.build();
    }
}
