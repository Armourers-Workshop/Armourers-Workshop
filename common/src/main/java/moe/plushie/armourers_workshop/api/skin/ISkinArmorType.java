package moe.plushie.armourers_workshop.api.skin;

import net.minecraft.world.entity.EquipmentSlot;

public interface ISkinArmorType extends ISkinEquipmentType {

    EquipmentSlot getSlotType();
}
