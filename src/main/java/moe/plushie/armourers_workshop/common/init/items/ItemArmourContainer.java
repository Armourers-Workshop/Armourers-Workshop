package moe.plushie.armourers_workshop.common.init.items;

import net.minecraft.inventory.EntityEquipmentSlot;

public class ItemArmourContainer extends AbstractModItemArmour {

    public ItemArmourContainer(String name, EntityEquipmentSlot armourType) {
        super(name, ArmorMaterial.IRON, armourType, false);
        setCreativeTab(null);
    }
}
