package moe.plushie.armourers_workshop.utils;


import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.slot.ItemOverrideType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentType;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;

public final class TranslateUtils {

    private static final TextFormatter FORMATTER = new TextFormatter();

    public static MutableComponent formatted(String content) {
        return Component.literal(FORMATTER.getFormattedString(content));
    }

    public static MutableComponent title(String key, Object... args) {
        return Component.translatable(FORMATTER, key, args);
    }

    public static MutableComponent subtitle(String key, Object... args) {
        return title(key, args).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
    }

    public static ArrayList<Component> subtitles(String key) {
        ArrayList<Component> results = new ArrayList<>();
        MutableComponent value1 = subtitle(key);
        String value = value1.getString();
        if (key.equals(value)) {
            return results;
        }
        Style style = Style.EMPTY.withColor(ChatFormatting.GRAY);
        for (String line : FORMATTER.getFormattedString(value).split("(\\r?\\n)|(%n)")) {
            results.add(Component.literal(line).setStyle(style));
        }
        return results;
    }

    public static class Name {

        public static MutableComponent of(ItemOverrideType overrideType) {
            return title("itemOverrideType.armourers_workshop." + overrideType.getName());
        }

        public static MutableComponent of(SkinDocumentType documentType) {
            Component lhs = title("documentType.armourers_workshop.category." + documentType.getCategory());
            Component rhs = of(documentType.getSkinType());
            return title("documentType.armourers_workshop.category", lhs, rhs);
        }

        public static MutableComponent of(ISkinType skinType) {
            if (skinType == SkinTypes.UNKNOWN) {
                return title("skinType.armourers_workshop.all");
            }
            String path = skinType.getRegistryName().getPath();
            return title("skinType.armourers_workshop." + path);
        }

        public static MutableComponent of(ISkinPartType skinPartType) {
            return of("skinPartType.armourers_workshop", skinPartType);
        }

        public static MutableComponent of(String prefix, ISkinPartType skinPartType) {
            String path = skinPartType.getRegistryName().getPath();
            String key = prefix + "." + path;
            MutableComponent text = title(key);
            if (!text.getString().equals(key)) {
                return text;
            }
            ModLog.debug("missing translation text for key {}", key);
            return title(prefix + ".part.base");
        }


        public static MutableComponent of(ISkinPaintType paintType) {
            String path = paintType.getRegistryName().getPath();
            return title("paintType.armourers_workshop." + path);
        }
    }
}
