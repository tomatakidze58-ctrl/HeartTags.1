package dev.hearttags.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.hearttags.HeartTagsClient;
import dev.hearttags.ui.SettingsScreen;
import net.minecraft.client.gui.screens.Screen;

public final class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<? extends Screen> getModConfigScreenFactory() {
        return parent -> new SettingsScreen(HeartTagsClient.get(), parent);
    }
}
