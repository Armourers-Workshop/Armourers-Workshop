package moe.plushie.armourers_workshop.builder.gui.armourer.dialog;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWComboBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWConfirmDialog;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class ArmourerCopyDialog extends AWConfirmDialog {

    AWComboBox sourcePartComboBox;
    AWComboBox destinationPartComboBox;

    AWCheckBox mirrorCheckBox;
    AWCheckBox paintCheckBox;

    final ArrayList<ISkinPartType> partTypes;

    public ArmourerCopyDialog(ArrayList<ISkinPartType> partTypes, ITextComponent title) {
        super(title);
        this.partTypes = partTypes;
        this.imageWidth = 240;
        this.imageHeight = 140;
    }

    @Override
    protected void init() {
        super.init();

        int right = cancelButton.x + cancelButton.getWidth();
        int bottom = confirmButton.y - 4;

        this.mirrorCheckBox = new AWCheckBox(confirmButton.x + 1, bottom - 22, 9, 9, getText("mirror"), false, Objects::hash);
        this.paintCheckBox = new AWCheckBox(confirmButton.x + 1, bottom - 11, 9, 9, getText("copyPaint"), false, Objects::hash);

        this.sourcePartComboBox = new AWComboBox(confirmButton.x + 1, topPos + 35, 100, 14, getItems(partTypes), 0, Objects::hash);
        this.destinationPartComboBox = new AWComboBox(right - 100 - 1, topPos + 35, 100, 14, getItems(partTypes), 0, Objects::hash);

        this.addButton(mirrorCheckBox);
        this.addButton(paintCheckBox);

        this.addButton(sourcePartComboBox);
        this.addButton(destinationPartComboBox);

        this.addLabel(sourcePartComboBox.x, sourcePartComboBox.y - 10, 100, 9, getText("srcPart"));
        this.addLabel(destinationPartComboBox.x, destinationPartComboBox.y - 10, 100, 9, getText("desPart"));
    }

    public ISkinPartType getSourcePartType() {
        if (partTypes != null && sourcePartComboBox != null && sourcePartComboBox.getSelectedIndex() < partTypes.size()) {
            return partTypes.get(sourcePartComboBox.getSelectedIndex());
        }
        return SkinPartTypes.UNKNOWN;
    }

    public ISkinPartType getDestinationPartType() {
        if (partTypes != null && destinationPartComboBox != null && destinationPartComboBox.getSelectedIndex() < partTypes.size()) {
            return partTypes.get(destinationPartComboBox.getSelectedIndex());
        }
        return SkinPartTypes.UNKNOWN;
    }

    public boolean isMirror() {
        return mirrorCheckBox == null || mirrorCheckBox.isSelected();
    }

    public boolean isCopyPaintData() {
        return paintCheckBox == null || paintCheckBox.isSelected();
    }

    private ArrayList<AWComboBox.ComboItem> getItems(ArrayList<ISkinPartType> partTypes) {
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        for (ISkinPartType partType : partTypes) {
            ITextComponent title = TranslateUtils.Name.of(partType);
            items.add(new AWComboBox.ComboItem(title));
        }
        return items;
    }

    private ITextComponent getText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.copy" + "." + key);
    }
}
