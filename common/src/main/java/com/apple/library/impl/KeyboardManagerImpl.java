package com.apple.library.impl;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeyboardManagerImpl {

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
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_SPACE);
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
