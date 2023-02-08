package moe.plushie.armourers_workshop.api.common;

import com.google.common.collect.Iterables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public interface IItemStackProvider {

    Iterable<ItemStack> getArmorSlots(Entity entity);

    Iterable<ItemStack> getHandSlots(Entity entity);

    default Iterable<ItemStack> getAllSlots(Entity entity) {
        return Iterables.concat(getHandSlots(entity), getArmorSlots(entity));
    }
}
