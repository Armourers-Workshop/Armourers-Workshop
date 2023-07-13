package moe.plushie.armourers_workshop.builder.data.properties;

import moe.plushie.armourers_workshop.api.data.IDataProperty;

public class FloatProperty implements IDataProperty<Float> {

    float floatValue;

    @Override
    public void set(Float value) {
        this.floatValue = value;
    }

    @Override
    public Float get() {
        return floatValue;
    }
}
