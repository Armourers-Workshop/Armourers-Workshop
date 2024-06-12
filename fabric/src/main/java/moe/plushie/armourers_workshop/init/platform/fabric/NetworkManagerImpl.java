package moe.plushie.armourers_workshop.init.platform.fabric;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricNetwork;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;

@SuppressWarnings("unused")
public class NetworkManagerImpl {


    public static NetworkManager.Dispatcher createDispatcher(IResourceLocation registryName, String version) {
        return new AbstractFabricNetwork.Dispatcher(registryName, version);
    }

    public static NetworkManager.Distributors createDistributors() {
        return new AbstractFabricNetwork.Distributors();
    }

}
