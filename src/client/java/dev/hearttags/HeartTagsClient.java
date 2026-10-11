package dev.hearttags;

import com.mojang.blaze3d.platform.InputConstants;
import dev.hearttags.config.HeartTagsConfig;
import dev.hearttags.render.PlayerHearts;
import dev.hearttags.ui.SettingsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class HeartTagsClient implements ClientModInitializer {
    private static HeartTagsClient instance;
    private HeartTagsConfig config;

    public static HeartTagsClient get() { return instance; }
    public HeartTagsConfig config() { return config; }

    @Override
    public void onInitializeClient() {
        instance = this;
        config = HeartTagsConfig.load();
        LevelExtractionEvents.END_EXTRACTION.register(new PlayerHearts(this)::extract);
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("hearttags", "controls"));
        // Unbound by default so it does not conflict with other installed mods.
        KeyMapping settings = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.hearttags.settings", InputConstants.Type.KEYSYM, -1, category));
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            boolean clicked = false;
            while (settings.consumeClick()) { clicked = true; }
            if (clicked && client.gui.screen() == null) {
                client.gui.setScreen(new SettingsScreen(this, null));
            }
        });
    }

    public void resetConfig() {
        config = new HeartTagsConfig();
        config.save();
    }
}
