package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.compat.builder.AbstractMenuTypeBuilder;
import moe.plushie.armourers_workshop.compat.fabric.AbstractFabricMenuType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Available("[1.16, )")
public class AbstractFabricMenuTypeBuilder<T extends AbstractContainerMenu, V> extends AbstractMenuTypeBuilder<T, V> {

    public AbstractFabricMenuTypeBuilder(IMenuProvider<T, V> factory, IMenuSerializer<V> serializer) {
        super(factory, serializer);
    }

    @Override
    public IMenuType<T> build(OpenResourceLocation registryName) {
        return new AbstractFabricMenuType<>(factory, serializer);
    }
}
