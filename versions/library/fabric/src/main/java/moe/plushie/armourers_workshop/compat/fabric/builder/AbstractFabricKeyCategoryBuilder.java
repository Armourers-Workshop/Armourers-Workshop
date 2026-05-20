package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.compat.builder.AbstractKeyCategoryBuilder;
import moe.plushie.armourers_workshop.compat.fabric.client.AbstractFabricKeyCategory;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractFabricKeyCategoryBuilder<T extends IKeyCategory> extends AbstractKeyCategoryBuilder<T> {

    public AbstractFabricKeyCategoryBuilder(String name) {
        super(name);
    }

    @Override
    protected IKeyCategory create(OpenResourceKey name) {
        return new AbstractFabricKeyCategory(name);
    }
}
