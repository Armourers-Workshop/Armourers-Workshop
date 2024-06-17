package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.core.data.ItemStackProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class SlotProvider {

    public static Iterable<ItemStack> getOverrideArmorSlots(@This Entity entity) {
        return ItemStackProvider.getInstance().getArmorSlots(entity);
    }

    public static Iterable<ItemStack> getOverrideHandSlots(@This Entity entity) {
        return ItemStackProvider.getInstance().getHandSlots(entity);
    }

    public static Iterable<ItemStack> getOverrideSlots(@This Entity entity) {
        return ItemStackProvider.getInstance().getAllSlots(entity);
    }
}
