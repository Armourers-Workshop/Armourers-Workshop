package com.apple.library.impl;

import com.apple.library.foundation.NSString;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class FormattedStringImpl extends NSString {

    private final String contents;

    public FormattedStringImpl(String contents) {
        super(FormattedCharSequence.forward(contents, Style.EMPTY));
        this.contents = contents;
    }

    @Override
    public String contents() {
        return contents;
    }
}
