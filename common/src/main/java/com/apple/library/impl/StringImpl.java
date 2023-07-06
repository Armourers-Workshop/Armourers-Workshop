package com.apple.library.impl;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public interface StringImpl {

    Component component();

    FormattedCharSequence chars();

    default CGRect boundingRectWithFont(UIFont font) {
        int width = font.font().width(chars());
        return new CGRect(0, 0, width, font.lineHeight());
    }

    default List<NSString> split(float width, UIFont font) {
        Component contents = component();
        if (contents != null) {
            return font.font().split(contents, (int) width).stream().map(NSString::new).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    default Map<String, ?> attributes(int width, UIFont font) {
        Style style = font.font().getSplitter().componentStyleAtWidth(chars(), width);
        if (style == null) {
            return null;
        }
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("ClickEvent", style.getClickEvent());
        return attributes;
    }
}
