package moe.plushie.armourers_workshop.compat.core.data;

import moe.plushie.armourers_workshop.api.common.IEntityDataSerializer;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.network.syncher.EntityDataSerializer;

public class AbstractEntityDataSerializerBuilder<T> {

    protected final IEntityDataSerializer<T> serializer;

    public AbstractEntityDataSerializerBuilder(IEntityDataSerializer<T> serializer) {
        this.serializer = serializer;
    }

    public EntityDataSerializer<T> build(OpenResourceLocation key) {
        return new AbstractEntityDataSerializer.Proxy<>(serializer);
    }
}
