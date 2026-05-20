package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.compat.builder.AbstractMenuTypeBuilder;
import moe.plushie.armourers_workshop.compat.fabric.core.AbstractFabricMenuType;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Available("[16, )")
public class AbstractFabricMenuTypeBuilder<T extends AbstractContainerMenu, V> extends AbstractMenuTypeBuilder<T, V> {

    public AbstractFabricMenuTypeBuilder(IMenuProvider<T, V> factory, IMenuSerializer<V> serializer) {
        super(factory, serializer);
    }

    @Override
    public IMenuType<T> build(OpenResourceKey registryName) {
        return new AbstractFabricMenuType<>(factory, serializer);
    }
}
