package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.compat.core.data.serializer.AbstractDataSerializerContext;
import org.jetbrains.annotations.Nullable;

public class SerializationContext extends AbstractDataSerializerContext {

    public static final SerializationContext EMPTY = new SerializationContext(null);

    private SerializationContext(@Nullable Object context) {
        super(context);
    }

    public static SerializationContext from(@Nullable Object context) {
        if (context != null) {
            return new SerializationContext(context);
        }
        return EMPTY;
    }
}
