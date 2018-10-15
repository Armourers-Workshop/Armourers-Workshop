package moe.plushie.armourers_workshop.common.capability.wardrobe.player;

import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;
import net.minecraft.inventory.EntityEquipmentSlot;

public interface IPlayerWardrobeCap extends IWardrobeCap {
    
    public boolean getArmourOverride(EntityEquipmentSlot equipmentSlot);
    
    public void setArmourOverride(EntityEquipmentSlot equipmentSlot, boolean override);
}
