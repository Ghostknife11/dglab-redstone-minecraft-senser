package com.coyote.coyotemod.gui;

import com.coyote.coyotemod.util.QRCodeGenerator;
import com.coyote.coyotemod.websocket.WebSocketServerManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.image.BufferedImage;

public class SensorScreen extends AbstractContainerScreen<SensorMenu> {

    private ResourceLocation qrTexture;

    public SensorScreen(SensorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 260;
        this.imageHeight = 170;
    }

    @Override
    protected void init() {
        super.init();

        // Generate QR once in init (runs on main thread, safe for AWT)
        if (qrTexture == null) {
            String url = WebSocketServerManager.getInstance().getQrCodeUrl();
            BufferedImage img = QRCodeGenerator.generateQRCode(url);
            if (img != null) {
                int iw = img.getWidth(), ih = img.getHeight();
                NativeImage ni = new NativeImage(iw, ih, false);
                for (int y = 0; y < ih; y++)
                    for (int x = 0; x < iw; x++) {
                        int rgb = img.getRGB(x, y);
                        ni.setPixelRGBA(x, y, ((rgb >> 24) & 0xFF) << 24
                                | (rgb & 0xFF) << 16 | ((rgb >> 8) & 0xFF) << 8 | ((rgb >> 16) & 0xFF));
                    }
                DynamicTexture tex = new DynamicTexture(ni);
                tex.setFilter(false, false);
                qrTexture = ResourceLocation.fromNamespaceAndPath("coyotemod",
                        "qr_" + System.currentTimeMillis());
                minecraft.getTextureManager().register(qrTexture, tex);
            }
        }

        // Refresh QR button
        addRenderableWidget(Button.builder(Component.literal("Refresh QR"), b -> {
            if (qrTexture != null) { minecraft.getTextureManager().release(qrTexture); qrTexture = null; }
            init(); // re-generate
        }).pos(leftPos + 10, topPos + 148).size(80, 16).build());

        // Copy URL button
        addRenderableWidget(Button.builder(Component.literal("Copy URL"), b ->
                Minecraft.getInstance().keyboardHandler
                        .setClipboard(WebSocketServerManager.getInstance().getQrCodeUrl()))
                .pos(leftPos + 170, topPos + 148).size(80, 16).build());
    }

    @Override
    protected void renderBg(GuiGraphics g, float delta, int mx, int my) {
        int l = leftPos, t = topPos, w = imageWidth, h = imageHeight;

        // Background
        g.fill(l, t, l + w, t + h, 0xC8101010);
        // Border (manual — renderOutline doesn't exist in 1.21.1)
        g.fill(l, t, l + w, t + 1, 0xFF555555);
        g.fill(l, t + h - 1, l + w, t + h, 0xFF555555);
        g.fill(l, t, l + 1, t + h, 0xFF555555);
        g.fill(l + w - 1, t, l + w, t + h, 0xFF555555);

        WebSocketServerManager ws = WebSocketServerManager.getInstance();

        // === QR code (left) ===
        int qrS = 80;
        int qrX = l + 10, qrY = t + 10;
        g.fill(qrX - 2, qrY - 2, qrX + qrS + 2, qrY + qrS + 2, 0xFFFFFFFF);
        if (qrTexture != null) {
            renderQR(g, qrX, qrY, qrS);
        } else {
            g.drawString(font, "QR...", qrX + 20, qrY + 35, 0x888888);
        }

        // === Status (right of QR) ===
        int rx = l + 100, ry = t + 10;
        boolean connected = ws.isConnected();
        // Status indicator
        g.fill(rx, ry + 2, rx + 6, ry + 8, connected ? 0xFF55FF55 : 0xFFFFFF55);
        g.drawString(font, connected ? "Connected" : "Waiting...",
                rx + 10, ry, connected ? 0x55FF55 : 0xFFFF55);
        ry += 12;
        g.drawString(font, ws.getServerAddress() + ":" + ws.getServerPort(),
                rx, ry, 0xAAAAAA);

        // === Signal bar (bottom) — read from DataSlots synced by server ===
        int sig = menu.signal.get();
        float pct = sig / 15f;
        int strA = (int) (pct * ws.getAppAMax());
        int strB = (int) (pct * ws.getAppBMax());

        int barY = t + 110, barH = 14, barW = w - 20;
        int barX = l + 10;
        g.fill(barX, barY, barX + barW, barY + barH, 0xFF111111);
        int fill = (int) (barW * pct);
        int col = sig >= 11 ? 0xFFFF3333 : sig >= 6 ? 0xFFFFFF33 : 0xFF33FF33;
        if (fill > 0) g.fill(barX, barY, barX + fill, barY + barH, col);
        g.drawCenteredString(font, "Signal: " + sig + "/15 (" + (int)(pct*100) + "%)",
                l + w/2, barY + 2, 0xFFFFFF);

        g.drawCenteredString(font,
                "A: " + strA + "/" + ws.getAppAMax() + "   B: " + strB + "/" + ws.getAppBMax(),
                l + w/2, barY + barH + 4, 0xCCCCCC);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        // Don't draw vanilla title/inventory labels — all text is in renderBg
    }

    private void renderQR(GuiGraphics g, int x, int y, int size) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, qrTexture);
        RenderSystem.enableBlend();
        var tess = Tesselator.getInstance();
        var buf = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buf.addVertex(x, y + size, 0).setUv(0, 1);
        buf.addVertex(x + size, y + size, 0).setUv(1, 1);
        buf.addVertex(x + size, y, 0).setUv(1, 0);
        buf.addVertex(x, y, 0).setUv(0, 0);
        BufferUploader.drawWithShader(buf.buildOrThrow());
        RenderSystem.disableBlend();
    }

    @Override
    public void removed() {
        super.removed();
        if (qrTexture != null) {
            minecraft.getTextureManager().release(qrTexture);
            qrTexture = null;
        }
    }
}
