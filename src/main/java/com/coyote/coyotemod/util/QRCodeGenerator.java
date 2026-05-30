package com.coyote.coyotemod.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates a QR code BufferedImage for in-GUI rendering.
 * Pattern matches Create-DG-Lab's QRCodeGenerator.
 */
public final class QRCodeGenerator {
    private static final int QR_SIZE = 256;

    private QRCodeGenerator() {}

    public static BufferedImage generateQRCode(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);

            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);
            int w = matrix.getWidth(), h = matrix.getHeight();
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE); g.fillRect(0, 0, w, h);
            g.setColor(Color.BLACK);
            for (int x = 0; x < w; x++)
                for (int y = 0; y < h; y++)
                    if (matrix.get(x, y)) g.fillRect(x, y, 1, 1);
            g.dispose();
            return image;
        } catch (WriterException e) {
            return null;
        }
    }
}
