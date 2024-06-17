package com.apple.library.impl;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface StringImpl {

    Component component();

    FormattedCharSequence characters();

    default CGRect boundingRectWithFont(UIFont font) {
        var width = font._getTextWidth(characters());
        return new CGRect(0, 0, width, font.lineHeight());
    }

    default List<NSString> split(UIFont font, float maxWidth) {
        var contents = component();
        if (contents != null) {
            return font._splitLines(contents, maxWidth, false, NSString::new);
        }
        return new ArrayList<>();
    }

    default Map<String, ?> attributes(int width, UIFont font) {
        var style = font._getStyleByWidth(characters(), width);
        if (style == null) {
            return null;
        }
        var attributes = new HashMap<String, Object>();
        attributes.put("ClickEvent", style.getClickEvent());
        return attributes;
    }

    static NSString join(Iterable<? extends NSString> strings, String separator) {
        var index = 0;
        var result = new NSMutableString("");
        for (var value : strings) {
            if (index++ > 0) {
                result.append(separator);
            }
            result.append(value);
        }
        return result;
    }

    static NSString localizedString(String table, String key, Object... args) {
        var fullKey = String.format("%s.%s.%s", table, ModConstants.MOD_ID, key);
        for (var i = 0; i < args.length; ++i) {
            if (args[i] instanceof NSString text) {
                args[i] = text.component();
            }
        }
        return new NSString(TranslateUtils.title(fullKey, args));
    }
}
