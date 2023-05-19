package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricCommonNativeProvider;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;

public class CommonNativeManagerImpl implements AbstractFabricCommonNativeProvider {

    public static final CommonNativeManagerImpl INSTANCE = new CommonNativeManagerImpl();

    public static CommonNativeProvider getProvider() {
        return INSTANCE;
    }
}
