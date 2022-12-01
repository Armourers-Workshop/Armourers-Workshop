package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricClientNativeImpl;
import moe.plushie.armourers_workshop.init.provider.ClientNativeFactory;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;

@SuppressWarnings("unused")
public class ClientNativeManagerImpl {

    public static final AbstractFabricClientNativeImpl INSTANCE = new AbstractFabricClientNativeImpl();

    public static ClientNativeFactory getFactory() {
        return INSTANCE;
    }

    public static ClientNativeProvider getProvider() {
        return INSTANCE;
    }
}
