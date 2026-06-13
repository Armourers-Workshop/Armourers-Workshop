package moe.plushie.armourers_workshop.init.platform.forge.runtime;

import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientPlatform;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientBuilderFactory;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientEventAccessor;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientPlatform;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientRegistryFactory;

public class ClientPlatformImpl extends AbstractForgeClientPlatform implements ClientPlatform {

    private final ClientBuilderPlatformImpl builder = new ClientBuilderPlatformImpl();
    private final ClientRegistryPlatformImpl registry = new ClientRegistryPlatformImpl();

    private final ClientEventAccessorImpl eventAccessor = new ClientEventAccessorImpl();

    @Override
    public ClientBuilderFactory builder() {
        return builder;
    }

    @Override
    public ClientRegistryFactory registry() {
        return registry;
    }

    @Override
    public ClientEventAccessor eventAccessor() {
        return eventAccessor;
    }
}
