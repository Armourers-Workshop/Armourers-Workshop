package moe.plushie.armourers_workshop.compatibility.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class AbstractFabricKeyMapping extends KeyMapping {

    public AbstractFabricKeyMapping(String string, InputConstants.Type type, int i, String string2) {
        super(string, type, i, string2);
    }

    public static void register(String key, KeyMapping keyMapping) {
        KeyBindingHelper.registerKeyBinding(keyMapping);
    }
}
