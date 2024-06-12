package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeNetwork;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;

@SuppressWarnings("unused")
public class NetworkManagerImpl {

    public static NetworkManager.Dispatcher createDispatcher(IResourceLocation registryName, String version) {
        return new AbstractForgeNetwork.Dispatcher(registryName, version);
    }

    public static NetworkManager.Distributors createDistributors() {
        return new AbstractForgeNetwork.Distributors();
    }
}
