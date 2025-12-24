package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.player.Player;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.16, 1.26)")
public class SlotDirectAccessor {

    public static void setItemSlotDirect(@This Player player, OpenEquipmentSlot equipmentSlot, ItemStack itemStack) {
        var inventory = player.getInventory();
        inventory.armor.set(equipmentSlot.index(), itemStack);
    }
}
