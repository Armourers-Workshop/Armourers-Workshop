package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.compat.api.level.BiomeAccessor;
import moe.plushie.armourers_workshop.compat.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.BiomeSelector;

public class BiomeSelectorImpl implements BiomeSelector {

    protected BiomeAccessor biome;

    public BiomeSelectorImpl apply(BiomeAccessor biome) {
        this.biome = biome;
        return this;
    }

    @Override
    public boolean hasTag(String name) {
        return AbstractRegistryManager.hasBiomeTag(biome, name);
    }
}
