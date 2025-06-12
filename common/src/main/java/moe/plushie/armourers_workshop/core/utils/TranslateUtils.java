package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.data.slot.ItemOverrideType;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
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
        var results = new ArrayList<Component>();
        var value1 = subtitle(key);
        var value = value1.getString();
        if (key.equals(value)) {
            return results;
        }
        var style = Style.EMPTY.withColor(ChatFormatting.GRAY);
        for (var line : FORMATTER.getFormattedString(value).split("(\\r?\\n)|(%n)")) {
            results.add(Component.literal(line).setStyle(style));
        }
        return results;
    }

    public static class Name {

        public static MutableComponent of(ItemOverrideType overrideType) {
            return title("itemOverrideType.armourers_workshop." + overrideType.serializedName());
        }

        public static MutableComponent of(SkinDocumentType documentType) {
            var lhs = title("documentType.armourers_workshop.category." + documentType.category());
            var rhs = of(documentType.skinType());
            return title("documentType.armourers_workshop.category", lhs, rhs);
        }

        public static MutableComponent of(SkinType skinType) {
            if (skinType == SkinTypes.UNKNOWN) {
                return title("skinType.armourers_workshop.all");
            }
            var path = skinType.registryName().path();
            return title("skinType.armourers_workshop." + path);
        }

        public static MutableComponent of(SkinPartType skinPartType) {
            return of("skinPartType.armourers_workshop", skinPartType);
        }

        public static MutableComponent of(String prefix, SkinPartType skinPartType) {
            var path = skinPartType.registryName().path();
            var key = prefix + "." + path;
            var text = title(key);
            if (!text.getString().equals(key)) {
                return text;
            }
            ModLog.debug("missing translation text for key {}", key);
            return title(prefix + ".part.base");
        }


        public static MutableComponent of(SkinPaintType paintType) {
            var path = paintType.registryName().path();
            return title("paintType.armourers_workshop." + path);
        }
    }

    public static class TextFormatter {

        public String getEmbeddedStyle(String value) {
            int i = value.length();
            StringBuilder results = new StringBuilder();
            for (int j = 0; j < i; ++j) {
                char c0 = value.charAt(j);
                if (c0 == 167) {
                    if (j + 1 >= i) {
                        break;
                    }
                    char c1 = value.charAt(j + 1);
                    results.append(c0);
                    results.append(c1);
                    ++j;
                }
            }
            return results.toString();
        }

        public String getFormattedString(String value) {
            // The following color codes can be added to the start of text to colur it.
            // &0 Black
            // &1 Dark Blue
            // &2 Dark Green
            // &3 Dark Aqua
            // &4 Dark Red
            // &5 Dark Purple
            // &6 Gold
            // &7 Gray
            // &8 Dark Gray
            // &9 Blue
            // &a Green
            // &b Aqua
            // &c Red
            // &d Light Purple
            // &e Yellow
            // &f White
            //
            // A new line can be inserted with %n. Please add/remove new lines to fit the localisations you are writing.
            //
            // The text %s will be replace with text. Example: "Author: %s" could become "Author: RiskyKen".
            // The text %d will be replace with a number. Example: "Radius: %d*%d*%d" could become "Radius: 3*3*3"
            value = value.replace("\n", System.lineSeparator());
            value = value.replace("%n", System.lineSeparator());
            return value;
        }
    }
}
