package moe.plushie.armourers_workshop.client.palette;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SerializeHelper;
import moe.plushie.armourers_workshop.utils.UtilColour;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PaletteManager {

    private static final String PALETTE_FILE_NAME = "palettes.json";

    private final File paletteFile;
    private final LinkedHashMap<String, Palette> paletteMap = new LinkedHashMap<String, Palette>();
    private boolean dirty = false;

    public PaletteManager(File modDirectory) {
        paletteFile = new File(modDirectory, PALETTE_FILE_NAME);
        if (paletteFile.exists()) {
            loadPalettes();
        } else {
            createDefaultPalettes();
            savePalettes();
        }
    }

    public void createDefaultPalettes() {
        ModLogger.log("Creating default palettes.");
        putPaletteInMap(new Palette("Minecraft", UtilColour.PALETTE_MINECRAFT));
        putPaletteInMap(new Palette("Shades", UtilColour.PALETTE_SHADES));
        putPaletteInMap(new Palette("Warm32", UtilColour.PALETTE_WARM32));
        putPaletteInMap(new Palette("Pastel-64 A", UtilColour.PALETTE_PASTEL_64_A));
        putPaletteInMap(new Palette("Pastel-64 B", UtilColour.PALETTE_PASTEL_64_B));
    }

    private void putPaletteInMap(Palette palette) {
        paletteMap.put(palette.getName(), palette);
    }

    public String getFirstPaletteName() {
        Palette[] palettes = getPalettes();
        if (palettes.length > 0) {
            return palettes[0].getName();
        }
        return "";
    }

    public Palette getPalette(String name) {
        return paletteMap.get(name);
    }

    public Palette[] getPalettes() {
        return paletteMap.values().toArray(new Palette[paletteMap.size()]);
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
        ModLogger.log("Saving palettes.");
        JsonArray json = new JsonArray();
        for (Palette palette : paletteMap.values()) {
            JsonObject jsonPalette = new JsonObject();
            jsonPalette.addProperty("name", palette.getName());
            jsonPalette.add("colours", intToJsonArray(palette.getColours()));
            json.add(jsonPalette);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SerializeHelper.writeFile(paletteFile, StandardCharsets.UTF_8, gson.toJson(json));
    }

    private void loadPalettes() {
        ModLogger.log("Loading palettes.");
        try {
            paletteMap.clear();
            JsonArray json = SerializeHelper.readJsonFile(paletteFile, StandardCharsets.UTF_8).getAsJsonArray();
            for (int i = 0; i < json.size(); i++) {
                JsonObject jsonPalette = json.get(i).getAsJsonObject();
                if (jsonPalette.has("name") & jsonPalette.has("colours")) {
                    String name = jsonPalette.get("name").getAsString();
                    int[] colours = jsonToIntArray(jsonPalette.get("colours").getAsJsonArray());
                    Palette palette = new Palette(name, colours);
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
            jsonArray.add(intArray[i]);
        }
        return jsonArray;
    }

    private int[] jsonToIntArray(JsonArray jsonArray) {
        int[] intArray = new int[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            intArray[i] = jsonArray.get(i).getAsInt();
        }
        return intArray;
    }
}
