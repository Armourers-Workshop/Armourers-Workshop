package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotMannequin extends Slot {

    private MannequinSlotType slotType;
    
    public SlotMannequin(MannequinSlotType slotType, IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.slotType = slotType;
    }
    
    @Override
    public boolean isItemValid(ItemStack stack) {
        return true;
    }
    
    public enum MannequinSlotType {
        HEAD,
        CHEST,
        LEGS,
        SKIRT,
        FEET,
        RIGHT_HAND,
        LEFT_HAND;

        public static MannequinSlotType getOrdinal(int i) {
            return MannequinSlotType.values()[i];
        }
    }
}
