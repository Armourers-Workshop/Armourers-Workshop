package moe.plushie.armourers_workshop.common.items;

import moe.plushie.armourers_workshop.common.lib.LibItemNames;

public class ItemArmourContainerItem extends AbstractModItem {

    public ItemArmourContainerItem() {
        super(LibItemNames.ARMOUR_CONTAINER);
        setMaxStackSize(64);
    }
}
