package com.coyote.coyotemod.websocket;

import com.coyote.coyotemod.CoyoteMod;
import com.coyote.coyotemod.config.ModConfig;
import com.coyote.coyotemod.util.WaveformGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class WebSocketServerManager {
    private static final WebSocketServerManager INSTANCE = new WebSocketServerManager();
    private static final Gson GSON = new Gson();
    private static final String FIXED_CLIENT_ID = "1234-123456789-12345-12345-01";

    private WebSocketServer server;
    private WebSocket connectedClient;
    private String generatedClientId;
    private String targetId;
    private String appClientId;
    private volatile boolean isRunning, isBound;
    private ScheduledExecutorService heartbeatExecutor;
    private ScheduledFuture<?> heartbeatTask;

    private int appAMax = 100, appBMax = 100;

    private WebSocketServerManager() {}

    public static WebSocketServerManager getInstance() { return INSTANCE; }

    // --- Lifecycle ---

    public void start() {
        if (isRunning) return;
        int port = ModConfig.WS_PORT.get();
        try {
            server = new CoyoteWSServer(new InetSocketAddress(port));
            server.start();
            isRunning = true;
            heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
            CoyoteMod.LOGGER.info("[Coyote] WS server started on port {}", port);
        } catch (Exception e) {
            CoyoteMod.LOGGER.error("[Coyote] Failed to start WS server: {}", e.getMessage());
        }
    }

    public void stop() {
        isRunning = false;
        stopHeartbeat();
        if (heartbeatExecutor != null) { heartbeatExecutor.shutdown(); heartbeatExecutor = null; }
        if (server != null) { try { server.stop(1000); } catch (Exception e) { Thread.currentThread().interrupt(); } server = null; }
        clearState();
    }

    // --- Status ---

    public boolean isRunning() { return isRunning; }
    public boolean isConnected() { return isBound && connectedClient != null && connectedClient.isOpen(); }
    public String getServerAddress() { return resolveHost(); }
    public int getServerPort() { return ModConfig.WS_PORT.get(); }
    public String getAppClientId() { return appClientId; }
    public int getAppAMax() { return appAMax; }
    public int getAppBMax() { return appBMax; }

    public String getQrCodeUrl() {
        return "https://www.dungeon-lab.com/app-download.php#DGLAB-SOCKET#ws://"
                + resolveHost() + ":" + getServerPort() + "/" + FIXED_CLIENT_ID;
    }

    // --- Output ---

    private int waveTick;
    private boolean pendingClear;
    private String[] waveData;
    private static final int WAVEFORM_REFRESH = 5; // every 5×4=20 ticks = 1s

    /** Send synced waveform + independent A/B strength. */
    public void sendPulse(int strengthA, int strengthB) {
        if (!isConnected()) return;

        waveTick++;
        if (waveData == null || waveTick >= WAVEFORM_REFRESH) {
            waveData = WaveformGenerator.generate().hexData();
            waveTick = 0;
            pendingClear = false;
        }

        if (waveTick == 1 && !pendingClear) {
            // Phase 1: clear
            sendMsg("clear-1");
            sendMsg("clear-2");
            pendingClear = true;
        } else if (pendingClear) {
            // Phase 2: waveform
            String pulse = WaveformGenerator.buildPulseCommand("A", waveData);
            sendMsg(pulse);
            sendMsg(pulse.replace("pulse-A", "pulse-B"));
            pendingClear = false;
        }

        sendMsg("strength-1+2+" + Math.min(200, Math.max(0, strengthA)));
        sendMsg("strength-2+2+" + Math.min(200, Math.max(0, strengthB)));
    }

    private void sendMsg(String message) {
        if (!isConnected() || connectedClient == null) return;
        JsonObject json = new JsonObject();
        json.addProperty("type", "msg");
        json.addProperty("clientId", FIXED_CLIENT_ID);
        json.addProperty("targetId", targetId != null ? targetId : "");
        json.addProperty("message", message);
        connectedClient.send(json.toString());
    }

    // --- Internal ---

    private String resolveHost() {
        String configured = ModConfig.WS_HOST.get();
        if (configured != null && !configured.isBlank() && !"0.0.0.0".equals(configured)
                && !"localhost".equalsIgnoreCase(configured) && !"127.0.0.1".equals(configured)) {
            return configured;
        }
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ni.isLoopback() || !ni.isUp()) continue;
                for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        String ip = addr.getHostAddress();
                        if (ip != null && !ip.isBlank()) return ip;
                    }
                }
            }
        } catch (SocketException ignored) {}
        return "127.0.0.1";
    }

    private void startHeartbeat() {
        if (heartbeatTask != null) heartbeatTask.cancel(false);
        if (heartbeatExecutor == null) return;
        heartbeatTask = heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (isConnected()) {
                JsonObject json = new JsonObject();
                json.addProperty("type", "heartbeat");
                json.addProperty("clientId", FIXED_CLIENT_ID);
                json.addProperty("targetId", targetId != null ? targetId : "");
                json.addProperty("message", "200");
                connectedClient.send(json.toString());
            } else if (heartbeatTask != null) {
                heartbeatTask.cancel(false);
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    private void stopHeartbeat() {
        if (heartbeatTask != null) { heartbeatTask.cancel(false); heartbeatTask = null; }
    }

    private void clearState() {
        connectedClient = null;
        generatedClientId = null;
        targetId = null;
        appClientId = null;
        isBound = false;
        appAMax = 100; appBMax = 100;
        waveTick = 0;
        pendingClear = false;
        waveData = null;
    }

    // --- WebSocket Server ---

    private class CoyoteWSServer extends WebSocketServer {
        CoyoteWSServer(InetSocketAddress addr) { super(addr); setReuseAddr(true); }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            if (connectedClient != null && connectedClient.isOpen()) {
                JsonObject err = new JsonObject();
                err.addProperty("type", "error"); err.addProperty("message", "400");
                conn.send(err.toString());
                conn.close(4000, "Already connected");
                return;
            }
            appAMax = 100; appBMax = 100;
            generatedClientId = UUID.randomUUID().toString();
            isBound = false;

            JsonObject bind = new JsonObject();
            bind.addProperty("type", "bind");
            bind.addProperty("clientId", generatedClientId);
            bind.addProperty("targetId", "");
            bind.addProperty("message", "targetId");
            conn.send(bind.toString());

            connectedClient = conn;
            targetId = generatedClientId;
            startHeartbeat();
            CoyoteMod.LOGGER.info("[Coyote] WS client connected");
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            if (conn == connectedClient) { stopHeartbeat(); clearState(); }
            CoyoteMod.LOGGER.info("[Coyote] WS client disconnected");
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            CoyoteMod.LOGGER.warn("[Coyote] WS error: {}", ex.getMessage());
        }

        @Override
        public void onStart() {
            CoyoteMod.LOGGER.info("[Coyote] WS server listening");
        }

        @Override
        public void onMessage(WebSocket conn, String text) {
            if (conn != connectedClient) return;
            try {
                JsonObject json = GSON.fromJson(text, JsonObject.class);
                String type = json.has("type") ? json.get("type").getAsString() : "";
                String msg = json.has("message") ? json.get("message").getAsString() : "";
                String cId = json.has("clientId") ? json.get("clientId").getAsString() : "";

                switch (type) {
                    case "bind" -> {
                        String tId = json.has("targetId") ? json.get("targetId").getAsString() : "";
                        if ("DGLAB".equals(msg) && generatedClientId != null
                                && generatedClientId.equals(tId)) {
                            appClientId = cId;
                            isBound = true;
                            JsonObject resp = new JsonObject();
                            resp.addProperty("type", "bind");
                            resp.addProperty("clientId", FIXED_CLIENT_ID);
                            resp.addProperty("targetId", appClientId);
                            resp.addProperty("message", "200");
                            resp.addProperty("statusCode", 200);
                            conn.send(resp.toString());
                            CoyoteMod.LOGGER.info("[Coyote] Bound with app: {}", appClientId);
                        }
                    }
                    case "heartbeat" -> {
                        JsonObject resp = new JsonObject();
                        resp.addProperty("type", "heartbeat");
                        resp.addProperty("clientId", FIXED_CLIENT_ID);
                        resp.addProperty("targetId", appClientId != null ? appClientId : "");
                        resp.addProperty("message", "200");
                        conn.send(resp.toString());
                    }
                    case "msg" -> {
                        if (msg.startsWith("strength-")) {
                            String[] parts = msg.substring(9).split("\\+");
                            if (parts.length >= 4) {
                                try { appAMax = Integer.parseInt(parts[2]); appBMax = Integer.parseInt(parts[3]); }
                                catch (NumberFormatException ignored) {}
                            }
                        }
                    }
                }
            } catch (Exception e) {
                CoyoteMod.LOGGER.debug("[Coyote] WS parse error: {}", e.getMessage());
            }
        }
    }
}
