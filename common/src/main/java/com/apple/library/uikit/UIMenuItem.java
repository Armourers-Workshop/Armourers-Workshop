package com.apple.library.uikit;

import com.apple.library.foundation.NSString;
import com.apple.library.impl.InputKeyImpl;
import com.apple.library.impl.InputManagerImpl;
import com.apple.library.impl.StringImpl;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class UIMenuItem {

    private static final ImmutableMap<String, Predicate<UIEvent>> TESTER = ImmutableMap.<String, Predicate<UIEvent>>builder()
            .put("key.keyboard.control", event -> InputManagerImpl.hasControlDown())
            .put("key.keyboard.shift", event -> InputManagerImpl.hasShiftDown())
            .put("key.keyboard.alt", event -> InputManagerImpl.hasAltDown())
            .build();

    private static final ImmutableMap<String, Supplier<String>> TESTER_NAME = ImmutableMap.<String, Supplier<String>>builder()
            .put("key.keyboard.alt", () -> "ALT")
            .put("key.keyboard.shift", () -> "SHIFT")
            .put("key.keyboard.control", () -> {
                if (Minecraft.ON_OSX) {
                    return "CMD";
                }
                return "CTRL";
            })
            .build();


    private final NSString title;
    private final NSString key;
    private final Collection<Runnable> actions;
    private final Collection<Predicate<UIEvent>> conditions;

    private final int group;
    private final boolean isEnabled;

    private UIMenuItem(NSString title, NSString key, Collection<Runnable> actions, Collection<Predicate<UIEvent>> conditions, int group, boolean isEnabled) {
        this.title = title;
        this.key = key;
        this.actions = actions;
        this.conditions = conditions;
        this.group = group;
        this.isEnabled = isEnabled;
    }

    public static Builder of(String title) {
        return new Builder(new NSString(title));
    }

    public static Builder of(NSString title) {
        return new Builder(title);
    }

    public void perform(UIEvent event) {
        actions.forEach(Runnable::run);
    }

    public boolean test(UIEvent event) {
        return this.conditions.stream().allMatch(it -> it.test(event));
    }

    public NSString title() {
        return title;
    }

    public NSString key() {
        return key;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public int group() {
        return group;
    }

    public static class Builder {

        private int group;
        private boolean isEnabled = true;
        private NSString inputName;

        private final NSString title;
        private final ArrayList<Runnable> actions = new ArrayList<>();
        private final ArrayList<Predicate<UIEvent>> conditions = new ArrayList<>();

        public Builder(NSString title) {
            this.title = title;
        }

        public Builder keyUp(String... keyNames) {
            return addCondition(UIEvent.Type.KEY_UP, keyNames);
        }

        public Builder keyDown(String... keyNames) {
            return addCondition(UIEvent.Type.KEY_DOWN, keyNames);
        }

        public Builder execute(Runnable handler) {
            this.actions.add(handler);
            return this;
        }

        public Builder group(int group) {
            this.group = group;
            return this;
        }

        public Builder enable(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public UIMenuItem build() {
            return new UIMenuItem(title, inputName, actions, conditions, group, isEnabled);
        }

        private Builder addCondition(UIEvent.Type eventType, String... keyNames) {
            conditions.add(event -> event.type() == eventType);
            var names = new ArrayList<NSString>();
            for (var keyName : keyNames) {
                var condition = TESTER.get(keyName);
                if (condition != null) {
                    conditions.add(condition);
                    var name = TESTER_NAME.get(keyName);
                    if (name != null) {
                        names.add(new NSString(name.get()));
                    }
                } else {
                    var key = InputKeyImpl.get(keyName);
                    conditions.add(event -> key.test(event.key(), event.keyModifier()));
                    names.add(new NSString(key.getName()));
                }
            }
            inputName = StringImpl.join(names, " + ");
            return this;
        }
    }
}
