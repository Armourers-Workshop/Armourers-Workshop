package moe.plushie.armourers_workshop.core.utils;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class Keybindings {
    public static final String CATEGORY = "keys." + SkinCore.getModId().toLowerCase() + ":category";
    public static final String WARDROBE = "keys." + SkinCore.getModId().toLowerCase() + ".open-wardrobe";
    public static final String UNDO = "keys." + SkinCore.getModId().toLowerCase() + ".undo";

    public static KeyBinding KEY_UNDO = new KeyBinding(UNDO, KeyConflictContext.IN_GAME, KeyModifier.CONTROL, InputMappings.getKey("key.keyboard.z"), CATEGORY);

    public static KeyBinding OPEN_WARDROBE = new KeyBinding(WARDROBE, KeyConflictContext.IN_GAME, InputMappings.getKey("key.keyboard.p"), CATEGORY);

}
