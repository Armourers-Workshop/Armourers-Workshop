package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.compat.client.AbstractClientRegistry;
import moe.plushie.armourers_workshop.compat.fabric.client.AbstractFabricClientRegistry;
import moe.plushie.armourers_workshop.init.platform.ClientResourceManager;

@SuppressWarnings("unused")
public class ClientResourceManagerImpl extends ClientResourceManager {

    @Override
    protected AbstractClientRegistry createClientRegistry() {
        return new AbstractFabricClientRegistry();
    }
}
