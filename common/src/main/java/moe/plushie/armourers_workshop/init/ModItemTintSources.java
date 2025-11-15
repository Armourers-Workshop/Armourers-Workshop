package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.client.IItemTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IItemTintSourceBuilder;
import moe.plushie.armourers_workshop.core.item.tintsource.DefaultItemTintSource;
import moe.plushie.armourers_workshop.init.platform.ClientBuilderManager;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ModItemTintSources {

    public static final IRegistryHolder<IItemTintSourceType<DefaultItemTintSource>> DYE = normal(DefaultItemTintSource.MAP_CODEC).build("dye");

    private static <T extends IItemTintSource> IItemTintSourceBuilder<T> normal(IDataMapCodec<T> codec) {
        return ClientBuilderManager.getInstance().createItemTintSourceBuilder(codec);
    }

    public static void init() {
    }
}
