package moe.plushie.armourers_workshop.init.platform.forge;

import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeNetwork;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class NetworkManagerImpl {

    public static NetworkManager.Dispatcher createDispatcher(ResourceLocation registryName, String version) {
        return new AbstractForgeNetwork.Dispatcher(registryName, version);
    }

    public static NetworkManager.Distributors createDistributors() {
        return new AbstractForgeNetwork.Distributors();
    }
}
