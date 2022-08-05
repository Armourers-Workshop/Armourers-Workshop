package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.builder.IItemTagBuilder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class ItemTagBuilderImpl<T extends Item> implements IItemTagBuilder<T> {

    public ItemTagBuilderImpl() {
    }

    @Override
    public Tag<T> build(String name) {
        return ObjectUtils.unsafeCast(TagRegistry.item(ArmourersWorkshop.getResource(name)));
    }
}
