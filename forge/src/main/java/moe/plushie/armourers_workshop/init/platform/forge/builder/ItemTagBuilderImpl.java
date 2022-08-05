package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.builder.IItemTagBuilder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class ItemTagBuilderImpl<T extends Item> implements IItemTagBuilder<T> {

    public ItemTagBuilderImpl() {
    }

    @Override
    public Tag<T> build(String name) {
        return ObjectUtils.unsafeCast(ItemTags.createOptional(ArmourersWorkshop.getResource(name)));
    }
}
