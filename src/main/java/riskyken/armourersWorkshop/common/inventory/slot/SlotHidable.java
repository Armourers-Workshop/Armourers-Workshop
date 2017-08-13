package riskyken.armourersWorkshop.common.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotHidable extends Slot {

    private boolean visible;
    private int xDisplayPositionNormal;
    private int yDisplayPositionNormal;
    
    public SlotHidable(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
        this.visible = true;
        this.xDisplayPositionNormal = xDisplayPosition;
        this.yDisplayPositionNormal = yDisplayPosition;
    }
    
    public void setDisplayPosition(int x, int y) {
        this.xDisplayPositionNormal = x;
        this.yDisplayPositionNormal = y;
        if (visible) {
            this.xDisplayPosition = x;
            this.yDisplayPosition = y;
        }
    }

    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (this.visible) {
            this.xDisplayPosition = xDisplayPositionNormal;
            this.yDisplayPosition = yDisplayPositionNormal;
        } else {
            this.xDisplayPosition = 100000;
            this.yDisplayPosition = 100000;
        }
    }
}
