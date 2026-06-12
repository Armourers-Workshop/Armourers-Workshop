package moe.plushie.armourers_workshop.init.platform.fabric.runtime;

import moe.plushie.armourers_workshop.init.platform.runtime.ClientBuilderFactory;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientEventAccessor;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientPlatform;
import moe.plushie.armourers_workshop.init.platform.runtime.ClientRegistryFactory;

public class ClientPlatformImpl implements ClientPlatform {

    private final ClientBuilderFactoryImpl builder = new ClientBuilderFactoryImpl();
    private final ClientRegistryFactoryImpl registry = new ClientRegistryFactoryImpl();

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
