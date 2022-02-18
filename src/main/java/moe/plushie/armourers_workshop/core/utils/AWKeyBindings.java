package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.AWCore;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class AWKeyBindings {

    public static final String CATEGORY = "keys." + AWCore.getModId().toLowerCase() + ":category";
    public static final String WARDROBE = "keys." + AWCore.getModId().toLowerCase() + ".open-wardrobe";
    public static final String UNDO = "keys." + AWCore.getModId().toLowerCase() + ".undo";

    public static KeyBinding UNDO_KEY = new KeyBinding(UNDO, KeyConflictContext.IN_GAME, KeyModifier.CONTROL, InputMappings.getKey("key.keyboard.z"), CATEGORY);

    public static KeyBinding OPEN_WARDROBE_KEY = new KeyBinding(WARDROBE, KeyConflictContext.IN_GAME, InputMappings.getKey("key.keyboard.p"), CATEGORY);

}
