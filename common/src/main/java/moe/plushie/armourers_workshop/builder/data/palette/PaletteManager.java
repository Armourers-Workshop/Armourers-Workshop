package moe.plushie.armourers_workshop.builder.data.palette;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        paletteMap.put(palette.name(), palette);
    }

    public Palette paletteByName(String name) {
        return paletteMap.get(name);
    }

    public Collection<Palette> palettes() {
        return paletteMap.values();
    }

    public void setCurrentPalette(@Nullable Palette palette) {
        currentPalette = palette;
    }

    @Nullable
    public Palette currentPalette() {
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
        var palette = paletteByName(oldName);
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
            jsonPalette.addProperty("name", palette.name());
            jsonPalette.addProperty("locked", palette.isLocked());
            jsonPalette.add("colours", intToJsonArray(palette.colors()));
            json.add(jsonPalette);
        }
        try {
            FileUtils.forceMkdirParent(paletteFile);
            var outputStream = new FileOutputStream(paletteFile, false);
            var text = new GsonBuilder().setPrettyPrinting().create().toJson(json);
            var data = text.getBytes(StandardCharsets.UTF_8);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPalettes() {
        ModLog.info("Loading palettes.");
        try {
            paletteMap.clear();
            var text = StreamUtils.readFileToString(paletteFile, StandardCharsets.UTF_8);
            var json = new JsonParser().parse(text).getAsJsonArray();
            for (var i = 0; i < json.size(); i++) {
                var jsonPalette = json.get(i).getAsJsonObject();
                if (jsonPalette.has("name") & jsonPalette.has("colours")) {
                    var name = jsonPalette.get("name").getAsString();
                    var locked = jsonPalette.get("locked").getAsBoolean();
                    var colors = jsonToIntArray(jsonPalette.get("colours").getAsJsonArray());
                    var palette = new Palette(name, locked, colors);
                    paletteMap.put(palette.name(), palette);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            createDefaultPalettes();
            savePalettes();
        }
    }

    private JsonArray intToJsonArray(int[] intArray) {
        var jsonArray = new JsonArray();
        for (var color : intArray) {
            jsonArray.add(colorToHex(color));
        }
        return jsonArray;
    }

    private int[] jsonToIntArray(JsonArray jsonArray) {
        var intArray = new int[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            var colorHex = jsonArray.get(i).getAsString();
            if (isValidHex(colorHex)) {
                intArray[i] = Integer.decode(colorHex);
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

    private String colorToHex(int c) {
        if (c == 0) {
            return "";
        }
        return String.format("#%06x", c);
    }
}
