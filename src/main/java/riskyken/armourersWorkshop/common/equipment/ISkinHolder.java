package riskyken.armourersWorkshop.common.equipment;

import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;

public interface ISkinHolder {
    public ItemStack makeStackForEquipment(CustomEquipmentItemData armourItemData);
}
