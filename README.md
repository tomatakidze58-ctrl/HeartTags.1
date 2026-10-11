# HeartTags

A client-side Minecraft **26.2 Fabric** mod that draws pixel hearts above players' heads. It includes full, half, and empty hearts, gold absorption hearts, and a configuration screen accessible from Mod Menu. The heart artwork is original and does not require a resource pack.

## Install

1. Install Fabric Loader **0.19.5 or newer** for Minecraft **26.2**. Use **Java 25**.
2. Extract this ZIP and copy `dist/hearttags-1.0.0+mc26.2.jar` into your Minecraft instance's `mods` folder.
3. Add [Fabric API for 26.2](https://modrinth.com/mod/fabric-api/versions?g=26.2), version **0.161.0+26.2 or newer**.
4. For the configuration menu, install [Mod Menu for 26.2](https://modrinth.com/mod/modmenu/versions?g=26.2). This build was checked against **20.0.3**. Install its required dependencies, including [Text Placeholder API for 26.2](https://modrinth.com/mod/placeholder-api/versions?g=26.2).
5. Launch Minecraft and open **Mods → HeartTags → Configure**.

HeartTags runs on the client; the server does not need this mod. Mod Menu is optional: without it, you can assign the **Open HeartTags settings** key in **Options → Controls → Key Binds → HeartTags**. The key is unbound by default.

## Settings

Changes take effect immediately and save automatically to `config/hearttags.json`. Click a numeric setting to cycle through its presets. The JSON file supports values between the documented limits; edit it while Minecraft is closed.

| Setting | Default | Behavior |
| --- | --- | --- |
| Enabled | On | Show or hide all heart indicators. |
| Range | 48 blocks | Maximum distance from the camera; 8–128 blocks in JSON. |
| Size | 1× | Scale the icons and optional text; 0.5–2×. |
| Head offset | 0.75 blocks | Height above the player's bounding box; 0.15–3 blocks. |
| Own hearts | Off | Show your indicator in third person. |
| Through walls | Off | Draw indicators above other scene geometry. |
| Absorption | On | Append gold hearts for absorption health. |
| Health numbers | Off | Add a health/max-health label and any visible absorption value. |
| Only when hurt | Off | Hide indicators for players at full normal health. |
| Hearts per row | 10 | Wrap hearts into centered rows; 5–20. |
| Heart color | Red | Cycle six colors; JSON accepts any `#RRGGBB` color. |
| Hide with HUD | On | Hide indicators when the HUD is hidden. |

One heart represents two health points. Partial health uses Minecraft-style half-heart rounding. Dead players, spectators, and players invisible to you do not receive an indicator. Ordinary hearts appear before absorption hearts.

Two advanced JSON settings limit rendering: `maxHearts` defaults to 40 (range 10–80), and `maxPlayers` defaults to 64 (range 1–128). The nearest eligible players are rendered first. If the heart limit truncates an indicator, a numerical label appears automatically.

The display uses the health values received by your client. If a server withholds or changes another player's health data, the display reflects those values.

## Build from source

The compiled installable JAR is already included in `dist`. To rebuild, install **JDK 25**, then run from this folder:

```sh
./gradlew build
```

On Windows, run `gradlew.bat build` or double-click `build-windows.bat` after extracting the archive. The result is `build/libs/hearttags-1.0.0+mc26.2.jar`; the `-sources.jar` is for source browsing, not installation. Gradle downloads the pinned build dependencies on the first build.

A GitHub Actions workflow is included in `.github/workflows/main.yml`. It builds with Java 25 and uploads the installable JAR as an artifact.

For a development client, use `./gradlew runClient`. To test the Mod Menu integration in that instance, place the compatible Mod Menu JAR and its dependencies in `run/mods`.

## Verification

Compilation, heart-layout and geometry checks, config persistence, and Fabric bootstrap checks with and without Mod Menu passed. An interactive Minecraft world and the rendered settings screen have **not** been tested in this environment. See `VALIDATION.md` for the checks performed.

MIT license. This is a separate implementation, not a release of the Health Indicators mod.
