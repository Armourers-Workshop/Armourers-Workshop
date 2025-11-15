package moe.plushie.armourers_workshop.compat.forge.builder;

import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.compat.builder.AbstractMenuTypeBuilder;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeMenuType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class AbstractForgeMenuTypeBuilder<T extends AbstractContainerMenu, V> extends AbstractMenuTypeBuilder<T, V> {

    public AbstractForgeMenuTypeBuilder(IMenuProvider<T, V> factory, IMenuSerializer<V> serializer) {
        super(factory, serializer);
    }

    @Override
    public IMenuType<T> build(OpenResourceLocation registryName) {
        return new AbstractForgeMenuType<>(factory, serializer);
    }
}
