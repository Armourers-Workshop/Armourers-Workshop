package moe.plushie.armourers_workshop.compat.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

@Available("[1.16, 1.22)")
public class AbstractFabricKeyMapping extends KeyMapping {

    public AbstractFabricKeyMapping(String string, InputConstants.Type type, int i, String string2) {
        super(string, type, i, string2);
    }

    public static void register(String key, KeyMapping keyMapping) {
        KeyBindingHelper.registerKeyBinding(keyMapping);
    }
}
