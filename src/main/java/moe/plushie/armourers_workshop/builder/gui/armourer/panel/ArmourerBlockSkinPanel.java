package moe.plushie.armourers_workshop.builder.gui.armourer.panel;

import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWInventoryBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ArmourerBlockSkinPanel extends ArmourerBaseSkinPanel {

    protected AWCheckBox blockBed;
    protected AWCheckBox blockEnderInventory;
    protected AWCheckBox blockInventory;

    protected AWLabel inventoryTitle;
    protected AWLabel inventorySlot;
    protected AWInventoryBox inventoryBox;

    public ArmourerBlockSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    public void apply() {
        this.resolveConflicts();
        this.resolveSlots();
        super.apply();
    }

    public void applySlot(Button sender) {
        Point offset = inventoryBox.getOffset();
        int width = (offset.x / 10) + 1;
        int height = (offset.y / 10) + 1;
        skinProperties.put(SkinProperty.BLOCK_INVENTORY_WIDTH, width);
        skinProperties.put(SkinProperty.BLOCK_INVENTORY_HEIGHT, height);
        apply();
    }

    @Override
    protected void init() {
        super.init();
        this.addCheckBox(0, 0, 9, 9, SkinProperty.BLOCK_GLOWING);
        this.addCheckBox(0, 0, 9, 9, SkinProperty.BLOCK_LADDER);
        this.addCheckBox(0, 0, 9, 9, SkinProperty.BLOCK_NO_COLLISION);
        this.addCheckBox(0, 0, 9, 9, SkinProperty.BLOCK_SEAT);
        this.addCheckBox(0, 0, 9, 9, SkinProperty.BLOCK_MULTIBLOCK);
        this.blockBed = addCheckBox(12, 0, 9, 9, SkinProperty.BLOCK_BED);
        this.blockEnderInventory = addCheckBox(0, 0, 9, 9, SkinProperty.BLOCK_ENDER_INVENTORY);
        this.blockInventory = addCheckBox(0, 0, 9, 9, SkinProperty.BLOCK_INVENTORY);
        this.inventoryTitle = addLabel(0, -2, width, 9, getDisplayText("label.inventorySize"));
        this.inventorySlot = addLabel(0, -1, width, 9, StringTextComponent.EMPTY);
        this.inventoryBox = addInventoryBox(0, -2);

        this.resolveConflicts();
        this.resolveSlots();
    }

    protected AWInventoryBox addInventoryBox(int x, int y) {
        AWInventoryBox box = new AWInventoryBox(cursorX + x, cursorY + y, 9 * 10, 6 * 10, 176, 0, RenderUtils.TEX_ARMOURER, this::applySlot);
        addButton(box);
        return box;
    }

    private void resolveSlots() {
        if (inventorySlot == null) {
            return;
        }
        boolean isEnabled = blockInventory.isEnabled() && blockInventory.isSelected();
        int width = skinProperties.get(SkinProperty.BLOCK_INVENTORY_WIDTH);
        int height = skinProperties.get(SkinProperty.BLOCK_INVENTORY_HEIGHT);
        inventorySlot.setMessage(getDisplayText("label.inventorySlots", width * height, width, height));
        inventoryBox.setOffset(new Point(Math.max(width - 1, 0) * 10, Math.max(height - 1, 0) * 10));
        inventoryTitle.visible = isEnabled;
        inventorySlot.visible = isEnabled;
        inventoryBox.visible = isEnabled;
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
