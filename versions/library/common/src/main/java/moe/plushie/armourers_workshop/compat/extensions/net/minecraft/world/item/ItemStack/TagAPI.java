package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.item.ItemStack;

import moe.plushie.armourers_workshop.api.common.ITagKey;
import moe.plushie.armourers_workshop.compat.core.item.AbstractItemTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class TagAPI {

    public static boolean is(@This ItemStack itemStack, ITagKey<Item> tag) {
        return ((AbstractItemTag) tag).test(itemStack);
    }
}
