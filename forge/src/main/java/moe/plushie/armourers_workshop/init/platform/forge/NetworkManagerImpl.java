package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.compat.forge.AbstractForgeNetwork;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;

@SuppressWarnings("unused")
public class NetworkManagerImpl extends NetworkManager {

    @Override
    public NetworkManager.Dispatcher createDispatcher(OpenResourceKey registryName, String version) {
        return new AbstractForgeNetwork.Dispatcher(registryName, version);
    }

    @Override
    public NetworkManager.Distributors createDistributors() {
        return new AbstractForgeNetwork.Distributors();
    }
}
