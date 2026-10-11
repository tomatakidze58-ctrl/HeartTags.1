package dev.hearttags.render;

import dev.hearttags.HeartTagsClient;
import dev.hearttags.config.HeartTagsConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.gizmos.GizmoProperties;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class PlayerHearts {
    private final HeartTagsClient mod;
    public PlayerHearts(HeartTagsClient mod) { this.mod = mod; }

    public void extract(LevelExtractionContext context) {
        Minecraft client = Minecraft.getInstance();
        HeartTagsConfig config = mod.config();
        if (!config.enabled || client.player == null || context.level() == null
                || (config.respectHiddenHud && client.gui.hud.isHidden())) { return; }
        Vec3 camera = context.camera().position();
        double rangeSquared = config.range * config.range;
        List<Player> players = new ArrayList<>();
        for (Player player : context.level().players()) {
            if (!player.isAlive() || player.isRemoved() || player.isSpectator()
                    || player.isInvisibleTo(client.player) || player.distanceToSqr(camera) > rangeSquared) { continue; }
            if (player == client.player && (!config.showSelf || client.options.getCameraType().isFirstPerson())) { continue; }
            if (config.onlyDamaged && player.getHealth() >= player.getMaxHealth() - 0.001F) { continue; }
            players.add(player);
        }
        players.sort(Comparator.comparingDouble(player -> player.distanceToSqr(camera)));
        Vector3f r = new Vector3f(1, 0, 0).rotate(context.camera().rotation());
        Vector3f u = new Vector3f(0, 1, 0).rotate(context.camera().rotation());
        Vec3 right = new Vec3(r.x, r.y, r.z), up = new Vec3(u.x, u.y, u.z);
        double pixelSize = 0.015 * config.scale;
        try (Gizmos.TemporaryCollection ignored = client.levelRenderer.collectPerFrameRenderThreadGizmos()) {
            for (int index = 0; index < Math.min(players.size(), config.maxPlayers); index++) {
                Player player = players.get(index);
                float delta = context.level().tickRateManager().isEntityFrozen(player)
                        ? 1 : context.deltaTracker().getGameTimeDeltaPartialTick(true);
                Vec3 origin = player.getPosition(delta).add(0, player.getBbHeight() + config.heightOffset, 0);
                HeartLayout.Layout layout = HeartLayout.create(player.getHealth(), player.getMaxHealth(),
                        player.getAbsorptionAmount(), config.showAbsorption, config.maxHearts);
                depth(Gizmos.addGizmo(new HeartBillboard(origin, right, up, pixelSize,
                        layout.hearts(), config.heartsPerRow, config.heartArgb())), config);
                if (config.showNumbers || layout.truncated()) {
                    String numbers = String.format(Locale.ROOT, "%.1f / %.1f HP", layout.health(), layout.maximum());
                    if (layout.absorption() > 0) { numbers += String.format(Locale.ROOT, " +%.1f", layout.absorption()); }
                    if (layout.truncated()) { numbers += " ..."; }
                    int rows = (layout.hearts().size() + config.heartsPerRow - 1) / config.heartsPerRow;
                    float textScale = (float) (0.25 * config.scale);
                    // Billboard text extends downward from its anchor, at scale / 16 blocks per pixel.
                    double textHeight = client.font.lineHeight * textScale / 16;
                    Vec3 position = origin.add(up.scale((rows * 10 + 1) * pixelSize + textHeight));
                    depth(Gizmos.billboardText(numbers, position,
                            TextGizmo.Style.whiteAndCentered().withScale(textScale)), config);
                }
            }
        }
    }

    private static void depth(GizmoProperties properties, HeartTagsConfig config) {
        if (config.throughWalls) { properties.setAlwaysOnTop(); }
    }
}
