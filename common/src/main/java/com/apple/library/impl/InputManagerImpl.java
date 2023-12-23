package com.apple.library.impl;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class InputManagerImpl {

    public static boolean hasControlDown() {
        return Screen.hasControlDown();
    }

    public static boolean hasShiftDown() {
        return Screen.hasShiftDown();
    }

    public static boolean hasAltDown() {
        return Screen.hasAltDown();
    }

    public static boolean hasSprintDown() {
        return Minecraft.getInstance().options.keySprint.isDown();
    }

    public static boolean hasSneakDown() {
        return Minecraft.getInstance().options.keyShift.isDown();
    }

    public static boolean hasSpaceDown() {
        return isKeyDown(GLFW.GLFW_KEY_SPACE);
    }

    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }

    public static boolean isCut(int i) {
        return Screen.isCut(i);
    }

    public static boolean isPaste(int i) {
        return Screen.isPaste(i);
    }

    public static boolean isCopy(int i) {
        return Screen.isCopy(i);
    }

    public static boolean isSelectAll(int i) {
        return Screen.isSelectAll(i);
    }

    public static boolean isSpace(int i) {
        return i == GLFW.GLFW_KEY_SPACE;
    }

    public static boolean isEnter(int i) {
        return i == GLFW.GLFW_KEY_ENTER || i == GLFW.GLFW_KEY_KP_ENTER;
    }

    public static boolean hasShortcutDown() {
        if (Minecraft.ON_OSX) {
            return isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
        }
        return false;
    }

    public static int getShortcutKey(int key) {
        switch (key) {
            case GLFW.GLFW_KEY_A:
                return GLFW.GLFW_KEY_HOME;

            case GLFW.GLFW_KEY_E:
                return GLFW.GLFW_KEY_END;

            case GLFW.GLFW_KEY_B:
                return GLFW.GLFW_KEY_LEFT;

            case GLFW.GLFW_KEY_F:
                return GLFW.GLFW_KEY_RIGHT;

            case GLFW.GLFW_KEY_D:
                return GLFW.GLFW_KEY_DELETE;

            case GLFW.GLFW_KEY_P:
                return GLFW.GLFW_KEY_UP;

            case GLFW.GLFW_KEY_N:
                return GLFW.GLFW_KEY_DOWN;

            default:
                return key;
        }
    }

    public static String getClipboard() {
        return keyboardHandler().getClipboard();
    }

    public static void setClipboard(String string) {
        keyboardHandler().setClipboard(string);
    }

    private static KeyboardHandler keyboardHandler() {
        return Minecraft.getInstance().keyboardHandler;
    }
}
