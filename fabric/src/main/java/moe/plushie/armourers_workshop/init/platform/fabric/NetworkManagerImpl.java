package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.compat.fabric.AbstractFabricNetwork;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;

@SuppressWarnings("unused")
public class NetworkManagerImpl extends NetworkManager {

    @Override
    public NetworkManager.Dispatcher createDispatcher(OpenResourceLocation registryName, String version) {
        return new AbstractFabricNetwork.Dispatcher(registryName, version);
    }

    @Override
    public NetworkManager.Distributors createDistributors() {
        return new AbstractFabricNetwork.Distributors();
    }

}
