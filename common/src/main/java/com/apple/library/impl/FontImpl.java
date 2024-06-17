package com.apple.library.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public abstract class FontImpl {

    private final Font font;
    private final float scale;
    private final float lineHeight;

    public FontImpl(Object impl, float size) {
        this.font = (Font) impl;
        this.scale = size / 9f;
        this.lineHeight = _i2f(font.lineHeight);
    }

    protected static Font defaultFont() {
        return Minecraft.getInstance().font;
    }

    public float lineHeight() {
        return lineHeight;
    }

    public Font impl() {
        return font;
    }


    public float _getScale() {
        return scale;
    }

    public float _getTextWidth(FormattedCharSequence value) {
        return _i2f(font.width(value));
    }

    public String _getTextByWidth(String text, float width) {
        return _getTextByWidth(text, width, false);
    }

    public String _getTextByWidth(String text, float width, boolean bl) {
        return font.plainSubstrByWidth(text, _f2i(width), bl);
    }

    public Style _getStyleByWidth(FormattedCharSequence value, float width) {
        return font.getSplitter().componentStyleAtWidth(value, _f2i(width));
    }

    public <T> List<T> _splitLines(String value, float maxWidth, boolean bl, SliceTransform<T> transformer) {
        var results = new ArrayList<T>();
        font.getSplitter().splitLines(value, _f2i(maxWidth), Style.EMPTY, bl, (style, bi, ei) -> {
            results.add(transformer.accept(value.substring(bi, ei), bi, ei));
        });
        return results;
    }

    public <T> List<T> _splitLines(Component value, float maxWidth, boolean bl, Function<FormattedCharSequence, T> transformer) {
        var results = new ArrayList<T>();
        for (var seq : font.split(value, _f2i(maxWidth))) {
            results.add(transformer.apply(seq));
        }
        return results;
    }

    private float _i2f(int size) {
        return size * scale;
    }

    private int _f2i(float size) {
        return (int) (size / scale);
    }

    public interface SliceTransform<T> {
        T accept(String substring, int beginIndex, int endIndex);
    }
}
