package moe.plushie.armourers_workshop.core.utils;


import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Optional;

public final class TranslateUtils {


    public static TextComponent literal(String content) {
        return new StringTextComponent(getFormattedString(content));
    }

    public static TranslationTextComponent title(String key) {
        return new ColorFixedTranslationTextComponent(key);
    }

    public static TextComponent title(String key, Object... args) {
        return new ColorFixedTranslationTextComponent(key, args);
    }

    public static ArrayList<ITextComponent> subtitles(String key) {
        ArrayList<ITextComponent> results = new ArrayList<>();
        TextComponent value1 = TranslateUtils.subtitle(key);
        String value = value1.getString();
        if (key.equals(value)) {
            return results;
        }
        Style style = Style.EMPTY.withColor(TextFormatting.GRAY);
        for (String line : getFormattedString(value).split("[\n]|%n")) {
            results.add(new StringTextComponent(line).setStyle(style));
        }
        return results;
    }

    public static TextComponent subtitle(String key, Object... args) {
        TranslationTextComponent text = new ColorFixedTranslationTextComponent(key, args);
        text.setStyle(Style.EMPTY.withColor(TextFormatting.GRAY));
        return text;
    }

    private static class ColorFixedTranslationTextComponent extends TranslationTextComponent {

        public ColorFixedTranslationTextComponent(String p_i45160_1_, Object... p_i45160_2_) {
            super(p_i45160_1_, p_i45160_2_);
        }

        @SuppressWarnings("NullableProblems")
        @OnlyIn(Dist.CLIENT)
        @Override
        public <T> Optional<T> visitSelf(ITextProperties.IStyledTextAcceptor<T> acceptor, Style initStyle) {
            String[] lastStyle = {""};
            return super.visitSelf((style1, value) -> {
                String embeddedStyle = lastStyle[0];
                lastStyle[0] = embeddedStyle + getEmbeddedStyle(value);
                return acceptor.accept(style1, embeddedStyle + getFormattedString(value));
            }, initStyle);
        }

    }

    public static String getEmbeddedStyle(String value) {
        int i = value.length();
        StringBuilder results = new StringBuilder();
        for(int j = 0; j < i; ++j) {
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

    public static String getFormattedString(String value) {
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
