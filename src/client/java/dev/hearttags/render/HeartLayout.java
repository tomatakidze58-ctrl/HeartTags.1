package dev.hearttags.render;

import java.util.ArrayList;
import java.util.List;

/** One heart equals two health points; a remaining partial point uses a half heart. */
public final class HeartLayout {
    public enum Fill { EMPTY, HALF, FULL }
    public record Heart(Fill fill, boolean absorption) { }
    public record Layout(List<Heart> hearts, double health, double maximum,
                         double absorption, boolean truncated) { }

    private HeartLayout() { }

    public static Layout create(double health, double maximum, double absorption,
                                boolean includeAbsorption, int limit) {
        maximum = Double.isFinite(maximum) && maximum > 0 ? maximum : 20;
        health = Double.isFinite(health) ? Math.max(0, Math.min(maximum, health)) : 0;
        absorption = includeAbsorption && Double.isFinite(absorption) ? Math.max(0, absorption) : 0;
        double normalSlots = Math.ceil(maximum / 2);
        double totalSlots = normalSlots + Math.ceil(absorption / 2);
        int count = (int) Math.min(Math.max(1, Math.min(80, limit)), totalSlots);
        List<Heart> hearts = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            boolean gold = index >= normalSlots;
            double remaining = gold ? absorption - 2 * (index - normalSlots) : health - 2 * index;
            Fill fill = remaining > 1 ? Fill.FULL : remaining > 0 ? Fill.HALF : Fill.EMPTY;
            hearts.add(new Heart(fill, gold));
        }
        return new Layout(List.copyOf(hearts), health, maximum, absorption, totalSlots > count);
    }
}
