package moe.plushie.armourers_workshop.client.palette;

import java.awt.Color;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        putPaletteInMap(new Palette("\u2606 Minecraft", true, UtilColour.PALETTE_MINECRAFT));
        putPaletteInMap(new Palette("\u2606 Shades", true, UtilColour.PALETTE_SHADES));
        putPaletteInMap(new Palette("\u2606 Warm32", true, UtilColour.PALETTE_WARM32));
        putPaletteInMap(new Palette("\u2606 Pastel-64 A", true, UtilColour.PALETTE_PASTEL_64_A));
        putPaletteInMap(new Palette("\u2606 Pastel-64 B", true, UtilColour.PALETTE_PASTEL_64_B));
        putPaletteInMap(new Palette("\u2606 SoftMilk32", true, UtilColour.PALETTE_SOFTMILK32));
        putPaletteInMap(new Palette("\u2606 Endesga 32", true, UtilColour.PALETTE_ENDESGA_32));
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
            jsonPalette.addProperty("locked", palette.isLocked());
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
