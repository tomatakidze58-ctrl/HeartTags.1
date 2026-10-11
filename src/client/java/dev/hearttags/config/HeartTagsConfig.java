package dev.hearttags.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

public final class HeartTagsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("hearttags.json");

    public boolean enabled = true;
    public double range = 48;
    public double scale = 1;
    public double heightOffset = 0.75;
    public boolean showSelf = false;
    public boolean throughWalls = false;
    public boolean showAbsorption = true;
    public boolean showNumbers = false;
    public boolean onlyDamaged = false;
    public boolean respectHiddenHud = true;
    public int heartsPerRow = 10;
    public int maxHearts = 40;
    public int maxPlayers = 64;
    public String heartColor = "#F33044";

    public static HeartTagsConfig load() {
        if (!Files.exists(FILE)) {
            HeartTagsConfig defaults = new HeartTagsConfig();
            defaults.save();
            return defaults;
        }
        try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            HeartTagsConfig config = GSON.fromJson(reader, HeartTagsConfig.class);
            if (config == null) { throw new IOException("Empty configuration"); }
            config.validate();
            return config;
        } catch (IOException | RuntimeException error) {
            LoggerFactory.getLogger("HeartTags").warn("Cannot read hearttags.json; using defaults", error);
            return new HeartTagsConfig();
        }
    }

    public void validate() {
        range = clamp(range, 8, 128, 48);
        scale = clamp(scale, 0.5, 2, 1);
        heightOffset = clamp(heightOffset, 0.15, 3, 0.75);
        heartsPerRow = Math.max(5, Math.min(20, heartsPerRow));
        maxHearts = Math.max(10, Math.min(80, maxHearts));
        maxPlayers = Math.max(1, Math.min(128, maxPlayers));
        if (heartColor == null || !heartColor.matches("#[0-9a-fA-F]{6}")) { heartColor = "#F33044"; }
    }

    private static double clamp(double value, double min, double max, double fallback) {
        return Double.isFinite(value) ? Math.max(min, Math.min(max, value)) : fallback;
    }

    public int heartArgb() { return 0xFF000000 | Integer.parseInt(heartColor.substring(1), 16); }

    public void save() {
        validate();
        Path temporary = FILE.resolveSibling("hearttags.json.tmp");
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(temporary, GSON.toJson(this) + "\n", StandardCharsets.UTF_8);
            try {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(temporary, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException error) {
            LoggerFactory.getLogger("HeartTags").warn("Cannot save hearttags.json", error);
        }
    }
}
