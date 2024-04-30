package moe.plushie.armourers_workshop.init.platform.event.client;

import net.minecraft.client.KeyMapping;

public interface RegisterKeyMappingsEvent {

    void register(KeyMapping key);
}
