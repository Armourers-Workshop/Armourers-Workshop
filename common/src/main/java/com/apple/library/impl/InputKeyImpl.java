package com.apple.library.impl;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

@SuppressWarnings("unused")
public class InputKeyImpl {

    public static final HashMap<String, InputKeyImpl> NAMED_KEYS = new HashMap<>();

    public static final InputKeyImpl KEY_0 = register("key.keyboard.0", GLFW.GLFW_KEY_0);
    public static final InputKeyImpl KEY_1 = register("key.keyboard.1", GLFW.GLFW_KEY_1);
    public static final InputKeyImpl KEY_2 = register("key.keyboard.2", GLFW.GLFW_KEY_2);
    public static final InputKeyImpl KEY_3 = register("key.keyboard.3", GLFW.GLFW_KEY_3);
    public static final InputKeyImpl KEY_4 = register("key.keyboard.4", GLFW.GLFW_KEY_4);
    public static final InputKeyImpl KEY_5 = register("key.keyboard.5", GLFW.GLFW_KEY_5);
    public static final InputKeyImpl KEY_6 = register("key.keyboard.6", GLFW.GLFW_KEY_6);
    public static final InputKeyImpl KEY_7 = register("key.keyboard.7", GLFW.GLFW_KEY_7);
    public static final InputKeyImpl KEY_8 = register("key.keyboard.8", GLFW.GLFW_KEY_8);
    public static final InputKeyImpl KEY_9 = register("key.keyboard.9", GLFW.GLFW_KEY_9);
    public static final InputKeyImpl KEY_A = register("key.keyboard.a", GLFW.GLFW_KEY_A);
    public static final InputKeyImpl KEY_B = register("key.keyboard.b", GLFW.GLFW_KEY_B);
    public static final InputKeyImpl KEY_C = register("key.keyboard.c", GLFW.GLFW_KEY_C);
    public static final InputKeyImpl KEY_D = register("key.keyboard.d", GLFW.GLFW_KEY_D);
    public static final InputKeyImpl KEY_E = register("key.keyboard.e", GLFW.GLFW_KEY_E);
    public static final InputKeyImpl KEY_F = register("key.keyboard.f", GLFW.GLFW_KEY_F);
    public static final InputKeyImpl KEY_G = register("key.keyboard.g", GLFW.GLFW_KEY_G);
    public static final InputKeyImpl KEY_H = register("key.keyboard.h", GLFW.GLFW_KEY_H);
    public static final InputKeyImpl KEY_I = register("key.keyboard.i", GLFW.GLFW_KEY_I);
    public static final InputKeyImpl KEY_J = register("key.keyboard.j", GLFW.GLFW_KEY_J);
    public static final InputKeyImpl KEY_K = register("key.keyboard.k", GLFW.GLFW_KEY_K);
    public static final InputKeyImpl KEY_L = register("key.keyboard.l", GLFW.GLFW_KEY_L);
    public static final InputKeyImpl KEY_M = register("key.keyboard.m", GLFW.GLFW_KEY_M);
    public static final InputKeyImpl KEY_N = register("key.keyboard.n", GLFW.GLFW_KEY_N);
    public static final InputKeyImpl KEY_O = register("key.keyboard.o", GLFW.GLFW_KEY_O);
    public static final InputKeyImpl KEY_P = register("key.keyboard.p", GLFW.GLFW_KEY_P);
    public static final InputKeyImpl KEY_Q = register("key.keyboard.q", GLFW.GLFW_KEY_Q);
    public static final InputKeyImpl KEY_R = register("key.keyboard.r", GLFW.GLFW_KEY_R);
    public static final InputKeyImpl KEY_S = register("key.keyboard.s", GLFW.GLFW_KEY_S);
    public static final InputKeyImpl KEY_T = register("key.keyboard.t", GLFW.GLFW_KEY_T);
    public static final InputKeyImpl KEY_U = register("key.keyboard.u", GLFW.GLFW_KEY_U);
    public static final InputKeyImpl KEY_V = register("key.keyboard.v", GLFW.GLFW_KEY_V);
    public static final InputKeyImpl KEY_W = register("key.keyboard.w", GLFW.GLFW_KEY_W);
    public static final InputKeyImpl KEY_X = register("key.keyboard.x", GLFW.GLFW_KEY_X);
    public static final InputKeyImpl KEY_Y = register("key.keyboard.y", GLFW.GLFW_KEY_Y);
    public static final InputKeyImpl KEY_Z = register("key.keyboard.z", GLFW.GLFW_KEY_Z);
    public static final InputKeyImpl KEY_F1 = register("key.keyboard.f1", GLFW.GLFW_KEY_F1);
    public static final InputKeyImpl KEY_F2 = register("key.keyboard.f2", GLFW.GLFW_KEY_F2);
    public static final InputKeyImpl KEY_F3 = register("key.keyboard.f3", GLFW.GLFW_KEY_F3);
    public static final InputKeyImpl KEY_F4 = register("key.keyboard.f4", GLFW.GLFW_KEY_F4);
    public static final InputKeyImpl KEY_F5 = register("key.keyboard.f5", GLFW.GLFW_KEY_F5);
    public static final InputKeyImpl KEY_F6 = register("key.keyboard.f6", GLFW.GLFW_KEY_F6);
    public static final InputKeyImpl KEY_F7 = register("key.keyboard.f7", GLFW.GLFW_KEY_F7);
    public static final InputKeyImpl KEY_F8 = register("key.keyboard.f8", GLFW.GLFW_KEY_F8);
    public static final InputKeyImpl KEY_F9 = register("key.keyboard.f9", GLFW.GLFW_KEY_F9);
    public static final InputKeyImpl KEY_F10 = register("key.keyboard.f10", GLFW.GLFW_KEY_F10);
    public static final InputKeyImpl KEY_F11 = register("key.keyboard.f11", GLFW.GLFW_KEY_F11);
    public static final InputKeyImpl KEY_F12 = register("key.keyboard.f12", GLFW.GLFW_KEY_F12);
    public static final InputKeyImpl KEY_F13 = register("key.keyboard.f13", GLFW.GLFW_KEY_F13);
    public static final InputKeyImpl KEY_F14 = register("key.keyboard.f14", GLFW.GLFW_KEY_F14);
    public static final InputKeyImpl KEY_F15 = register("key.keyboard.f15", GLFW.GLFW_KEY_F15);
    public static final InputKeyImpl KEY_F16 = register("key.keyboard.f16", GLFW.GLFW_KEY_F16);
    public static final InputKeyImpl KEY_F17 = register("key.keyboard.f17", GLFW.GLFW_KEY_F17);
    public static final InputKeyImpl KEY_F18 = register("key.keyboard.f18", GLFW.GLFW_KEY_F18);
    public static final InputKeyImpl KEY_F19 = register("key.keyboard.f19", GLFW.GLFW_KEY_F19);
    public static final InputKeyImpl KEY_F20 = register("key.keyboard.f20", GLFW.GLFW_KEY_F20);
    public static final InputKeyImpl KEY_F21 = register("key.keyboard.f21", GLFW.GLFW_KEY_F21);
    public static final InputKeyImpl KEY_F22 = register("key.keyboard.f22", GLFW.GLFW_KEY_F22);
    public static final InputKeyImpl KEY_F23 = register("key.keyboard.f23", GLFW.GLFW_KEY_F23);
    public static final InputKeyImpl KEY_F24 = register("key.keyboard.f24", GLFW.GLFW_KEY_F24);
    public static final InputKeyImpl KEY_F25 = register("key.keyboard.f25", GLFW.GLFW_KEY_F25);
    public static final InputKeyImpl KEY_NUM_LOCK = register("key.keyboard.num.lock", GLFW.GLFW_KEY_NUM_LOCK);
    public static final InputKeyImpl KEY_KP_0 = register("key.keyboard.keypad.0", GLFW.GLFW_KEY_KP_0);
    public static final InputKeyImpl KEY_KP_1 = register("key.keyboard.keypad.1", GLFW.GLFW_KEY_KP_1);
    public static final InputKeyImpl KEY_KP_2 = register("key.keyboard.keypad.2", GLFW.GLFW_KEY_KP_2);
    public static final InputKeyImpl KEY_KP_3 = register("key.keyboard.keypad.3", GLFW.GLFW_KEY_KP_3);
    public static final InputKeyImpl KEY_KP_4 = register("key.keyboard.keypad.4", GLFW.GLFW_KEY_KP_4);
    public static final InputKeyImpl KEY_KP_5 = register("key.keyboard.keypad.5", GLFW.GLFW_KEY_KP_5);
    public static final InputKeyImpl KEY_KP_6 = register("key.keyboard.keypad.6", GLFW.GLFW_KEY_KP_6);
    public static final InputKeyImpl KEY_KP_7 = register("key.keyboard.keypad.7", GLFW.GLFW_KEY_KP_7);
    public static final InputKeyImpl KEY_KP_8 = register("key.keyboard.keypad.8", GLFW.GLFW_KEY_KP_8);
    public static final InputKeyImpl KEY_KP_9 = register("key.keyboard.keypad.9", GLFW.GLFW_KEY_KP_9);
    public static final InputKeyImpl KEY_KP_ADD = register("key.keyboard.keypad.add", GLFW.GLFW_KEY_KP_ADD);
    public static final InputKeyImpl KEY_KP_DECIMAL = register("key.keyboard.keypad.decimal", GLFW.GLFW_KEY_KP_DECIMAL);
    public static final InputKeyImpl KEY_KP_ENTER = register("key.keyboard.keypad.enter", GLFW.GLFW_KEY_KP_ENTER);
    public static final InputKeyImpl KEY_KP_EQUAL = register("key.keyboard.keypad.equal", GLFW.GLFW_KEY_KP_EQUAL);
    public static final InputKeyImpl KEY_KP_MULTIPLY = register("key.keyboard.keypad.multiply", GLFW.GLFW_KEY_KP_MULTIPLY);
    public static final InputKeyImpl KEY_KP_DIVIDE = register("key.keyboard.keypad.divide", GLFW.GLFW_KEY_KP_DIVIDE);
    public static final InputKeyImpl KEY_KP_SUBTRACT = register("key.keyboard.keypad.subtract", GLFW.GLFW_KEY_KP_SUBTRACT);
    public static final InputKeyImpl KEY_DOWN = register("key.keyboard.down", GLFW.GLFW_KEY_DOWN);
    public static final InputKeyImpl KEY_LEFT = register("key.keyboard.left", GLFW.GLFW_KEY_LEFT);
    public static final InputKeyImpl KEY_RIGHT = register("key.keyboard.right", GLFW.GLFW_KEY_RIGHT);
    public static final InputKeyImpl KEY_UP = register("key.keyboard.up", GLFW.GLFW_KEY_UP);
    public static final InputKeyImpl KEY_APOSTROPHE = register("key.keyboard.apostrophe", GLFW.GLFW_KEY_APOSTROPHE);
    public static final InputKeyImpl KEY_BACKSLASH = register("key.keyboard.backslash", GLFW.GLFW_KEY_BACKSLASH);
    public static final InputKeyImpl KEY_COMMA = register("key.keyboard.comma", GLFW.GLFW_KEY_COMMA);
    public static final InputKeyImpl KEY_EQUAL = register("key.keyboard.equal", GLFW.GLFW_KEY_EQUAL);
    public static final InputKeyImpl KEY_GRAVE_ACCENT = register("key.keyboard.grave.accent", GLFW.GLFW_KEY_GRAVE_ACCENT);
    public static final InputKeyImpl KEY_LEFT_BRACKET = register("key.keyboard.left.bracket", GLFW.GLFW_KEY_LEFT_BRACKET);
    public static final InputKeyImpl KEY_MINUS = register("key.keyboard.minus", GLFW.GLFW_KEY_MINUS);
    public static final InputKeyImpl KEY_PERIOD = register("key.keyboard.period", GLFW.GLFW_KEY_PERIOD);
    public static final InputKeyImpl KEY_RIGHT_BRACKET = register("key.keyboard.right.bracket", GLFW.GLFW_KEY_RIGHT_BRACKET);
    public static final InputKeyImpl KEY_SEMICOLON = register("key.keyboard.semicolon", GLFW.GLFW_KEY_SEMICOLON);
    public static final InputKeyImpl KEY_SLASH = register("key.keyboard.slash", GLFW.GLFW_KEY_SLASH);
    public static final InputKeyImpl KEY_SPACE = register("key.keyboard.space", GLFW.GLFW_KEY_SPACE);
    public static final InputKeyImpl KEY_TAB = register("key.keyboard.tab", GLFW.GLFW_KEY_TAB);
    public static final InputKeyImpl KEY_LEFT_ALT = register("key.keyboard.left.alt", GLFW.GLFW_KEY_LEFT_ALT);
    public static final InputKeyImpl KEY_LEFT_CONTROL = register("key.keyboard.left.control", GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final InputKeyImpl KEY_LEFT_SHIFT = register("key.keyboard.left.shift", GLFW.GLFW_KEY_LEFT_SHIFT);
    public static final InputKeyImpl KEY_LEFT_SUPER = register("key.keyboard.left.win", GLFW.GLFW_KEY_LEFT_SUPER);
    public static final InputKeyImpl KEY_RIGHT_ALT = register("key.keyboard.right.alt", GLFW.GLFW_KEY_RIGHT_ALT);
    public static final InputKeyImpl KEY_RIGHT_CONTROL = register("key.keyboard.right.control", GLFW.GLFW_KEY_RIGHT_CONTROL);
    public static final InputKeyImpl KEY_RIGHT_SHIFT = register("key.keyboard.right.shift", GLFW.GLFW_KEY_RIGHT_SHIFT);
    public static final InputKeyImpl KEY_RIGHT_SUPER = register("key.keyboard.right.win", GLFW.GLFW_KEY_RIGHT_SUPER);
    public static final InputKeyImpl KEY_ENTER = register("key.keyboard.enter", GLFW.GLFW_KEY_ENTER);
    public static final InputKeyImpl KEY_ESCAPE = register("key.keyboard.escape", GLFW.GLFW_KEY_ESCAPE);
    public static final InputKeyImpl KEY_BACKSPACE = register("key.keyboard.backspace", GLFW.GLFW_KEY_BACKSPACE);
    public static final InputKeyImpl KEY_DELETE = register("key.keyboard.delete", GLFW.GLFW_KEY_DELETE);
    public static final InputKeyImpl KEY_END = register("key.keyboard.end", GLFW.GLFW_KEY_END);
    public static final InputKeyImpl KEY_HOME = register("key.keyboard.home", GLFW.GLFW_KEY_HOME);
    public static final InputKeyImpl KEY_INSERT = register("key.keyboard.insert", GLFW.GLFW_KEY_INSERT);
    public static final InputKeyImpl KEY_PAGE_DOWN = register("key.keyboard.page.down", GLFW.GLFW_KEY_PAGE_DOWN);
    public static final InputKeyImpl KEY_PAGE_UP = register("key.keyboard.page.up", GLFW.GLFW_KEY_PAGE_UP);
    public static final InputKeyImpl KEY_CAPS_LOCK = register("key.keyboard.caps.lock", GLFW.GLFW_KEY_CAPS_LOCK);
    public static final InputKeyImpl KEY_PAUSE = register("key.keyboard.pause", GLFW.GLFW_KEY_PAUSE);
    public static final InputKeyImpl KEY_SCROLL_LOCK = register("key.keyboard.scroll.lock", GLFW.GLFW_KEY_SCROLL_LOCK);
    public static final InputKeyImpl KEY_MENU = register("key.keyboard.menu", GLFW.GLFW_KEY_MENU);
    public static final InputKeyImpl KEY_PRINT_SCREEN = register("key.keyboard.print.screen", GLFW.GLFW_KEY_PRINT_SCREEN);
    public static final InputKeyImpl KEY_WORLD_1 = register("key.keyboard.world.1", GLFW.GLFW_KEY_WORLD_1);
    public static final InputKeyImpl KEY_WORLD_2 = register("key.keyboard.world.2", GLFW.GLFW_KEY_WORLD_2);

    public static final InputKeyImpl UNKNOWN = register("key.keyboard.unknown", InputConstants.UNKNOWN);

    private final InputConstants.Key key;

    public InputKeyImpl(InputConstants.Key key) {
        this.key = key;
    }

    public static InputKeyImpl get(String name) {
        return NAMED_KEYS.getOrDefault(name, UNKNOWN);
    }

    public static InputKeyImpl register(String name, int keyCode) {
        return register(name, InputConstants.getKey(keyCode, -1));
    }

    public static InputKeyImpl register(String name, InputConstants.Key key) {
        InputKeyImpl impl = new InputKeyImpl(key);
        NAMED_KEYS.put(name, impl);
        return impl;
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
