# Validation

Checked on October 11, 2026 against Minecraft 26.2, Java 25.0.2, Fabric Loader 0.19.5, Fabric API 0.161.0+26.2, and Mod Menu 20.0.3.

## Build

`gradle build` completed successfully with Gradle 9.5.1 and Fabric Loom 1.17.21. The resulting mod targets Java 25. Mod Menu is a compile-only dependency; its implementation and Minecraft classes are not bundled in the mod JAR.

This workspace blocks Loom's optional Unix-domain-socket support probe. The cached Loom plugin used for validation was adjusted to skip that probe. No change to the project's source, Gradle wrapper, or Minecraft runtime was needed for that environment restriction.

## Automated checks

A separate Fabric pre-launch checker exercised the actual compiled mod classes using the official Minecraft runtime and libraries:

- Full, half, and empty health states, including fractional health and zero health.
- Absorption counts, disabled absorption, invalid numeric inputs, and bounded output for unusually large values.
- Immutable heart layouts.
- Pixel masks with consistent silhouettes, non-overlapping rectangles, and half fills confined to the left side.
- Billboard row centering, finite non-degenerate quads, rotated/scaled geometry, absorption colors, and opacity.
- Configuration bounds, invalid-color recovery, and JSON save/reload with changed settings.
- Client/render/screen class linkage in Fabric.
- Bootstrap without Mod Menu installed.
- Bootstrap with Mod Menu and Text Placeholder API installed, including discovery of HeartTags' Mod Menu entrypoint and its non-null configuration-screen factory.

The checks completed with these markers:

```text
HEARTTAGS_CHECK_PASSED: layouts, pixel masks, billboard geometry, config persistence, class linking; Mod Menu=false
HEARTTAGS_MODMENU_FACTORY_PASSED
HEARTTAGS_CHECK_PASSED: layouts, pixel masks, billboard geometry, config persistence, class linking; Mod Menu=true
```

## Remaining manual checks

The bootstrap checks exit before opening a game window. They do not establish in-world rendering or interactive UI behavior. In a 26.2 Fabric client, verify:

1. Another player's damage produces full, half, and empty hearts above the head.
2. Absorption adds gold hearts, and optional health numbers remain above the icons.
3. Indicators face the camera, interpolate movement, wrap into rows, and obey the chosen range.
4. Mod Menu opens the configuration screen; controls update and persist after restarting.
5. The own-player option works in third person, and the hidden-HUD option follows the HUD state.
6. Wall occlusion follows the Through walls setting.
