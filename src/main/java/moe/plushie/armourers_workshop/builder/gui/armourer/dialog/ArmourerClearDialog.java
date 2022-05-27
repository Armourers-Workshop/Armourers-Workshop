package moe.plushie.armourers_workshop.builder.gui.armourer.dialog;

import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWComboBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWConfirmDialog;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class ArmourerClearDialog extends AWConfirmDialog {

    AWComboBox partComboBox;

    AWCheckBox blockCheckBox;
    AWCheckBox paintCheckBox;
    AWCheckBox markersCheckBox;

    final ArrayList<ISkinPartType> partTypes;

    public ArmourerClearDialog(ArrayList<ISkinPartType> partTypes, ITextComponent title) {
        super(title);
        this.partTypes = partTypes;
        this.imageWidth = 240;
        this.imageHeight = 140;
    }

    @Override
    protected void init() {
        super.init();

        int bottom = confirmButton.y - 4;
        this.blockCheckBox = new AWCheckBox(confirmButton.x + 1, bottom - 22, 9, 9, getText("clearBlocks"), true, Objects::hash);
        this.paintCheckBox = new AWCheckBox(confirmButton.x + 1, bottom - 11, 9, 9, getText("clearPaint"), true, Objects::hash);
        this.markersCheckBox = new AWCheckBox(confirmButton.x + 1, bottom - 33, 9, 9, getText("clearMarkers"), true, Objects::hash);

        this.partComboBox = new AWComboBox(confirmButton.x + 1, topPos + 20, 80, 14, getItems(partTypes), 0, Objects::hash);

        this.addButton(blockCheckBox);
        this.addButton(paintCheckBox);
        this.addButton(markersCheckBox);

        this.addButton(partComboBox);
    }

    public boolean isClearBlocks() {
        return blockCheckBox == null || blockCheckBox.isSelected();
    }

    public boolean isClearPaints() {
        return paintCheckBox == null || paintCheckBox.isSelected();
    }

    public boolean isClearMarkers() {
        return markersCheckBox == null || markersCheckBox.isSelected();
    }

    public ISkinPartType getSelectedPartType() {
        if (partTypes != null && partComboBox != null && partComboBox.getSelectedIndex() < partTypes.size()) {
            return partTypes.get(partComboBox.getSelectedIndex());
        }
        return SkinPartTypes.UNKNOWN;
    }

    private ArrayList<AWComboBox.ComboItem> getItems(ArrayList<ISkinPartType> partTypes) {
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        for (ISkinPartType partType : partTypes) {
            ITextComponent title;
            if (partType != SkinPartTypes.UNKNOWN) {
                title = TranslateUtils.Name.of(partType);
            } else {
                title = new StringTextComponent("*");
            }
            items.add(new AWComboBox.ComboItem(title));
        }
        return items;
    }

    private ITextComponent getText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.clear" + "." + key);
    }
}
