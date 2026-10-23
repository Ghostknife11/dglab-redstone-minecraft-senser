# Coyote Redstone Link

Minecraft NeoForge 1.21.1 mod that links redstone signal strength to DG-Lab Coyote device output via WebSocket.

## Features

- Place a **Gimbal Sensor** block
- Redstone signal (0-15) maps to DG-Lab output strength (0-100% of app max)
- Built-in WebSocket server — phone app connects directly
- QR code display for easy DG-Lab app pairing
- Random waveform generation for realistic sensations
- A/B channel independent strength control

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1+
- Java 21+

## Usage

1. Craft or obtain the Gimbal Sensor block
2. Place it in the world
3. Apply redstone signal (lever, comparator, etc.)
4. Right-click the block to open the GUI
5. Scan QR code with DG-Lab app to connect
6. Redstone signal strength controls output intensity

## Building

```bash
./gradlew build
```

Output: `build/libs/coyotemod-1.0.0.jar`

## License

Copyright (C) 2026 Ghostknife and contributors.

Original project code is licensed under **GPL-3.0-only**, with the additional
[Minecraft / NeoForge linking permission](LICENSES/Minecraft-linking-exception.txt).
The former MIT grant is retained in [LICENSES/Legacy-MIT.txt](LICENSES/Legacy-MIT.txt)
for the historical revisions that were published under it.

This is the author's licensing choice for new revisions. Merely using the
DG-Lab WebSocket protocol does not by itself establish a GPL requirement.
Third-party dependencies keep their own licenses; see
[THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
