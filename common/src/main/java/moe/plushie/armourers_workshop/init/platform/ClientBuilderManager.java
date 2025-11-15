package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.client.IBlockTintSource;
import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.registry.IBlockTintSourceBuilder;
import moe.plushie.armourers_workshop.api.registry.IItemTintSourceBuilder;
import moe.plushie.armourers_workshop.api.registry.ISpecialModelRendererBuilder;

public abstract class ClientBuilderManager {

    private static final ClientBuilderManager INSTANCE = PlatformLoader.load(ClientBuilderManager.class);

    public static ClientBuilderManager getInstance() {
        return INSTANCE;
    }

    public abstract <T extends IItemTintSource> IItemTintSourceBuilder<T> createItemTintSourceBuilder(IDataMapCodec<T> codec);

    public abstract <T extends IBlockTintSource> IBlockTintSourceBuilder<T> createBlockTintSourceBuilder(IDataMapCodec<T> codec);

    public abstract <T extends ISpecialModelRenderer<?>> ISpecialModelRendererBuilder<T> createSpecialModelRendererBuilder(IDataMapCodec<T> codec);
}
