package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientNativeImpl;
import moe.plushie.armourers_workshop.init.provider.ClientNativeFactory;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;

@SuppressWarnings("unused")
public class ClientNativeManagerImpl {

    public static final AbstractForgeClientNativeImpl INSTANCE = new AbstractForgeClientNativeImpl();

    public static ClientNativeFactory getFactory() {
        return INSTANCE;
    }

    public static ClientNativeProvider getProvider() {
        return INSTANCE;
    }
}
