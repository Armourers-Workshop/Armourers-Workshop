package moe.plushie.armourers_workshop.builder.data.palette;

import com.apple.library.uikit.UIColor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.SerializeHelper;

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
        markDirty();
    }

    public Palette addPalette(String paletteName) {
        Palette palette = new Palette(paletteName);
        paletteMap.put(paletteName, palette);
        markDirty();
        return palette;
    }

    public void renamePalette(String oldName, String newName) {
        Palette palette = getPalette(oldName);
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
        ModLog.info("Loading palettes.");
        try {
            paletteMap.clear();
            JsonArray json = SerializeHelper.readJsonFile(paletteFile, StandardCharsets.UTF_8).getAsJsonArray();
            for (int i = 0; i < json.size(); i++) {
                JsonObject jsonPalette = json.get(i).getAsJsonObject();
                if (jsonPalette.has("name") & jsonPalette.has("colours")) {
                    String name = jsonPalette.get("name").getAsString();
                    boolean locked = jsonPalette.get("locked").getAsBoolean();
                    UIColor[] colours = jsonToIntArray(jsonPalette.get("colours").getAsJsonArray());
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

    private JsonArray intToJsonArray(UIColor[] intArray) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < intArray.length; i++) {
            jsonArray.add(colourToHex(intArray[i]));
        }
        return jsonArray;
    }

    private UIColor[] jsonToIntArray(JsonArray jsonArray) {
        UIColor[] intArray = new UIColor[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            String colourHex = jsonArray.get(i).getAsString();
            if (isValidHex(colourHex)) {
                intArray[i] = UIColor.decode(colourHex);
            }
        }
        return intArray;
    }

    private boolean isValidHex(String colorStr) {
        if (colorStr.isEmpty()) {
            return false;
        }
        String hexPatten = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern pattern = Pattern.compile(hexPatten);
        Matcher matcher = pattern.matcher(colorStr);
        return matcher.matches();
    }

    private String colourToHex(UIColor c) {
        if (c == null) {
            return "";
        }
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
