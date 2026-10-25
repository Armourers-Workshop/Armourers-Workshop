# Vulkan rendering source review

Minecraft 26.2 / NeoForge implementation based directly on official commit `f79d70dd928e0294b8925d0f3ff9142471c38b67`. The author confirmed in-game acceptance.

This directory contains 16 production Java implementations, six regression tests, and an isolated compilation/test configuration. `UPSTREAM_PATHS.tsv` maps the implementations to their upstream `.java/.tj26` files and records SHA256 hashes. Thirteen adapt existing classes; three GUI support classes are new.

The existing upstream version directories and build configuration remain unchanged. This is source for review and integration, not a claim that the complete upstream 26.2 build or other versions have been validated. Keep upstream version selection when integrating; do not blindly replace shared legacy classes.

## Build and test

From the repository root, set `AW_BASE_JAR` to the existing adapted 26.2 AW JAR:

```bash
./gradlew -p vulkan-review -PawBaseJar="$AW_BASE_JAR" check
python3 vulkan-review/tools/check_removal_contract.py vulkan-review/build/classes/java/main
```

Compilation/tests use Java 25. Dependencies and natives are resolved through Gradle; no personal installation path or RT code is required. Override native selection with `-PlwjglNatives=...` if needed. All six checks and the compiled removal contract passed in the isolated project.

To package the compiled overlay:

```bash
python3 vulkan-review/tools/package_port.py "$AW_BASE_JAR" \
  vulkan-review/build/classes/java/main /path/to/output.jar
```

The base JAR is an explicit dependency and is not included. Ordinary `jar` output is disabled because these sources alone are not a complete installable mod. Resource post-processing remains in the packaging tool/base JAR; this branch does not rewrite shared upstream recipes or atlases. Back up the installed JAR and restart Minecraft after replacement.

The painter-order test uses real native phase scheduling with a recording renderer, not GPU pixel readback. Reflection/Unsafe is test-only. Large-library performance has not been separately benchmarked. Source licensing follows the repository's root LICENSE.
