package moe.plushie.armourers_workshop.utils;

import net.minecraft.client.resources.I18n;

public final class TranslateUtils {
    
    public static String translate(String unlocalizedText) {
        String localizedText = I18n.format(unlocalizedText);
        return localizedText.replace("&", "\u00a7");
    }
    
    public static String translate(String unlocalizedText, Object ... args) {
        String localizedText = I18n.format(unlocalizedText, args);
        return localizedText.replace("&", "\u00a7");
    }
}
