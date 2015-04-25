package riskyken.armourersWorkshop.utils;

import net.minecraft.util.StatCollector;

public final class TranslateUtils {
    
    public static String translate(String unlocalizedText) {
        String localizedText = StatCollector.translateToLocal(unlocalizedText);
        return localizedText.replace("&", "\u00a7");
    }
    
    public static String translate(String unlocalizedText, Object ... args) {
        String localizedText = StatCollector.translateToLocalFormatted(unlocalizedText, args);
        return localizedText.replace("&", "\u00a7");
    }
}
