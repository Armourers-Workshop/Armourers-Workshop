package moe.plushie.armourers_workshop.core.utils;

import java.util.HashMap;

public class NamedClass {

    private static final HashMap<String, Class<?>> CLASSES = new HashMap<>();

    public static Class<?> forName(String name) {
        return CLASSES.get(name);
    }

    public static void define(String name, Class<?> clazz) {
        CLASSES.put(name, clazz);
    }
}
