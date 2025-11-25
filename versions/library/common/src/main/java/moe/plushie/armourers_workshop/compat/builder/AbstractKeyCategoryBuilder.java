package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public abstract class AbstractKeyCategoryBuilder<T extends IKeyCategory> {

    protected final String name;

    public AbstractKeyCategoryBuilder(String name) {
        this.name = name;
    }

    public T build(OpenResourceLocation registryName) {
        var category = create(registryName);
        return Objects.unsafeCast(category);
    }

    protected abstract IKeyCategory create(OpenResourceLocation name);
}
