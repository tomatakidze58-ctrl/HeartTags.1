package dev.hearttags.render;

import java.util.List;
import net.minecraft.gizmos.Gizmo;
import net.minecraft.gizmos.GizmoPrimitives;
import net.minecraft.world.phys.Vec3;

/** Immutable world-space snapshot. Emission never reads the player or the config. */
public record HeartBillboard(Vec3 origin, Vec3 right, Vec3 up, double pixelSize,
                             List<HeartLayout.Heart> hearts, int perRow, int heartColor) implements Gizmo {
    public HeartBillboard { hearts = List.copyOf(hearts); }

    @Override
    public void emit(GizmoPrimitives primitives, float opacity) {
        int rows = (hearts.size() + perRow - 1) / perRow;
        for (int index = 0; index < hearts.size(); index++) {
            HeartLayout.Heart heart = hearts.get(index);
            int row = index / perRow;
            int column = index % perRow;
            int width = Math.min(perRow, hearts.size() - row * perRow);
            double xOffset = -(width * 10 - 1) / 2.0 + column * 10;
            double yOffset = (rows - 1 - row) * 10;
            for (PixelHeart.Rectangle rectangle : PixelHeart.rectangles(heart.fill())) {
                double x0 = (xOffset + rectangle.x()) * pixelSize;
                double y0 = (yOffset + 9 - rectangle.y() - rectangle.height()) * pixelSize;
                double x1 = x0 + rectangle.width() * pixelSize;
                double y1 = y0 + rectangle.height() * pixelSize;
                int color = switch (rectangle.shade()) {
                    case OUTLINE -> 0xFF111016;
                    case EMPTY -> heart.absorption() ? 0xFF4C3A16 : 0xFF402126;
                    case FILL -> heart.absorption() ? 0xFFFFD23F : heartColor;
                    case HIGHLIGHT -> lighten(heart.absorption() ? 0xFFFFD23F : heartColor);
                };
                int alpha = Math.max(0, Math.min(255, Math.round(255 * opacity)));
                color = (color & 0x00FFFFFF) | (alpha << 24);
                primitives.addQuad(point(x0, y0), point(x1, y0), point(x1, y1), point(x0, y1), color);
            }
        }
    }

    private Vec3 point(double x, double y) { return origin.add(right.scale(x)).add(up.scale(y)); }

    private static int lighten(int color) {
        int red = ((color >> 16) & 255), green = ((color >> 8) & 255), blue = color & 255;
        return 0xFF000000 | ((red + (255 - red) / 2) << 16)
                | ((green + (255 - green) / 2) << 8) | (blue + (255 - blue) / 2);
    }
}
