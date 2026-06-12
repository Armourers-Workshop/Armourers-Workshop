package moe.plushie.armourers_workshop.init.platform.forge.runtime;

import moe.plushie.armourers_workshop.compat.forge.AbstractForgeRegistryAccessor;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonBuilderFactory;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonPlatform;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonRegistryAccessor;
import moe.plushie.armourers_workshop.init.platform.runtime.CommonRegistryFactory;

public class CommonPlatformImpl implements CommonPlatform {

    private final CommonBuilderPlatformImpl builder = new CommonBuilderPlatformImpl();
    private final CommonRegistryPlatformImpl registry = new CommonRegistryPlatformImpl();

    private final CommonRegistryAccessor registryAccess = new AbstractForgeRegistryAccessor();

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
