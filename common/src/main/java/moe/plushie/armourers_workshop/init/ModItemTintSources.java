package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.core.item.tintsource.DefaultItemTintSource;
import moe.plushie.armourers_workshop.init.platform.Platform;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ModItemTintSources {

    public static final IRegistryHolder<ItemTintSourceType<DefaultItemTintSource>> DYE = normal(DefaultItemTintSource.MAP_CODEC).build("dye");

    private static <T extends ItemTintSource> IRegistryBuilder<ItemTintSourceType<T>> normal(IDataMapCodec<T> codec) {
        return Platform.get().client().builder().itemTintSource(codec);
    }

    public static void init() {
    }
}
