package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AbstractMenuTypeBuilder<T extends AbstractContainerMenu, V> {

    protected final IMenuProvider<T, V> factory;
    protected final IMenuSerializer<V> serializer;

    public AbstractMenuTypeBuilder(IMenuProvider<T, V> factory, IMenuSerializer<V> serializer) {
        this.factory = factory;
        this.serializer = serializer;
    }

    public abstract IMenuType<T> build(OpenResourceLocation registryName);
}
