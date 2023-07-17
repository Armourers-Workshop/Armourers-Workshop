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

    FormattedCharSequence chars();

    default CGRect boundingRectWithFont(UIFont font) {
        int width = font.impl().width(chars());
        return new CGRect(0, 0, width, font.lineHeight());
    }

    default List<NSString> split(float width, UIFont font) {
        Component contents = component();
        if (contents != null) {
            return ObjectUtilsImpl.map(font.impl().split(contents, (int) width), NSString::new);
        }
        return new ArrayList<>();
    }

    default Map<String, ?> attributes(int width, UIFont font) {
        auto style = font.impl().getSplitter().componentStyleAtWidth(chars(), width);
        if (style == null) {
            return null;
        }
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("ClickEvent", style.getClickEvent());
        return attributes;
    }
}
