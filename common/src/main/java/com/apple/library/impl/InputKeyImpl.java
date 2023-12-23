package com.apple.library.impl;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;

public class InputKeyImpl {

    private final InputConstants.Key key;

    public InputKeyImpl(String keyName) {
        this.key = InputConstants.getKey(keyName);
    }

    public boolean test(int i, int j) {
        if (i == InputConstants.UNKNOWN.getValue()) {
            return key.getType() == InputConstants.Type.SCANCODE && key.getValue() == j;
        }
        return key.getType() == InputConstants.Type.KEYSYM && key.getValue() == i;
    }

    public Component getName() {
        return key.getDisplayName();
    }
}
