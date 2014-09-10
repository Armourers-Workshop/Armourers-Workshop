package riskyken.armourersWorkshop.client.settings;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import riskyken.armourersWorkshop.common.lib.LibKeyBindingNames;

public class Keybindings {
    
    public static KeyBinding openCustomArmourGui = new KeyBinding(LibKeyBindingNames.EQUIPMENT_WARDROBE, Keyboard.KEY_P, LibKeyBindingNames.CATEGORY);
}
