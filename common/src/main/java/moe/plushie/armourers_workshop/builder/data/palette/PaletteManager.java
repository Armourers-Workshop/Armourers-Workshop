package moe.plushie.armourers_workshop.builder.data.palette;

import com.apple.library.uikit.UIColor;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.SerializeHelper;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class PaletteManager {

    private static final PaletteManager INSTANCE = new PaletteManager();
    private final File paletteFile;
    private final LinkedHashMap<String, Palette> paletteMap = new LinkedHashMap<>();
    private boolean dirty = false;
    private Palette currentPalette = null;

    public PaletteManager() {
        paletteFile = new File(EnvironmentManager.getRootDirectory(), "palettes.json");
        if (paletteFile.exists()) {
            loadPalettes();
        } else {
            createDefaultPalettes();
            savePalettes();
        }
    }

    public static PaletteManager getInstance() {
        return INSTANCE;
    }

    public void createDefaultPalettes() {
        ModLog.debug("Creating default palettes.");
        putPaletteInMap(new Palette("☆ Minecraft", true, ColorUtils.PALETTE_MINECRAFT));
        putPaletteInMap(new Palette("☆ Shades", true, ColorUtils.PALETTE_SHADES));
        putPaletteInMap(new Palette("☆ Warm32", true, ColorUtils.PALETTE_WARM32));
        putPaletteInMap(new Palette("☆ Pastel-64 A", true, ColorUtils.PALETTE_PASTEL_64_A));
        putPaletteInMap(new Palette("☆ Pastel-64 B", true, ColorUtils.PALETTE_PASTEL_64_B));
        putPaletteInMap(new Palette("☆ SoftMilk32", true, ColorUtils.PALETTE_SOFTMILK32));
        putPaletteInMap(new Palette("☆ Endesga 32", true, ColorUtils.PALETTE_ENDESGA_32));
    }

    private void putPaletteInMap(Palette palette) {
        paletteMap.put(palette.getName(), palette);
    }

    public Palette getPalette(String name) {
        return paletteMap.get(name);
    }

    public Collection<Palette> getPalettes() {
        return paletteMap.values();
    }

    public void setCurrentPalette(@Nullable Palette palette) {
        currentPalette = palette;
    }

    @Nullable
    public Palette getCurrentPalette() {
        return currentPalette;
    }

    public void deletePalette(String paletteName) {
        paletteMap.remove(paletteName);
        markDirty();
    }

    public Palette addPalette(String paletteName) {
        var palette = new Palette(paletteName);
        paletteMap.put(paletteName, palette);
        markDirty();
        return palette;
    }

    public void renamePalette(String oldName, String newName) {
        if (oldName.equals(newName)) {
            return;
        }
        var palette = getPalette(oldName);
        palette.setName(newName);
        paletteMap.put(newName, palette);
        paletteMap.remove(oldName);
        markDirty();
    }

    public void save() {
        if (dirty) {
            savePalettes();
            dirty = false;
        }
    }

    public void markDirty() {
        dirty = true;
    }

    private void savePalettes() {
        ModLog.info("Saving palettes.");
        var json = new JsonArray();
        for (var palette : paletteMap.values()) {
            JsonObject jsonPalette = new JsonObject();
            jsonPalette.addProperty("name", palette.getName());
            jsonPalette.addProperty("locked", palette.isLocked());
            jsonPalette.add("colours", intToJsonArray(palette.getColors()));
            json.add(jsonPalette);
        }
        var gson = new GsonBuilder().setPrettyPrinting().create();
        SerializeHelper.writeFile(paletteFile, StandardCharsets.UTF_8, gson.toJson(json));
    }

    private void loadPalettes() {
        ModLog.info("Loading palettes.");
        try {
            paletteMap.clear();
            var json = SerializeHelper.readJsonFile(paletteFile, StandardCharsets.UTF_8).getAsJsonArray();
            for (var i = 0; i < json.size(); i++) {
                var jsonPalette = json.get(i).getAsJsonObject();
                if (jsonPalette.has("name") & jsonPalette.has("colours")) {
                    var name = jsonPalette.get("name").getAsString();
                    var locked = jsonPalette.get("locked").getAsBoolean();
                    var colors = jsonToIntArray(jsonPalette.get("colours").getAsJsonArray());
                    var palette = new Palette(name, locked, colors);
                    paletteMap.put(palette.getName(), palette);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            createDefaultPalettes();
            savePalettes();
        }
    }

    private JsonArray intToJsonArray(UIColor[] intArray) {
        var jsonArray = new JsonArray();
        for (var color : intArray) {
            jsonArray.add(colorToHex(color));
        }
        return jsonArray;
    }

    private UIColor[] jsonToIntArray(JsonArray jsonArray) {
        var intArray = new UIColor[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            var colorHex = jsonArray.get(i).getAsString();
            if (isValidHex(colorHex)) {
                intArray[i] = UIColor.decode(colorHex);
            }
        }
        return intArray;
    }

    private boolean isValidHex(String colorStr) {
        if (colorStr.isEmpty()) {
            return false;
        }
        var hexPatten = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        var pattern = Pattern.compile(hexPatten);
        var matcher = pattern.matcher(colorStr);
        return matcher.matches();
    }

    private String colorToHex(UIColor c) {
        if (c == null) {
            return "";
        }
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
