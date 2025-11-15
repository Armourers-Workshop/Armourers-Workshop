package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.LivingEntity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractEquipmentSlot;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, )")
@Extension
public class EquippableAPI {

    public static ItemStack getItemBySlot(@This LivingEntity entity, OpenEquipmentSlot equipmentSlot) {
        return entity.getItemBySlot(AbstractEquipmentSlot.unwrap(equipmentSlot));
    }

    public static void setItemSlot(@This LivingEntity entity, OpenEquipmentSlot equipmentSlot, ItemStack itemStack) {
        entity.setItemSlot(AbstractEquipmentSlot.unwrap(equipmentSlot), itemStack);
    }
}
