# Licenses and sources

Updated 2026-10-23. Original project code is GPL-3.0-only, with the additional
Minecraft / NeoForge linking permission in `LICENSES/`. The prior MIT notice
is preserved as `LICENSES/Legacy-MIT.txt`; this change does not revoke grants
for historical MIT revisions or rewrite dependency licenses.

| Component | Version | License / source |
|---|---|---|
| ZXing core and javase | 3.5.3 | Apache-2.0, [upstream](https://github.com/zxing/zxing/tree/zxing-3.5.3); full upstream license in `LICENSES/ZXing-Apache-2.0.txt`. Included through JarJar. |
| Java-WebSocket | 1.6.0 | MIT, [upstream](https://github.com/TooTallNate/Java-WebSocket/tree/v1.6.0); original notice in `LICENSES/Java-WebSocket-MIT.txt`. Included through JarJar; SLF4J is excluded by the build. |
| Gradle wrapper | 8.11 | Apache-2.0, [upstream](https://github.com/gradle/gradle/tree/v8.11.0). Build tooling, not mod runtime. The Apache license text is retained in `LICENSES/`. |
| Minecraft / NeoForge | 1.21.1 / 21.1.130 | Supplied by the user's game installation. Neither game nor loader is relicensed by this project. |

The original QR generator comment acknowledges the implementation pattern
of Create-DG-Lab. This is recorded as a reference, not presented as evidence
of copying the whole mod. No official DG-Lab SDK or relay-server source is
included as a build dependency here; the project implements its WebSocket
messages using Java-WebSocket.

The author requested GPLv3 for new versions. This is not a claim that using a
WebSocket protocol automatically forces GPLv3; the actual included code and
dependencies determine their own obligations. See the
[GNU GPL FAQ](https://www.gnu.org/licenses/gpl-faq.en.html#MereAggregation).

JAR builds include the root GPL text, these notices, the retained legacy
notice and dependency licenses under `META-INF`. When distributing a new
GPL binary, also provide its matching complete project source and the
corresponding ZXing / Java-WebSocket source versions, not just a license
label on the binary. Existing published MIT binaries remain historical
artifacts and have not been rebuilt or relabeled by this change.
