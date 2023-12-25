package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.client.gui.widget.InventoryBox;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

import java.util.Collection;

public abstract class PropertySettingView extends UIView {

    private float cursorY = 0;

    protected UICheckBox blockBed;
    protected UICheckBox blockEnderInventory;
    protected UICheckBox blockInventory;

    protected UILabel inventoryTitle;
    protected UILabel inventorySlot;
    protected InventoryBox inventoryBox;


    public PropertySettingView(CGRect rect, Collection<ISkinProperty<?>> properties) {
        super(rect);
        for (ISkinProperty<?> property : properties) {
            if (property.getDefaultValue() instanceof Boolean) {
                addCheckBox(ObjectUtils.unsafeCast(property));
            }
            if (property == SkinProperty.BLOCK_INVENTORY_WIDTH) {
                addInventoryBox();
            }
        }
        this.setFrame(new CGRect(rect.x, rect.y, rect.width, cursorY));
        this.resolveConflicts();
        this.resolveSlots();
    }

    public void beginEditing() {
    }

    public abstract <T> void putValue(ISkinProperty<T> property, T value);

    public abstract <T> T getValue(ISkinProperty<T> property);

    public void endEditing() {
    }

    protected void addCheckBox(ISkinProperty<Boolean> property) {
        UICheckBox checkBox = new UICheckBox(new CGRect(0, cursorY, bounds().width, 10));
        checkBox.setTitle(getDisplayText(property.getKey()));
        checkBox.setTitleColor(UIColor.WHITE);
        checkBox.setTitleColor(UIColor.GRAY, UIControl.State.DISABLED);
        checkBox.setSelected(getValue(property));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            UICheckBox checkBox1 = ObjectUtils.unsafeCast(c);
            self.beginEditing();
            self.putValue(property, checkBox1.isSelected());
            self.resolveConflicts();
            self.endEditing();
        });
        addSubview(checkBox);
        if (property == SkinProperty.BLOCK_BED) {
            checkBox.setFrame(checkBox.frame().insetBy(0, 4, 0, 0));
            blockBed = checkBox;
        }
        if (property == SkinProperty.BLOCK_ENDER_INVENTORY) {
            blockEnderInventory = checkBox;
        }
        if (property == SkinProperty.BLOCK_INVENTORY) {
            blockInventory = checkBox;
        }
        cursorY = checkBox.frame().getMaxY() + 2;
    }

    protected void addInventoryBox() {
        inventoryTitle = new UILabel(new CGRect(0, cursorY - 2, bounds().width, 9));
        inventorySlot = new UILabel(new CGRect(0, cursorY + 6, bounds().width, 9));
        inventoryBox = new InventoryBox(new CGRect(0, cursorY, 9 * 10, 6 * 10));

        inventoryTitle.setText(getDisplayText("label.inventorySize"));
        inventoryBox.addTarget(this, UIControl.Event.VALUE_CHANGED, PropertySettingView::setInventorySize);

        addSubview(inventoryTitle);
        addSubview(inventorySlot);
        addSubview(inventoryBox);

        cursorY = inventoryBox.frame().getMaxY() + 2;
    }

    private void setInventorySize(UIControl sender) {
        CGPoint offset = inventoryBox.getOffset();
        int width = (int) (offset.x / 10) + 1;
        int height = (int) (offset.y / 10) + 1;
        beginEditing();
        putValue(SkinProperty.BLOCK_INVENTORY_WIDTH, width);
        putValue(SkinProperty.BLOCK_INVENTORY_HEIGHT, height);
        endEditing();
        resolveSlots();
    }

    private void resolveSlots() {
        if (inventorySlot == null) {
            return;
        }
        boolean isEnabled = getValue(SkinProperty.BLOCK_INVENTORY) && !getValue(SkinProperty.BLOCK_ENDER_INVENTORY);
        int width = getValue(SkinProperty.BLOCK_INVENTORY_WIDTH);
        int height = getValue(SkinProperty.BLOCK_INVENTORY_HEIGHT);
        inventorySlot.setText(getDisplayText("label.inventorySlots", width * height, width, height));
        inventoryBox.setOffset(new CGPoint(Math.max(width - 1, 0) * 10, Math.max(height - 1, 0) * 10));
        inventoryTitle.setHidden(!isEnabled);
        inventorySlot.setHidden(!isEnabled);
        inventoryBox.setHidden(!isEnabled);
    }

    private void resolveConflicts() {
        if (blockBed != null) {
            blockBed.setEnabled(getValue(SkinProperty.BLOCK_MULTIBLOCK));
            if (!blockBed.isEnabled() && blockBed.isSelected()) {
                blockBed.setSelected(false);
                putValue(SkinProperty.BLOCK_BED, false);
            }
        }
        if (blockEnderInventory != null) {
            blockEnderInventory.setEnabled(!getValue(SkinProperty.BLOCK_INVENTORY));
            if (!blockEnderInventory.isEnabled() && blockEnderInventory.isSelected()) {
                blockEnderInventory.setSelected(false);
                putValue(SkinProperty.BLOCK_ENDER_INVENTORY, false);
            }
        }
        if (blockInventory != null) {
            blockInventory.setEnabled(!getValue(SkinProperty.BLOCK_ENDER_INVENTORY));
            if (!blockInventory.isEnabled() && blockInventory.isSelected()) {
                blockInventory.setSelected(false);
                putValue(SkinProperty.BLOCK_INVENTORY, false);
            }
        }
        resolveSlots();
    }

    protected NSString getDisplayText(String key, Object... objects) {
        return NSString.localizedString("armourer.skinSettings." + key, objects);
    }
}
