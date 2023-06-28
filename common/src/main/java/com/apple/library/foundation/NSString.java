package com.apple.library.foundation;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.StringEncoderImpl;
import com.apple.library.uikit.UIFont;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NSString {

    private static final Component EMPTY_COMPONENT = Component.literal("");

    private Component completedValue;

    private final Component value;
    private final FormattedCharSequence sequence;

    public NSString(String value) {
        this(Component.literal(value));
    }

    public NSString(Component value) {
        this.value = value;
        this.sequence = null;
    }

    public NSString(FormattedCharSequence sequence) {
        this.value = null;
        this.sequence = sequence;
    }

    @Environment(EnvType.CLIENT)
    public CGRect boundingRectWithFont(UIFont font) {
        int width = font.font().width(chars());
        return new CGRect(0, 0, width, font.lineHeight());
    }

    @Environment(EnvType.CLIENT)
    public List<NSString> split(int width, UIFont font) {
        Component contents = component();
        if (contents != null) {
            return font.font().split(contents, width).stream().map(NSString::new).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Environment(EnvType.CLIENT)
    public Map<String, ?> attributes(int width, UIFont font) {
        Style style = font.font().getSplitter().componentStyleAtWidth(chars(), width);
        if (style == null) {
            return null;
        }
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("ClickEvent", style.getClickEvent());
        return attributes;
    }

    public Component component() {
        if (value != null) {
            return value;
        }
        if (completedValue != null) {
            return completedValue;
        }
        if (sequence != null) {
            StringEncoderImpl impl = new StringEncoderImpl();
            sequence.accept(impl::append);
            completedValue = impl.encode();
            return completedValue;
        }
        return EMPTY_COMPONENT;
    }

    public String contents() {
        return component().getString();
    }

    public FormattedCharSequence chars() {
        if (value != null) {
            return value.getVisualOrderText();
        }
        return sequence;
    }

    @Override
    public String toString() {
        return contents();
    }
}
