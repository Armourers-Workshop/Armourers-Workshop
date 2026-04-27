package moe.plushie.armourers_workshop.compat.forge.builder;

import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.compat.builder.AbstractKeyCategoryBuilder;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeKeyCategory;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractForgeKeyCategoryBuilder<T extends IKeyCategory> extends AbstractKeyCategoryBuilder<T> {

    public AbstractForgeKeyCategoryBuilder(String name) {
        super(name);
    }

    @Override
    protected IKeyCategory create(OpenResourceKey name) {
        return new AbstractForgeKeyCategory(name);
    }
}
