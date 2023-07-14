package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.impl.KeyboardManagerImpl;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;

import java.util.ArrayList;

public class Shortcut {

    private static final ImmutableMap<String, Condition> TESTER = ImmutableMap.<String, Condition>builder().put("key.keyboard.control", (k1, k2) -> KeyboardManagerImpl.hasControlDown()).put("key.keyboard.shift", (k1, k2) -> KeyboardManagerImpl.hasShiftDown()).put("key.keyboard.alt", (k1, k2) -> KeyboardManagerImpl.hasAltDown()).build();

    private final ArrayList<Condition> conditions;

    private Shortcut(ArrayList<Condition> conditions) {
        this.conditions = conditions;
    }

    public static Shortcut of(String... keyNames) {
        ArrayList<Condition> conditions = new ArrayList<>();
        for (String keyName : keyNames) {
            Condition condition = TESTER.get(keyName);
            if (condition != null) {
                conditions.add(condition);
                continue;
            }
            InputConstants.Key key = InputConstants.getKey(keyName);
            conditions.add((i, j) -> {
                if (i == InputConstants.UNKNOWN.getValue()) {
                    return key.getType() == InputConstants.Type.SCANCODE && key.getValue() == j;
                }
                return key.getType() == InputConstants.Type.KEYSYM && key.getValue() == i;
            });
        }
        return new Shortcut(conditions);
    }

    public boolean matches(int key1, int key2) {
        for (Condition condition : conditions) {
            if (!condition.test(key1, key2)) {
                return false;
            }
        }
        return true;
    }

    public interface Condition {
        boolean test(int key1, int key2);
    }
}
