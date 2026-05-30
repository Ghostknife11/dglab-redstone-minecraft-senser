package com.coyote.coyotemod.util;

import java.util.List;
import java.util.Random;

/**
 * Generates random waveform hex data for DG-Lab pulse commands.
 * Each hex string = 16 chars (8 bytes) = 4 frequency+intensity pairs = 100ms.
 */
public final class WaveformGenerator {
    private static final Random RNG = new Random();

    private WaveformGenerator() {}

    /** Generate a fresh random waveform. */
    public static WaveformDef generate() {
        int len = 15 + RNG.nextInt(25); // 15-39 elements (1.5-3.9 seconds)
        String[] hex = new String[len];
        for (int i = 0; i < len; i++) {
            hex[i] = randomHex();
        }
        return new WaveformDef("random", hex);
    }

    private static String randomHex() {
        StringBuilder sb = new StringBuilder(16);
        for (int j = 0; j < 4; j++) {
            int f = 20 + RNG.nextInt(81);  // frequency: 20-100
            int a = 10 + RNG.nextInt(91);  // intensity: 10-100
            sb.append(String.format("%02X%02X", f, a));
        }
        return sb.toString();
    }

    /**
     * Build a pulse command string for the given channel.
     */
    public static String buildPulseCommand(String channel, String[] hexData) {
        StringBuilder sb = new StringBuilder("pulse-");
        sb.append(channel).append(":[");
        for (int i = 0; i < hexData.length; i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(hexData[i]).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    public record WaveformDef(String name, String[] hexData) {}
}
