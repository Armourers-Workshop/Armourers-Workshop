package moe.plushie.armourers_workshop.init.event.client;

import moe.plushie.armourers_workshop.core.utils.OpenResourceManager;

public interface RegisterClientDataPackEvent {

    void register(OpenResourceManager.PreparableReloadListener reloadListener);
}
