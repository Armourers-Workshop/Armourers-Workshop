package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.compat.core.menu.AbstractContainerSlot;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;

public class SkinSlot extends AbstractContainerSlot {

    protected final List<SkinSlotType> slotTypes;
    protected final List<OpenResourceLocation> slotTypeIcons;

    public SkinSlot(Container inventory, int index, int x, int y, SkinSlotType... slotTypes) {
        super(inventory, index, x, y);
        this.slotTypes = Collections.newList(slotTypes);
        this.slotTypeIcons = Collections.compactMap(slotTypes, SkinSlotType::icon);
    }

    public Collection<SkinSlotType> slotTypes() {
        return slotTypes;
    }

    @Override
    protected boolean abi$mayPlace(ItemStack itemStack) {
        // when slot type is not provide, we consider it is an unrestricted slot.
        if (!slotTypes.isEmpty() && !slotTypes.contains(SkinSlotType.byItem(itemStack))) {
            return false;
        }
        return container.canPlaceItem(index, itemStack);
    }

    @Override
    protected OpenResourceLocation abi$noItemIcon() {
        int size = slotTypeIcons.size();
        if (size > 0) {
            return slotTypeIcons.get((int) ((System.currentTimeMillis() / 1000L) % size));
        }
        return null;
    }
}
