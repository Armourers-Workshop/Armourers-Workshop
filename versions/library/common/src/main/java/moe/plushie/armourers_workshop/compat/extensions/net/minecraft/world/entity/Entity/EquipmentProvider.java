package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.core.data.EntityEquipmentManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class EquipmentProvider {

    public static void getHandSlots(@This Entity entity, Consumer<ItemStack> handler) {
        EntityEquipmentManager.getHandSlots(entity, handler);
    }

    public static void getArmorSlots(@This Entity entity, Consumer<ItemStack> handler) {
        EntityEquipmentManager.getArmorSlots(entity, handler);
    }
}
