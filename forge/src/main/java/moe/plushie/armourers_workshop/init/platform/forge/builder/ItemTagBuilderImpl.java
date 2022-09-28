package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import moe.plushie.armourers_workshop.api.common.builder.IItemTagBuilder;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeItemTag;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.world.item.Item;

public class ItemTagBuilderImpl<T extends Item> implements IItemTagBuilder<T> {

    public ItemTagBuilderImpl() {
    }

    @Override
    public IItemTagKey<T> build(String name) {
        return AbstractForgeItemTag.create(ModConstants.key(name));
    }
}
