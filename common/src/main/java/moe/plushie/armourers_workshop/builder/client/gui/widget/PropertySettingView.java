package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.client.gui.widget.InventoryBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.VerticalStackView;
import moe.plushie.armourers_workshop.core.math.OpenVector3d;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.Collection;

public abstract class PropertySettingView extends UIView {

    protected final VerticalStackView stackView = new VerticalStackView(CGRect.ZERO);

    protected UICheckBox blockBed;
    protected UICheckBox blockEnderInventory;
    protected UICheckBox blockInventory;

    protected UILabel inventoryTitle;
    protected UILabel inventorySlot;
    protected InventoryBox inventoryBox;

    protected EntitySizeBox entitySizeBox;

    public PropertySettingView(CGRect rect, Collection<SkinProperty<?>> properties) {
        super(rect);
        this.stackView.setFrame(bounds());
        this.stackView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        this.addSubview(stackView);
        for (var property : properties) {
            if (property.defaultValue() instanceof Boolean) {
                addCheckBox(Objects.unsafeCast(property));
            }
            if (property == SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH) {
                addEntitySize();
            }
            if (property == SkinProperty.BLOCK_INVENTORY_WIDTH) {
                addInventoryBox();
            }
        }
        this.stackView.sizeToFit();
        this.setFrame(new CGRect(rect.x, rect.y, rect.width, stackView.frame().maxY()));
        this.resolveConflicts();
    }

    public void beginEditing() {
    }

    public abstract <T> void putValue(SkinProperty<T> property, T value);

    public abstract <T> T getValue(SkinProperty<T> property);

    public void endEditing() {
    }

    protected void addCheckBox(SkinProperty<Boolean> property) {
        var checkBox = new UICheckBox(new CGRect(0, 0, 80, 10));
        checkBox.setTitle(getDisplayText(property.key()));
        checkBox.setTitleColor(UIColor.WHITE);
        checkBox.setTitleColor(UIColor.GRAY, UIControl.State.DISABLED);
        checkBox.setSelected(getValue(property));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            UICheckBox checkBox1 = Objects.unsafeCast(c);
            self.beginEditing();
            self.putValue(property, checkBox1.isSelected());
            self.resolveConflicts();
            self.endEditing();
        });
        stackView.addArrangedSubview(checkBox);
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
    }

    protected void addEntitySize() {
        var contentView = new UIView(new CGRect(0, 0, 80, 48));

        entitySizeBox = new EntitySizeBox(new CGRect(12, 0, 56, 48));
        entitySizeBox.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        entitySizeBox.setHidden(true);
        entitySizeBox.addEditingObserver(flag -> {
            if (flag) {
                beginEditing();
            } else {
                endEditing();
            }
        });
        entitySizeBox.addObserver(value -> {
            putValue(SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH, value.x());
            putValue(SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT, value.y());
            putValue(SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT, value.z());
        });
        contentView.addSubview(entitySizeBox);

        stackView.addArrangedSubview(contentView);
    }

    protected void addInventoryBox() {
        var contentView = new UIView(new CGRect(0, 0, 80, 6 * 10));

        inventoryTitle = new UILabel(new CGRect(0, -2, 80, 9));
        inventoryTitle.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        inventoryTitle.setText(getDisplayText("label.inventorySize"));
        contentView.addSubview(inventoryTitle);

        inventorySlot = new UILabel(new CGRect(0, 6, 80, 9));
        inventorySlot.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        contentView.addSubview(inventorySlot);

        inventoryBox = new InventoryBox(new CGRect(0, 0, 9 * 10, 6 * 10));
        inventoryBox.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleBottomMargin);
        inventoryBox.addTarget(this, UIControl.Event.VALUE_CHANGED, PropertySettingView::setInventorySize);
        contentView.addSubview(inventoryBox);

        stackView.addArrangedSubview(contentView);
    }

    private void setInventorySize(UIControl sender) {
        var offset = inventoryBox.offset();
        var width = (int) (offset.x / 10) + 1;
        var height = (int) (offset.y / 10) + 1;
        beginEditing();
        putValue(SkinProperty.BLOCK_INVENTORY_WIDTH, width);
        putValue(SkinProperty.BLOCK_INVENTORY_HEIGHT, height);
        endEditing();
        resolveInventorySlots();
    }

    private void resolveEntitySize() {
        if (entitySizeBox == null) {
            return;
        }
        var isEnabled = getValue(SkinProperty.OVERRIDE_ENTITY_SIZE);
        var width = getValue(SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH);
        var height = getValue(SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT);
        var eyeHeight = getValue(SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT);
        entitySizeBox.setValue(new OpenVector3d(width, height, eyeHeight));
        entitySizeBox.setHidden(!isEnabled);
    }

    private void resolveInventorySlots() {
        if (inventorySlot == null) {
            return;
        }
        var isEnabled = getValue(SkinProperty.BLOCK_INVENTORY) && !getValue(SkinProperty.BLOCK_ENDER_INVENTORY);
        var width = getValue(SkinProperty.BLOCK_INVENTORY_WIDTH);
        var height = getValue(SkinProperty.BLOCK_INVENTORY_HEIGHT);
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
        resolveEntitySize();
        resolveInventorySlots();
    }

    protected NSString getDisplayText(String key, Object... objects) {
        return NSString.localizedString("armourer.skinSettings." + key, objects);
    }
}
