package com.apple.library.uikit;

import com.apple.library.foundation.NSString;
import com.apple.library.impl.KeyboardManagerImpl;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class UIMenuItem {

    private static final ImmutableMap<String, Predicate<UIEvent>> TESTER = ImmutableMap.<String, Predicate<UIEvent>>builder()
            .put("key.keyboard.control", event -> KeyboardManagerImpl.hasControlDown())
            .put("key.keyboard.shift", event -> KeyboardManagerImpl.hasShiftDown())
            .put("key.keyboard.alt", event -> KeyboardManagerImpl.hasAltDown())
            .build();

    private final NSString title;
    private final Collection<Runnable> actions;
    private final Collection<Predicate<UIEvent>> conditions;

    private final int group;
    private final boolean isEnabled;

    private UIMenuItem(NSString title, Collection<Runnable> actions, Collection<Predicate<UIEvent>> conditions, int group, boolean isEnabled) {
        this.title = title;
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public int group() {
        return group;
    }

    public static class Builder {

        private int group;
        private boolean isEnabled = true;

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
            return new UIMenuItem(title, actions, conditions, group, isEnabled);
        }

        private Builder addCondition(UIEvent.Type eventType, String... keyNames) {
            conditions.add(event -> event.type() == eventType);
            for (String keyName : keyNames) {
                Predicate<UIEvent> condition = TESTER.get(keyName);
                if (condition != null) {
                    conditions.add(condition);
                    continue;
                }
                BiPredicate<Integer, Integer> keyHandler = KeyboardManagerImpl.getKeyHandler(keyName);
                conditions.add(event -> keyHandler.test(event.key(), event.keyModifier()));
            }
            return this;
        }
    }
}
