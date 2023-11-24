package com.apple.library.impl;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import manifold.ext.rt.api.auto;

@SuppressWarnings("unused")
public interface StringImpl {

    Component component();

    FormattedCharSequence characters();

    default CGRect boundingRectWithFont(UIFont font) {
        float width = font._getTextWidth(characters());
        return new CGRect(0, 0, width, font.lineHeight());
    }

    default List<NSString> split(UIFont font, float maxWidth) {
        Component contents = component();
        if (contents != null) {
            return font._splitLines(contents, maxWidth, false, NSString::new);
        }
        return new ArrayList<>();
    }

    default Map<String, ?> attributes(int width, UIFont font) {
        auto style = font._getStyleByWidth(characters(), width);
        if (style == null) {
            return null;
        }
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("ClickEvent", style.getClickEvent());
        return attributes;
    }
}
