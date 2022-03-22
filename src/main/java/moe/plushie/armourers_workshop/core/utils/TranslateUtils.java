package moe.plushie.armourers_workshop.core.utils;


import joptsimple.internal.Strings;
import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.Optional;

public final class TranslateUtils {


    public static TextComponent literal(String content) {
        return new StringTextComponent(content);
    }

    public static TranslationTextComponent title(String key) {
        return new TranslationTextComponent(key);
    }

    public static TextComponent title(String key, Object... args) {
        return new MergedTextComponent(new TranslationTextComponent(key, args));
    }

    public static ArrayList<ITextComponent> subtitles(String key) {
        ArrayList<ITextComponent> results = new ArrayList<>();
        TextComponent value1 = TranslateUtils.subtitle(key);
        String value = value1.getContents();
        if (key.equals(value)) {
            return results;
        }
        results.add(value1.setStyle(Style.EMPTY.withColor(TextFormatting.GRAY)));
        return results;
    }

    public static TextComponent subtitle(String key, Object... args) {
        MergedTextComponent text = new MergedTextComponent(new TranslationTextComponent(key, args));
        text.setStyle(Style.EMPTY.withColor(TextFormatting.GRAY));
        return text;
    }

    private static class MergedTextComponent extends TextComponent {
        final TranslationTextComponent component;

        MergedTextComponent(TranslationTextComponent component) {
            this.component = component;
        }

        @Override
        public String getContents() {
            ArrayList<String> contents = new ArrayList<>();
            component.visitSelf(value -> {
                contents.add(value);
                return Optional.empty();
            });
            return Strings.join(contents, "");
        }

        @Override
        public MergedTextComponent plainCopy() {
            return new MergedTextComponent(component);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MergedTextComponent that = (MergedTextComponent) o;
            return component.equals(that.component);
        }

        @Override
        public int hashCode() {
            return component.hashCode();
        }
    }
}
