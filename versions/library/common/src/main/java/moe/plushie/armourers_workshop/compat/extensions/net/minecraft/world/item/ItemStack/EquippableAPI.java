package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.item.ItemStack;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractEquipmentSlot;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.21, 1.26)")
@Extension
public class EquippableAPI {

    public static OpenEquipmentSlot getEquipmentSlot(@This ItemStack itemStack) {
        var equipable = Equipable.get(itemStack);
        if (equipable != null) {
            return AbstractEquipmentSlot.wrap(equipable.getEquipmentSlot());
        }
        return OpenEquipmentSlot.MAINHAND;
    }
}
