package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricNetwork;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;

@SuppressWarnings("unused")
public class NetworkManagerImpl extends NetworkManager {

    @Override
    public NetworkManager.Dispatcher createDispatcher(OpenResourceKey registryName, String version) {
        return new AbstractFabricNetwork.Dispatcher(registryName, version);
    }

    @Override
    public NetworkManager.Distributors createDistributors() {
        return new AbstractFabricNetwork.Distributors();
    }

}
