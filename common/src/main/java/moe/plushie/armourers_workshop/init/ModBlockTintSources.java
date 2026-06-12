package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.core.block.tintsource.DefaultBlockTintSource;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSource;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSourceType;
import moe.plushie.armourers_workshop.init.platform.Platform;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ModBlockTintSources {

    public static final IRegistryHolder<BlockTintSourceType<DefaultBlockTintSource>> DYE = normal(DefaultBlockTintSource.MAP_CODEC).build("dye");

    private static <T extends BlockTintSource> IRegistryBuilder<BlockTintSourceType<T>> normal(IDataMapCodec<T> codec) {
        return Platform.get().client().builder().blockTintSource(codec);
    }

    public static void init() {
    }
}
