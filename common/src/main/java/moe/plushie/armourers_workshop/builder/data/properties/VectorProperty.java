package moe.plushie.armourers_workshop.builder.data.properties;

import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class VectorProperty extends DataProperty<Vector3f> {

    private final DataProperty<Float> x = field(Vector3f::setX, Vector3f::getX);
    private final DataProperty<Float> y = field(Vector3f::setY, Vector3f::getY);
    private final DataProperty<Float> z = field(Vector3f::setZ, Vector3f::getZ);

    @Override
    public void set(Vector3f value) {
        super.set(value);
        this.x.set(value.getX());
        this.y.set(value.getY());
        this.z.set(value.getZ());
    }

    public DataProperty<Float> x() {
        return x;
    }

    public DataProperty<Float> y() {
        return y;
    }

    public DataProperty<Float> z() {
        return z;
    }

    private DataProperty<Float> field(BiConsumer<Vector3f, Float> setter, Function<Vector3f, Float> getter) {
        return new DataProperty<Float>() {

            @Override
            public void beginEditing() {
                super.beginEditing();
                VectorProperty.super.beginEditing();
            }

            @Override
            public void endEditing() {
                super.endEditing();
                VectorProperty.super.endEditing();
            }

            @Override
            public void set(Float value) {
                super.set(value);
                Vector3f newValue = VectorProperty.this.value.copy();
                setter.accept(newValue, value);
                VectorProperty.super.set(newValue);
            }

            @Override
            public Float get() {
                if (VectorProperty.this.value != null) {
                    return getter.apply(VectorProperty.this.value);
                }
                return 0.0f;
            }
        };
    }
}
