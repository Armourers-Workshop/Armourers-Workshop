package riskyken.armourersWorkshop.common.equipment;

import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.equipment.data.EquipmentSkinTypeData;

public interface ISkinHolder {
    public ItemStack makeStackForEquipment(EquipmentSkinTypeData armourItemData);
}
