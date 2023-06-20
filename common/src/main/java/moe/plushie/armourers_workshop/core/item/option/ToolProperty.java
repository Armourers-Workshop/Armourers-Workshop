package moe.plushie.armourers_workshop.core.item.option;

import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;

public abstract class ToolProperty<T> implements IConfigurableToolProperty<T> {

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
