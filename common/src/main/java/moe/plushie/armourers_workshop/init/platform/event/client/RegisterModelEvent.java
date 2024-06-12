package moe.plushie.armourers_workshop.init.platform.event.client;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;

public interface RegisterModelEvent {

    void register(IResourceLocation registryName);
}
