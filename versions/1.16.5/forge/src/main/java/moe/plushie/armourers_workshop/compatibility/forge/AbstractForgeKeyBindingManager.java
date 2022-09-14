package moe.plushie.armourers_workshop.compatibility.forge;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public interface AbstractForgeKeyBindingManager {

    static void register(KeyMapping mapping) {
        ClientRegistry.registerKeyBinding(mapping);
    }
}
