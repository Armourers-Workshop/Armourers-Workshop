package moe.plushie.armourers_workshop.builder.data.properties;

import moe.plushie.armourers_workshop.api.data.IDataProperty;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class VectorProperty implements IDataProperty<Vector3f> {

    private Vector3f vectorValue;

    @Override
    public void set(Vector3f value) {
        this.vectorValue = value;
    }

    @Override
    public Vector3f get() {
        return vectorValue;
    }

    public FloatProperty x() {
        return field(Vector3f::setX, Vector3f::getX);
    }

    public FloatProperty y() {
        return field(Vector3f::setY, Vector3f::getY);
    }

    public FloatProperty z() {
        return field(Vector3f::setZ, Vector3f::getZ);
    }

    private FloatProperty field(BiConsumer<Vector3f, Float> setter, Function<Vector3f, Float> getter) {
        return new FloatProperty() {
            @Override
            public void set(Float value) {
                Vector3f newValue = vectorValue.copy();
                setter.accept(newValue, value);
                vectorValue = newValue;
            }

            @Override
            public @Nullable Float get() {
                if (vectorValue != null) {
                    return getter.apply(vectorValue);
                }
                return null;
            }
        };
    }
}
