package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientNativeProvider;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;

@SuppressWarnings("unused")
public class ClientNativeManagerImpl implements AbstractForgeClientNativeProvider {

    public static final ClientNativeManagerImpl INSTANCE = new ClientNativeManagerImpl();

    public static ClientNativeProvider getProvider() {
        return INSTANCE;
    }
}
