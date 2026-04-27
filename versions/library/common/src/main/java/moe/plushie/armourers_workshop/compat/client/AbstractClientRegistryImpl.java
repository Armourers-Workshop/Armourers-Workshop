package moe.plushie.armourers_workshop.compat.client;

import moe.plushie.armourers_workshop.api.annotation.Available;

@Available("[16, )")
public abstract class AbstractClientRegistryImpl {

    protected AbstractClientRegistryImpl() {
        this.init();
        this.load();
    }

    protected abstract void init();

    protected abstract void load();
}
