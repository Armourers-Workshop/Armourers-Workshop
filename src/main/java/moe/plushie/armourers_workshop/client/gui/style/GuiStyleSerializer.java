package moe.plushie.armourers_workshop.client.gui.style;

import java.awt.Color;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class GuiStyleSerializer {
    
    private static final String PROP_STYLE = "style";
    private static final String PROP_COLOUR = "color";
    
    public static JsonElement serializeJson(GuiStyle guiStyle, boolean compact) {
        return null;
    }
    
    public static GuiStyle deserializeJson(JsonElement jsonElement) {
        GuiStyle guiStyle = new GuiStyle();
        try {
            JsonObject json = jsonElement.getAsJsonObject();
            if (json.has(PROP_STYLE)) {
                JsonObject jsonStyle = json.get(PROP_STYLE).getAsJsonObject();
                if (jsonStyle.has(PROP_COLOUR)) {
                    JsonArray jsonColour = jsonStyle.get(PROP_COLOUR).getAsJsonArray();
                    for (JsonElement element : jsonColour) {
                        String[] split = element.getAsString().split("=");
                        guiStyle.setColour(split[0], Color.decode(split[1]).getRGB());
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return guiStyle;
    }
}
