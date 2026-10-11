package dev.hearttags.ui;

import dev.hearttags.HeartTagsClient;
import dev.hearttags.config.HeartTagsConfig;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SettingsScreen extends Screen {
    private final HeartTagsClient mod;
    private final Screen parent;
    private int left, top, panelWidth;

    public SettingsScreen(HeartTagsClient mod, Screen parent) {
        super(Component.literal("HeartTags Settings"));
        this.mod = mod;
        this.parent = parent;
    }

    @Override
    protected void init() {
        HeartTagsConfig config = mod.config();
        panelWidth = Math.min(380, width - 24);
        left = (width - panelWidth) / 2;
        top = Math.max(26, (height - 224) / 2);
        button(0, 0, () -> "Enabled: " + onOff(config.enabled), () -> config.enabled = !config.enabled);
        button(1, 0, () -> String.format(Locale.ROOT, "Range: %.0fm", config.range),
                () -> config.range = next(config.range, 16, 32, 48, 64, 96, 128));
        button(0, 1, () -> String.format(Locale.ROOT, "Size: %.2fx", config.scale),
                () -> config.scale = next(config.scale, 0.5, 0.75, 1, 1.25, 1.5, 2));
        button(1, 1, () -> String.format(Locale.ROOT, "Head offset: %.2fm", config.heightOffset),
                () -> config.heightOffset = next(config.heightOffset, 0.35, 0.5, 0.75, 1, 1.25, 1.5, 2));
        button(0, 2, () -> "Own hearts: " + onOff(config.showSelf), () -> config.showSelf = !config.showSelf)
                .setTooltip(Tooltip.create(Component.literal("Shows your own indicator in third person.")));
        button(1, 2, () -> "Through walls: " + onOff(config.throughWalls), () -> config.throughWalls = !config.throughWalls);
        button(0, 3, () -> "Absorption: " + onOff(config.showAbsorption), () -> config.showAbsorption = !config.showAbsorption);
        button(1, 3, () -> "Health numbers: " + onOff(config.showNumbers), () -> config.showNumbers = !config.showNumbers);
        button(0, 4, () -> "Only when hurt: " + onOff(config.onlyDamaged), () -> config.onlyDamaged = !config.onlyDamaged);
        button(1, 4, () -> "Hearts per row: " + config.heartsPerRow,
                () -> config.heartsPerRow = (int) next(config.heartsPerRow, 5, 10, 15, 20));
        button(0, 5, () -> "Heart color: " + config.heartColor, () -> config.heartColor = nextColor(config.heartColor));
        button(1, 5, () -> "Hide with HUD: " + onOff(config.respectHiddenHud),
                () -> config.respectHiddenHud = !config.respectHiddenHud);
        int columnWidth = (panelWidth - 8) / 2;
        addRenderableWidget(Button.builder(Component.literal("Reset defaults"), button -> {
            mod.resetConfig();
            rebuildWidgets();
        }).bounds(left, top + 150, columnWidth, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(left + columnWidth + 8, top + 150, columnWidth, 20).build());
    }

    private Button button(int column, int row, Supplier<String> label, Runnable action) {
        int columnWidth = (panelWidth - 8) / 2;
        return addRenderableWidget(Button.builder(Component.literal(label.get()), button -> {
            action.run();
            mod.config().save();
            button.setMessage(Component.literal(label.get()));
        }).bounds(left + column * (columnWidth + 8), top + row * 24, columnWidth, 20).build());
    }

    private static String onOff(boolean value) { return value ? "ON" : "OFF"; }

    private static double next(double value, double... choices) {
        for (double choice : choices) { if (choice > value + 0.001) { return choice; } }
        return choices[0];
    }

    private static String nextColor(String value) {
        String[] colors = { "#F33044", "#FF76B9", "#59BFFF", "#7FE078", "#CE8CFF", "#FFFFFF" };
        for (int index = 0; index < colors.length; index++) {
            if (colors[index].equalsIgnoreCase(value)) { return colors[(index + 1) % colors.length]; }
        }
        return colors[0];
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        extractTransparentBackground(graphics);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(font, title, width / 2, top - 22, 0xFFF77F97);
        graphics.centeredText(font, "One heart = 2 HP. Gold hearts = absorption.", width / 2, top + 182, 0xFFBAC2CF);
        graphics.centeredText(font, "Changes save automatically.", width / 2, top + 194, 0xFFBAC2CF);
    }

    @Override
    public void onClose() {
        mod.config().save();
        minecraft.gui.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
