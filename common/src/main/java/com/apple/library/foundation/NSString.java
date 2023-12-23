package com.apple.library.foundation;

import com.apple.library.impl.StringEncoderImpl;
import com.apple.library.impl.StringImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

@SuppressWarnings("unused")
public class NSString implements StringImpl {

    private static final Component EMPTY_COMPONENT = Component.literal("");

    private Component completedValue;

    private final Component value;
    private final FormattedCharSequence characters;

    public NSString(String value) {
        this(Component.literal(value));
    }

    public NSString(Component value) {
        this.value = value;
        this.characters = null;
    }

    public NSString(FormattedCharSequence characters) {
        this.value = null;
        this.characters = characters;
    }

    public static NSString localizedString(String key, Object... args) {
        return StringImpl.localizedString("inventory", key, args);
    }

    public static NSString localizedTableString(String table, String key, Object... args) {
        return StringImpl.localizedString(table, key, args);
    }

    @Override
    public Component component() {
        if (value != null) {
            return value;
        }
        if (completedValue != null) {
            return completedValue;
        }
        if (characters != null) {
            StringEncoderImpl impl = new StringEncoderImpl();
            characters.accept(impl::append);
            completedValue = impl.encode();
            return completedValue;
        }
        return EMPTY_COMPONENT;
    }

    @Override
    public FormattedCharSequence characters() {
        if (value != null) {
            return value.getVisualOrderText();
        }
        return characters;
    }

    public String contents() {
        return component().getString();
    }

    public boolean isEmpty() {
        return contents().isEmpty();
    }

    @Override
    public String toString() {
        return contents();
    }
}
