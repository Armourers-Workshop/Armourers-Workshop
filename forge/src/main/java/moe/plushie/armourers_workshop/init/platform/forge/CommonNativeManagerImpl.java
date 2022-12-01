package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonNativeImpl;
import moe.plushie.armourers_workshop.init.provider.CommonNativeProvider;

public class CommonNativeManagerImpl {

    public static final AbstractForgeCommonNativeImpl INSTANCE = new AbstractForgeCommonNativeImpl();

    public static CommonNativeProvider getProvider() {
        return INSTANCE;
    }

}
