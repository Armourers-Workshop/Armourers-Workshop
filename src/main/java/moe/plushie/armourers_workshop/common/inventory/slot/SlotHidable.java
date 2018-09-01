package moe.plushie.armourers_workshop.common.inventory.slot;

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
            this.xPos = x;
            this.yPos = y;
        }
    }

    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (this.visible) {
            this.xPos = xDisplayPositionNormal;
            this.yPos = yDisplayPositionNormal;
        } else {
            this.xPos = 100000;
            this.yPos = 100000;
        }
    }
}
