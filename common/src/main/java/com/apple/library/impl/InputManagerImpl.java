package com.apple.library.impl;

import com.apple.library.impl.event.InputKeyEvent;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractKeyEvent;
import moe.plushie.armourers_workshop.compat.core.AbstractPlatform;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class InputManagerImpl {

    public static final boolean ON_OSX = AbstractPlatform.ON_OSX;

    public static boolean hasControlDown() {
        if (InputManagerImpl.ON_OSX) {
            return isKeyDown(GLFW.GLFW_KEY_LEFT_SUPER) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SUPER);
        }
        return isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean hasShiftDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean hasAltDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_ALT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
    }

    public static boolean hasSpaceDown() {
        return isKeyDown(GLFW.GLFW_KEY_SPACE);
    }

    public static boolean isCut(InputKeyEvent key) {
        return key.code() == GLFW.GLFW_KEY_X && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isPaste(InputKeyEvent key) {
        return key.code() == GLFW.GLFW_KEY_V && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isCopy(InputKeyEvent key) {
        return key.code() == GLFW.GLFW_KEY_C && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isSelectAll(InputKeyEvent key) {
        return key.code() == GLFW.GLFW_KEY_A && hasControlDown() && !hasShiftDown() && !hasAltDown();
    }

    public static boolean isKeyDown(int key) {
        return AbstractKeyEvent.isKeyDown(key);
    }

    public static boolean hasShortcutDown(InputKeyEvent key) {
        if (InputManagerImpl.ON_OSX) {
            return isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
        }
        return false;
    }

    public static int getShortcutKey(InputKeyEvent key) {
        return switch (key.code()) {
            case GLFW.GLFW_KEY_A -> GLFW.GLFW_KEY_HOME;
            case GLFW.GLFW_KEY_E -> GLFW.GLFW_KEY_END;
            case GLFW.GLFW_KEY_B -> GLFW.GLFW_KEY_LEFT;
            case GLFW.GLFW_KEY_F -> GLFW.GLFW_KEY_RIGHT;
            case GLFW.GLFW_KEY_D -> GLFW.GLFW_KEY_DELETE;
            case GLFW.GLFW_KEY_P -> GLFW.GLFW_KEY_UP;
            case GLFW.GLFW_KEY_N -> GLFW.GLFW_KEY_DOWN;
            default -> key.code();
        };
    }

    public static String getClipboard() {
        return getKeyboardHandler().getClipboard();
    }

    public static void setClipboard(String string) {
        getKeyboardHandler().setClipboard(string);
    }

    private static KeyboardHandler getKeyboardHandler() {
        return Minecraft.getInstance().keyboardHandler;
    }
}
