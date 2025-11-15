package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBlockTintSource;
import moe.plushie.armourers_workshop.api.client.IBlockTintSourceType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IBlockTintSourceBuilder;
import moe.plushie.armourers_workshop.core.block.tintsource.DefaultBlockTintSource;
import moe.plushie.armourers_workshop.init.platform.ClientBuilderManager;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ModBlockTintSources {

    public static final IRegistryHolder<IBlockTintSourceType<DefaultBlockTintSource>> DYE = normal(DefaultBlockTintSource.MAP_CODEC).build("dye");

    private static <T extends IBlockTintSource> IBlockTintSourceBuilder<T> normal(IDataMapCodec<T> codec) {
        return ClientBuilderManager.getInstance().createBlockTintSourceBuilder(codec);
    }

    public static void init() {
    }
}
