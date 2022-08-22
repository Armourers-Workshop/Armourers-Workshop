package moe.plushie.armourers_workshop.builder.client.gui.armourer.panel;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import moe.plushie.armourers_workshop.core.client.gui.widget.InventoryBox;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class ArmourerBlockSkinPanel extends ArmourerBaseSkinPanel {

    protected UICheckBox blockBed;
    protected UICheckBox blockEnderInventory;
    protected UICheckBox blockInventory;

    protected UILabel inventoryTitle;
    protected UILabel inventorySlot;
    protected InventoryBox inventoryBox;

    public ArmourerBlockSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    public void apply() {
        this.resolveConflicts();
        this.resolveSlots();
        super.apply();
    }

    public void applySlot(UIControl sender) {
        CGPoint offset = inventoryBox.getOffset();
        int width = (offset.x / 10) + 1;
        int height = (offset.y / 10) + 1;
        skinProperties.put(SkinProperty.BLOCK_INVENTORY_WIDTH, width);
        skinProperties.put(SkinProperty.BLOCK_INVENTORY_HEIGHT, height);
        apply();
    }

    @Override
    public void init() {
        super.init();
        addCheckBox(0, 0, SkinProperty.BLOCK_GLOWING);
        addCheckBox(0, 0, SkinProperty.BLOCK_LADDER);
        addCheckBox(0, 0, SkinProperty.BLOCK_NO_COLLISION);
        addCheckBox(0, 0, SkinProperty.BLOCK_SEAT);
        addCheckBox(0, 0, SkinProperty.BLOCK_MULTIBLOCK);
        blockBed = addCheckBox(12, 0, SkinProperty.BLOCK_BED);
        blockEnderInventory = addCheckBox(0, 0, SkinProperty.BLOCK_ENDER_INVENTORY);
        blockInventory = addCheckBox(0, 0, SkinProperty.BLOCK_INVENTORY);
        inventoryTitle = addLabel(0, -2, getDisplayText("label.inventorySize"));
        inventorySlot = addLabel(0, -1, new NSString(""));
        inventoryBox = addInventoryBox(0, -2);

        resolveConflicts();
        resolveSlots();
    }

    protected InventoryBox addInventoryBox(int x, int y) {
        InventoryBox box = new InventoryBox(new CGRect(cursorX + x, cursorY + y, 9 * 10, 6 * 10));
        box.addTarget(this, UIControl.Event.VALUE_CHANGED, ArmourerBlockSkinPanel::applySlot);
        addSubview(box);
        return box;
    }

    private void resolveSlots() {
        if (inventorySlot == null) {
            return;
        }
        boolean isEnabled = blockInventory.isEnabled() && blockInventory.isSelected();
        int width = skinProperties.get(SkinProperty.BLOCK_INVENTORY_WIDTH);
        int height = skinProperties.get(SkinProperty.BLOCK_INVENTORY_HEIGHT);
        inventorySlot.setText(getDisplayText("label.inventorySlots", width * height, width, height));
        inventoryBox.setOffset(new CGPoint(Math.max(width - 1, 0) * 10, Math.max(height - 1, 0) * 10));
        inventoryTitle.setHidden(!isEnabled);
        inventorySlot.setHidden(!isEnabled);
        inventoryBox.setHidden(!isEnabled);
    }

    private void resolveConflicts() {
        // apply enable status
        blockBed.setEnabled(skinProperties.get(SkinProperty.BLOCK_MULTIBLOCK));
        blockEnderInventory.setEnabled(!skinProperties.get(SkinProperty.BLOCK_INVENTORY));
        blockInventory.setEnabled(!skinProperties.get(SkinProperty.BLOCK_ENDER_INVENTORY));
        // apply properties
        if (!blockBed.isEnabled() && blockBed.isSelected()) {
            blockBed.setSelected(false);
            skinProperties.remove(SkinProperty.BLOCK_BED);
        }
        if (!blockEnderInventory.isEnabled() && blockEnderInventory.isSelected()) {
            blockEnderInventory.setSelected(false);
            skinProperties.remove(SkinProperty.BLOCK_ENDER_INVENTORY);
        }
        if (!blockInventory.isEnabled() && blockInventory.isSelected()) {
            blockInventory.setSelected(false);
            skinProperties.remove(SkinProperty.BLOCK_INVENTORY);
        }
    }
}
