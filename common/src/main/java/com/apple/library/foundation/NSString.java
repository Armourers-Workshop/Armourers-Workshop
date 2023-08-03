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

    @Override
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

    @Override
    public FormattedCharSequence chars() {
        if (value != null) {
            return value.getVisualOrderText();
        }
        return sequence;
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
