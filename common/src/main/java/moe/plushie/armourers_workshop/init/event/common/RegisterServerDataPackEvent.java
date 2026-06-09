package moe.plushie.armourers_workshop.init.event.common;

import moe.plushie.armourers_workshop.core.utils.OpenResourceManager;

public interface RegisterServerDataPackEvent {

    void register(OpenResourceManager.PreparableReloadListener reloadListener);
}
