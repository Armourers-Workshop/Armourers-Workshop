package riskyken.armourersWorkshop.common.items;

import net.minecraft.inventory.EntityEquipmentSlot;

public class ItemArmourContainer extends AbstractModItemArmour {

    public ItemArmourContainer(String name, EntityEquipmentSlot armourType) {
        super(name, ArmorMaterial.IRON, armourType, false);
    }
}
