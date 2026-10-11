package dev.hearttags.render;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/** Original pixel artwork expressed as merged colored rectangles, not a text glyph. */
public final class PixelHeart {
    public enum Shade { OUTLINE, EMPTY, FILL, HIGHLIGHT }
    public record Rectangle(int x, int y, int width, int height, Shade shade) { }
    private static final String[] MASK = {
            ".........", ".##...##.", "#RR#.#RR#", "#HRR#RRR#", "#RRRRRRR#",
            ".#RRRRR#.", "..#RRR#..", "...#R#...", "....#...."
    };
    private static final EnumMap<HeartLayout.Fill, List<Rectangle>> RECTANGLES = new EnumMap<>(HeartLayout.Fill.class);

    static {
        for (HeartLayout.Fill fill : HeartLayout.Fill.values()) { RECTANGLES.put(fill, build(fill)); }
    }

    private PixelHeart() { }
    public static List<Rectangle> rectangles(HeartLayout.Fill fill) { return RECTANGLES.get(fill); }

    private static Shade shade(int x, int y, HeartLayout.Fill fill) {
        char pixel = MASK[y].charAt(x);
        if (pixel == '.') { return null; }
        if (pixel == '#') { return Shade.OUTLINE; }
        boolean filled = fill == HeartLayout.Fill.FULL || (fill == HeartLayout.Fill.HALF && x <= 4);
        return !filled ? Shade.EMPTY : pixel == 'H' ? Shade.HIGHLIGHT : Shade.FILL;
    }

    private static List<Rectangle> build(HeartLayout.Fill fill) {
        List<Rectangle> rectangles = new ArrayList<>();
        for (int y = 0; y < 9; y++) {
            int x = 0;
            while (x < 9) {
                Shade shade = shade(x, y, fill);
                if (shade == null) { x++; continue; }
                int start = x++;
                while (x < 9 && shade(x, y, fill) == shade) { x++; }
                int width = x - start;
                int previous = -1;
                for (int index = 0; index < rectangles.size(); index++) {
                    Rectangle rectangle = rectangles.get(index);
                    if (rectangle.x == start && rectangle.width == width && rectangle.shade == shade
                            && rectangle.y + rectangle.height == y) { previous = index; break; }
                }
                if (previous >= 0) {
                    Rectangle rectangle = rectangles.get(previous);
                    rectangles.set(previous, new Rectangle(start, rectangle.y, width, rectangle.height + 1, shade));
                } else {
                    rectangles.add(new Rectangle(start, y, width, 1, shade));
                }
            }
        }
        return List.copyOf(rectangles);
    }
}
