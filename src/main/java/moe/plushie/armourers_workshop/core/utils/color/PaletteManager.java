package moe.plushie.armourers_workshop.core.utils.color;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.utils.AWLog;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.SerializeHelper;

import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaletteManager {

    private static final PaletteManager INSTANCE = new PaletteManager();
    private final File paletteFile;
    private final LinkedHashMap<String, Palette> paletteMap = new LinkedHashMap<>();
    private boolean dirty = false;

    public PaletteManager() {
        paletteFile = new File(AWCore.getRootDirectory(), "palettes.json");
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
        AWLog.debug("Creating default palettes.");
        putPaletteInMap(new Palette("\u2606 Minecraft", true, ColorUtils.PALETTE_MINECRAFT));
        putPaletteInMap(new Palette("\u2606 Shades", true, ColorUtils.PALETTE_SHADES));
        putPaletteInMap(new Palette("\u2606 Warm32", true, ColorUtils.PALETTE_WARM32));
        putPaletteInMap(new Palette("\u2606 Pastel-64 A", true, ColorUtils.PALETTE_PASTEL_64_A));
        putPaletteInMap(new Palette("\u2606 Pastel-64 B", true, ColorUtils.PALETTE_PASTEL_64_B));
        putPaletteInMap(new Palette("\u2606 SoftMilk32", true, ColorUtils.PALETTE_SOFTMILK32));
        putPaletteInMap(new Palette("\u2606 Endesga 32", true, ColorUtils.PALETTE_ENDESGA_32));
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

    public void deletePalette(String paletteName) {
        paletteMap.remove(paletteName);
    }

    public void addPalette(String paletteName) {
        Palette palette = new Palette(paletteName);
        paletteMap.put(paletteName, palette);
    }

    public void renamePalette(String oldName, String newName) {
        Palette palette = getPalette(oldName);
        palette.setName(newName);
        paletteMap.put(newName, palette);
        paletteMap.remove(oldName);
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
        AWLog.info("Saving palettes.");
        JsonArray json = new JsonArray();
        for (Palette palette : paletteMap.values()) {
            JsonObject jsonPalette = new JsonObject();
            jsonPalette.addProperty("name", palette.getName());
            jsonPalette.addProperty("locked", palette.isLocked());
            jsonPalette.add("colours", intToJsonArray(palette.getColors()));
            json.add(jsonPalette);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SerializeHelper.writeFile(paletteFile, StandardCharsets.UTF_8, gson.toJson(json));
    }

    private void loadPalettes() {
        AWLog.info("Loading palettes.");
        try {
            paletteMap.clear();
            JsonArray json = SerializeHelper.readJsonFile(paletteFile, StandardCharsets.UTF_8).getAsJsonArray();
            for (int i = 0; i < json.size(); i++) {
                JsonObject jsonPalette = json.get(i).getAsJsonObject();
                if (jsonPalette.has("name") & jsonPalette.has("colours")) {
                    String name = jsonPalette.get("name").getAsString();
                    boolean locked = jsonPalette.get("locked").getAsBoolean();
                    int[] colours = jsonToIntArray(jsonPalette.get("colours").getAsJsonArray());
                    Palette palette = new Palette(name, locked, colours);
                    paletteMap.put(palette.getName(), palette);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            createDefaultPalettes();
            savePalettes();
        }
    }

    private JsonArray intToJsonArray(int[] intArray) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < intArray.length; i++) {
            jsonArray.add(colourToHex(intArray[i]));
        }
        return jsonArray;
    }

    private int[] jsonToIntArray(JsonArray jsonArray) {
        int[] intArray = new int[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            String colourHex = jsonArray.get(i).getAsString();
            intArray[i] = hexToColour(colourHex);
        }
        return intArray;
    }

    private boolean isValidHex(String colorStr) {
        String hexPatten = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern pattern = Pattern.compile(hexPatten);
        Matcher matcher = pattern.matcher(colorStr);
        return matcher.matches();
    }

    private String colourToHex(int colour) {
        return colourToHex(new Color(colour, false));
    }

    private String colourToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private int hexToColour(String hex) {
        if (isValidHex(hex)) {
            return Color.decode(hex).getRGB();
        } else {
            return 0xFF000000;
        }
    }
}
