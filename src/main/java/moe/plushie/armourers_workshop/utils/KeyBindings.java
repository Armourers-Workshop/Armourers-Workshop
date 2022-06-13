package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.init.common.AWCore;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class KeyBindings {

//    public static final String CATEGORY = "keys." + AWCore.getModId().toLowerCase() + ":category";
//    public static final String WARDROBE = "keys." + AWCore.getModId().toLowerCase() + ".open-wardrobe";
//    public static final String UNDO = "keys." + AWCore.getModId().toLowerCase() + ".undo";

    public static KeyBinding UNDO_KEY = register("undo", "key.keyboard.z");
    public static KeyBinding OPEN_WARDROBE_KEY = register("open-wardrobe", "key.keyboard.p");

    public static KeyBinding register(String name, String key) {
        String nameKey = "keys." + AWCore.getModId() + "." + name;
        String categoryKey = "keys." + AWCore.getModId() + ".category";
        InputMappings.Input input = InputMappings.getKey(key);
        return new OnceKeyBinding(nameKey, KeyConflictContext.IN_GAME, KeyModifier.CONTROL, input, categoryKey);
    }

    public static class OnceKeyBinding extends KeyBinding {

        // Once consumed, must need to release the key to reset this flags.
        private boolean canConsumeClick = true;

        public OnceKeyBinding(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, InputMappings.Input keyCode, String category) {
            super(description, keyConflictContext, keyModifier, keyCode, category);
        }

        @Override
        public boolean consumeClick() {
            if (canConsumeClick && isDown()) {
                canConsumeClick = false;
                return true;
            }
            return false;
        }

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (!isDown) {
                canConsumeClick = true;
            }
        }
    }
}
