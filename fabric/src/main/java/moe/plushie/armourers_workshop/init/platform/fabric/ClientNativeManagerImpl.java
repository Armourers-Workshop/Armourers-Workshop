package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricClientNativeProvider;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;

@SuppressWarnings("unused")
public class ClientNativeManagerImpl implements AbstractFabricClientNativeProvider {

    public static final ClientNativeManagerImpl INSTANCE = new ClientNativeManagerImpl();

    public static ClientNativeProvider getProvider() {
        return INSTANCE;
    }
}
