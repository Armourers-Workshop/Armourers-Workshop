package moe.plushie.armourers_workshop.init.platform.fabric.runtime;

import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricRegistryAccessor;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonBuilderFactory;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonPlatform;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonRegistryAccessor;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonRegistryFactory;

public class CommonPlatformImpl implements CommonPlatform {

    private final CommonBuilderFactoryImpl builder = new CommonBuilderFactoryImpl();
    private final CommonRegistryFactoryImpl registry = new CommonRegistryFactoryImpl();

    private final CommonRegistryAccessor registryAccess = new AbstractFabricRegistryAccessor();

    @Override
    public CommonBuilderFactory builder() {
        return builder;
    }

    @Override
    public CommonRegistryFactory registry() {
        return registry;
    }

    @Override
    public CommonRegistryAccessor registryAccess() {
        return registryAccess;
    }
}
