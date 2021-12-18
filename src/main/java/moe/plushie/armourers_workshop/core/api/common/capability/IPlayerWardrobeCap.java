package moe.plushie.armourers_workshop.core.api.common.capability;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinType;
import net.minecraft.inventory.EquipmentSlotType;

public interface IPlayerWardrobeCap extends IWardrobeCap {
    
    public boolean getArmourOverride(EquipmentSlotType equipmentSlot);
    
    public void setArmourOverride(EquipmentSlotType equipmentSlot, boolean override);
    
    public int getUnlockedSlotsForSkinType(ISkinType skinType);
    
    public void setUnlockedSlotsForSkinType(ISkinType skinType, int count);
    
    public int getMaxSlotsForSkinType(ISkinType skinType);
}
