package com.apple.library.foundation;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class NSMutableString extends NSString {

    private final MutableComponent mutableValue;

    public NSMutableString(String value) {
        this(Component.literal(value));
    }

    public NSMutableString(MutableComponent value) {
        super(value);
        this.mutableValue = value;
    }

    public void append(String value) {
        mutableValue.append(value);
    }

    public void append(Component value) {
        mutableValue.append(value);
    }

    public void append(NSString value) {
        mutableValue.append(value.component());
    }
}
