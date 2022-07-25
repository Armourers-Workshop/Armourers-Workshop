package moe.plushie.armourers_workshop.builder.item.tooloption;

import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;

public abstract class ToolProperty<T> implements IPaintingToolProperty<T> {

    protected final String name;
    protected final T defaultValue;

    public ToolProperty(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public T empty() {
        return defaultValue;
    }

    @Override
    public String getName() {
        return name;
    }
}
