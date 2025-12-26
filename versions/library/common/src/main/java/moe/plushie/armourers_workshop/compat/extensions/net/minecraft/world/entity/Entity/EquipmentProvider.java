package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.core.data.SlotManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class EquipmentProvider {

    public static void getHandSlots(@This Entity entity, Consumer<ItemStack> handler) {
        SlotManager.getProviders().forEach(provider -> {
            var slots = provider.getHandSlots(entity);
            if (slots != null) {
                slots.forEach(handler);
            }
        });
    }

    public static void getArmorSlots(@This Entity entity, Consumer<ItemStack> handler) {
        SlotManager.getProviders().forEach(provider -> {
            var slots = provider.getArmorSlots(entity);
            if (slots != null) {
                slots.forEach(handler);
            }
        });
    }
}
