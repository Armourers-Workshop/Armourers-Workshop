package moe.plushie.armourers_workshop.client.gui.style;

import java.util.HashMap;

import net.minecraft.util.ResourceLocation;

public class GuiStyle {

    private final HashMap<String, Integer> mapColours = new HashMap<String, Integer>();
    private final HashMap<String, ResourceLocation> mapTextures = new HashMap<String, ResourceLocation>();

    public GuiStyle() {
        setColour("text", 0x333333);
    }
    
    public void setColour(String key, int colour) {
        mapColours.put(key, colour);
    }
 
    public int getColour(String key) {
        return mapColours.getOrDefault(key, 0xFFFFFF);
    }
}
