package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonNativeProvider;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;

public class CommonNativeManagerImpl implements AbstractForgeCommonNativeProvider {

    public static final CommonNativeManagerImpl INSTANCE = new CommonNativeManagerImpl();

    public static CommonNativeProvider getProvider() {
        return INSTANCE;
    }
}
