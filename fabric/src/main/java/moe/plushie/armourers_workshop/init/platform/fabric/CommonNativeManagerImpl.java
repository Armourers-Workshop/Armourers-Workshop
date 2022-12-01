package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricCommonNativeImpl;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;

public class CommonNativeManagerImpl {

    public static final AbstractFabricCommonNativeImpl INSTANCE = new AbstractFabricCommonNativeImpl();

    public static CommonNativeProvider getProvider() {
        return INSTANCE;
    }

}
