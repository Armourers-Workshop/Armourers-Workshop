package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.AbstractClientRegistry;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public abstract class ClientResourceManager {

    private static final ClientResourceManager INSTANCE = PlatformLoader.load(ClientResourceManager.class);

    public static void init() {
        INSTANCE.createClientRegistry();
    }

    protected abstract AbstractClientRegistry createClientRegistry();
}
